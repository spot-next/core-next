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

[](diagrams/spring_setup.plantuml ':include :type=plantuml')

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

[](diagrams/persistence_layer.plantuml ':include :type=plantuml')

The items definitions can be accessed using the `TypeService` at runtime too.

The main difference to a pure JPA/Hibernate approach is once a Hibernate session has been opened in thread, it stays open! This is also referred to as "**long running session**". The session has not to be opened manually, but instead it **automatically created when necessary**.
This approach has the advantage that you will never have to face the infamous "**LazyLoadException**", although by default all relations (collections, maps) are **lazy-loaded on access**.
The downside is that this can lead to the (also) infamous **N+1 problem**. Each lazy-loaded collection triggers an database query.
A typical scenario is an "export service" that generates CSV files out of all product items. This service might iterate over all product properties and causes a lot of queries. To circumvent this the persistence API offers a way to (programmatically) specify which properties (or all) should be eagerly loaded.

> For a more details head over to [Persistence operations](#persistence-operations)

### REST endpoint
Each item type is accessible via the CRUD REST endpoint running on **port 19000** by default. All the common HTTP verbs are supported:
* `GET`: get all or a single item
* `POST`: always creates a new item
* `PUT`: create or update and item (Idempotent)
* `PATCH`: update an existing item
* `DELETE`: delete an item

For example to fetch all `Country` objects you can call this URL: http://localhost:19000/v1/models/country.

> For a more details head over to [CRUD REST interface](#crud-rest-interface)

## Service infrastructure
To implement your business logic there are serveral services available that wrap functionalities provided by the underlying frameworks' and libraries'.

The most notable frameworks used are:
* **Persistence**: Hibernate
* **Infrastructure**: Spring
* **Serialization**: Jackson
* **HTTP endpoints**: [spark](http://sparkjava.com/)

It is an advantage to have Spring know-how to make the best out of spot. You won't get into contact with Hibernate on the other hand. Hibernate's complexitiy is hidden - persistence operations are consolidated in the `ModelService`and `QueryService`.

### Persistence operations
One of the fundamental aspects of spot is the "everything is an object" mantra. Therefore one of the most important services is the `ModelService`. It provides basic functinality to handle `Item`s (also called "models", or "entities" in JPA).

#### Item lifecycle
The basic item lifecycle looks like this:

[](diagrams/item_lifecycle.plantuml ':include :type=plantuml')

#### Item modification interceptors
The `ItemInterceptor`s provide a way to inject business logic into the persistence operations. **This API is not implemented using Hibernate or JPA APIs**. Instead a generic approach has been chosen. It is enough to extend the `io.spotnext.core.infrastructure.interceptor.impl.AbstractItemInterceptor` and implement one of:
* `ItemCreateInterceptor`
* `ItemPrepareInterceptor`
* `ItemValidateInterceptor`
* `ItemLoadInterceptor`
* `ItemRemoveInterceptor`

By extending the base class the new interceptor is registered in the interceptor registry and will automatically called. It is perfectly valid to throw the defined exceptions from the interceptors - though this will cancel the ongoing persistence operation.

A good example for a prepare interceptor is the `io.spotnext.core.infrastructure.interceptor.impl.UniqueIdItemSaveInterceptor`. It generates a unique "business key" for each item extending `UniqueIdItem`:

[](https://raw.githubusercontent.com/spot-next/spot-framework/develop/spot-core/src/main/java/io/spotnext/core/infrastructure/interceptor/impl/UniqueIdItemSaveInterceptor.java ':include')

#### Item modification events
Interceptors are called synchronously and therefore have a high impact on the performance of persistence operations. So if you plan on doing some heavy work bound to a specific persistence operation, using `ItemModificationEvent`s is a better idea - they are called asynchronously. You can subscribe to such an event by using Spring's annotation-based handling mechanism:

[PartyModificationListener](https://raw.githubusercontent.com/spot-next/spot-framework/develop/spot-sample-simple/src/main/java/io/spotnext/sample/listeners/PartyModificationListener.java ':include')

The handled item can be accessed using `ItemModificationEvent.getItem()` (used in the condition SPEL expression). Furthermore the kind of modification is expose through `ItemModificationEvent.getModificationType()`.

#### ModelService API
Handling the various item persistence operations is achived by utilising the `ModelService`.

This is the way to instantiate and save a new item:
```java
final User user = modelService.create(User.class);
user.setId(id);
user.setShortName(shortName);

modelService.save(user);
```
> It is always recommenced to use the `modelService.create(...)` method for instantiation. Otherwise the `io.spotnext.core.infrastructure.interceptor.ItemCreateInterceptor` would not be invoked.

As long as the current thread is active the model is registered in the Hibernate session. If you reuse threads, for example when using an `ExecutorService`, it is necessary to close the session manually: `persistenceService.unbindSession();`. This not just frees up some memory (by empting the session cache) but also prevents some tricky caching issues and other Hibernate problems.

A Hibernate session can only contain **one** physical instance of an entity. If you load the same entity again, it is loaded from the session cache. If in the mean time the item has been altered, the cached item might not represent the most up-to-date version. This can later lead to `OptimisticLockException` or similar.

> As long as an item is unsaved, its `version` property is `-1`. This property is used for optmistic locking and will be increased each time the item is saved (using an `UPDATE ... WHERE` clause)

Already persisted Item instances can be removed using `ModelService.remove(...)`.

##### Model queries
Items can be queried in various ways

Query by example:
```java
final LocalizationValue example = new LocalizationValue();
example.setId("test.key");
example.setLocale(Locale.ENGLISH);
final LocalizationValue loaded = modelService.getByExample(example);
```

Query by key-value pairs


Query using JPQL:
```java
final JpqlQuery<User> query = new JpqlQuery<>("SELECT u FROM User u WHERE id = :id", User.class);
query.addParam("id", "testUser");
query.setEagerFetchRelations(true);
final QueryResult<UserGroup> result = queryService.query(query);
```

Type-safe queries:
```java
final LambdaQuery<User> query = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testUser"));
final QueryResult<User> result = queryService.query(query);
```
> Internally a JPQL query will be generated!

JPQL queries with complex non-Item type results:
```java
final JpqlQuery<UserData> query = new JpqlQuery<>("SELECT id as id, shortName as shortName FROM User u WHERE id = :id", UserData.class);
query.addParam("id", "testUser");
final QueryResult<UserData> result = queryService.query(query);
```

JPQL queries with primitive results:
```java
final JpqlQuery<String> query = new JpqlQuery<>("SELECT id FROM User u WHERE id = :id", String.class);
query.addParam("id", "testUser");
final QueryResult<String> result = queryService.query(query);
```

##### Other operations


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


