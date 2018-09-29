# API documentation

## General architecture
The general idea behind spot is that each subsystem of your application should be implemented as one microservice. Those microservices communicate via REST or any other remote protocol.
There the whole framework is split into these three API blocks:
* Persistence
* Infrastructure
* REST endpoint

The infrastructure API provides facilities to implement the business logic. It handles serialization, conversion, internationalization, persistence etc. The CRUD REST endpoint offers a generic way to access the the stored domain objects. It can be used from other (spot) microservices.

### Spring configuration
The spot framework is build upon the Spring Boot framework. Every spot application has a main `Init` class that basically is just a **Spring application** class:

```java
@DependsOn("coreInit")
@SpringBootApplication
public class SampleInit extends ModuleInit {
    ...

    public static void main(final String[] args) throws Exception {
        ModuleInit.bootstrap(CoreInit.class, SampleInit.class, args);
    }
}
```

The `SampleInit` initializes a Spring parent context (based on the `CoreInit` application configuration) and sets the `SampleInit` application as its sibling:

```plantuml
    rectangle "Spring context" {
        component CoreInit #WhiteSmoke [
            <b>CoreInit application</b>
            (parent context)
        ]
        component SampleInit [
            <b>SampleInit</b>
            (custom child context)
        ] 

        CoreInit <-- SampleInit
    }
```

There are a few **differences to a standard Spring Boot setup** though.

#### Spring context setup
To setup the Spring contexts the `ModuleInit.bootstrap(...)` method is used, instead of the `SpringApplicationBuilder` class. Although this is just for convenience - it correctly sets up command line arguments handling and disables the banner logo.

#### Event handling
Event handling on the other hand, is a bit different. Event handler methods that are annotated with `@EventListener` are registered in the **parent context** instead of the current child context (via ``io.spotnext.core.infrastructure.support.spring.HierarchyAwareEventListenerMethodProcessor`). Therefore not only events published in the parent context are delivered to all child context, but also events fired in **any** child context are.
To publish such a global event in a child context, it is necessary to facilitate the `io.spotnext.core.infrastructure.service.EventService`:
```java
    // asynchronous event publishing
    eventService.multicastEvent(new CustomEvent()));

    //synchronous event publishing
    eventService.publishEvent(new CustomEvent()));
```

> Global event handling is only enabled for **annotation-based** event handlers

Spring's `ApplicationEventMulticaster` and `ApplicationEventPublisher` can still be used, although only using the "old-school" `ApplicationListener` interface.

#### Configuration
There is also a difference regarding how **configuration properties** are handled. Any property defined in the custom `application.properties` file **overwrite properties defined the parent context**.

All (merged) properties can be accessed using either:
* `@Value` annotation on fields
* `io.spotnext.core.infrastructure.service.ConfigurationService`'s methods like `getString()`

#### Summary
Aside from that, a spot application can be utilized as any regular Spring app.

### Persistene layer
The persistence layer is based on **Hibernate/JPA** - although its complexity is highly abstracted away. In contrast to a pure JPA approach entities are not created as Java classes and configured via XML or Annotations. Entities (or so called Item types) are generated based on type definitinos written in XML.
During building these classes are (bytecode-) transformed to JPA entities (adding annotations etc).

It's possible to defined different kinds of types:
* **Atom types** ("primitive" types)
* **Enums**
* **Beans** (POJOs)
* **Collections** (of Item types)
* **Item types** (JPA entities)

> For a more details head over to [Domain modelling](#domain-modelling)

To work with Item types neither Spring repositories nor direct usage of the JPA/Hibernate API is used. Instead `ModelService` and `QueryService` provide functionality to deal with the Item type lifecycle.

The main difference to a pure JPA/Hibernate approach is once a Hibernate session has been opened in thread, it stays open! This is also referred to as "**long running session**". The session has not to be opened manually, but instead it **automatically created when necessary**.
This approach has the advantage that you will never have to face the infamous "**LazyLoadException**", although by default all relations (collections, maps) are **lazy-loaded on access**.
The downside is that this can lead to the (also) infamous **N+1 problem**. Each lazy-loaded collection triggers an database query.
A typical scenario is an "export service" that generates CSV files out of all product items. This service might iterate over all product properties and causes a lot of queries. To circumvent this the persistence API offers a way to (programmatically) specify which properties (or all) should be eagerly loaded.

> For a more details head over to [Persistence operations](#persistence-operations)

### REST endpoint
Each item type is accessible via the CRUD REST endpoint running on **port 19000** by default. All the common HTTP verbs are supported:
* `GET`: get all or a single item
* `PUT`: create or update and item (Idempotent)
* `POST`: always creates a new item
* `PATCH`: update an existing item
* `DELETE`: delete an item

For example to fetch all `Country` objects you can call this URL: http://localhost:19000/v1/models/country.

> For a more details head over to [CRUD REST interface](#crud-rest-interface)

## Service infrastructure
To facilitate the full power of spot framework, there are serveral services available, that wrap functionalities defined in the underlying frameworks' and libraries'.

The most notable frameworks used are:
* **Infrastructure**: Spring
* **Serialization**: Jackson
* **Persistence**: Hibernate
* **HTTP endpoints**: [spark](http://sparkjava.com/)

It is an advantage to have Spring know-how to make the best out of spot. You won't get into contact with Hibernate on the other hand. Hibernate's complexitiy is hidden - persistence operations are consolidated in the `ModelService`and `QueryService`.




#### Persistence operations
One of the fundamental aspects of spot is the "everything is an object" mantra. Therefore one of the most important services is the `ModelService`. It provides basic functinality to handle `Item`s (also called "models", or "entities" in JPA).

```plantuml

    frame "Persistence layer" as PersLayer {
        component QueryService [
            QueryService
        ]
        component ModelService [
            ModelService
        ]
        component PersistenceService #WhiteSmoke [
            PersistenceService
        ]
        component TypeService #WhiteSmoke [
            TypeService
        ]

        QueryService --> PersistenceService
        ModelService --> PersistenceService
        ModelService --> TypeService
        PersistenceService -> TypeService
    }
```

The functionality `ModelService` offers:
* Save
* Get
* Simple queries based 

#### ImportService
#### ConversionService
#### LoggingService
#### I18nService
#### L10nService
#### SessionService
#### TypeService
#### UserService
#### ValidationService
#### ConfigurationService

## Domain modelling
These defintions look like this:
```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<types xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="http://spot-next.io/schemas/v2/itemtypes.xsd">

    <atomic name="String" className="java.lang.String" />

	<enum name="UserType" package="io.spotnext.sample.types.enumerations">
		<value code="GUEST" />
		<value code="REGISTERED" />
	</enum>

	<bean name="UserData" package="io.spotnext.sample.types.beans">
		<properties>
			<property name="email" type="String" />
			<property name="name" type="String" />
			<property name="type" type="UserType" />
		</properties>
	</bean>

	<collection elementType="UserData" name="UserDataList" />
	
	<type name="User" package="io.spotnext.itemtype.core.user">
		<properties>
			<property name="type" type="UserType">
				<annotations>
					<annotation javaClass="javax.validation.constraints.NotNull" />
				</annotations>
				<defaultValue>io.spotnext.sample.types.enumerations.UserType.REGISTERED
				</defaultValue>
			</property>
		</properties>
	</type>
</types>
```
### Defining domain models
### Working with models


## System setup
### Initial data import



## Build process
### Maven 

## CRUD REST interface


