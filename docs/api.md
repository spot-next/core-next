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

### Persistence layer
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
Handling the various item persistence operations is achived by utilizing the `ModelService`.

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
Items can be queried in various ways. One of the easiest ones is "Query by example" (or QBE):
```java
final LocalizationValue example = new LocalizationValue();
example.setId("test.key");
example.setLocale(Locale.ENGLISH);
final LocalizationValue result = modelService.getByExample(example);
```

The same can be achieved using a `java.util.Map` containg the key-value pairs used for quering:
```java
Map<String, Object> example = Collections.singletonMap(LocalizationValue.PROPERTY_ID, "test.key");
final LocalizationValue result = modelService.get(LocalizationValue.class, example);
```

> When `ModelQuery`s are configured to use pagination (`ModelQuery.pageSize > 0`), the query results will be ordered by `createdAt` and `id` (both ascending) when there is not custom ordering specified using `Query.addOrderBy(SortOrder)`. This is necessary to ensure consistent ordering.

Another possible way of making type-safe queries is using so called `LambdaQuery`s:
```java
final LambdaQuery<User> query = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testUser"));
final QueryResult<User> result = queryService.query(query);
```
> Internally a JPQL query will be generated, using joins to fetch related objects.

The most flexible way to query data is to write those JPQL queries by yourself using `JpqlQuery`:
```java
final JpqlQuery<User> query = new JpqlQuery<>("SELECT u FROM User u WHERE id = :id", User.class);
query.addParam("uid", "testUser");
final QueryResult<UserGroup> result = queryService.query(query);
```
To execute a JPQL query the `QueryService` is used instead of the `ModelService`. The reason is that using JPQL not only domain models can be retrieved, but also arbitrary java types, most likely Data Transfer Object (DTOs):
```java
final JpqlQuery<UserData> query = new JpqlQuery<>("SELECT id as id, shortName as shortName FROM User u WHERE id = :id", UserData.class);
query.addParam("uid", "testUser");
final QueryResult<UserData> result = queryService.query(query);
```
> The properties `UserData.id` and `UserData.shortName` are automatically populated - either by using a suitable constructor or by directly accessing the setters.
> Because of limitations in Hibernate, it is not possible right now to infert the corresponding DTO properties based on the implicite column names. Therefore every selected column has to be aliased!

Of course, it's also possible to query for primitive types:
```java
final JpqlQuery<String> query = new JpqlQuery<>("SELECT id FROM User u WHERE id = :id", String.class);
query.addParam("uid", "testUser");
final QueryResult<String> result = queryService.query(query);
```

The `io.spotnext.core.persistence.query.Query` can be configured to influnce the way data is queried:
* `eagerFetchRelations`: if true, alls releations (1-1, 1-N, N-M) will be fetched using a `FETCH JOIN` using one single SQL select. If used together with `page` and/or `pageSize` a second query will be issues to avoid Hibernate problem **HHH000104** (firstResult/maxResults specified with collection fetch; applying in memory), which can be a major source of performance issues.
* `eagerFetchRelationProperties`: instead of eagerly fetching all relation properties, this allows to configure only specific properties by using their name.
* `page`: specifies the page for pagination, defaults to page `0` (= first page)
* `pageSize`: specifies the page sie used for pagination, defaults to `0` (= no limitation)
* `cachable`:specifies that the query can can be cached using Hibernate's second level cache (if enabled)
* `ignoreCache`: specifies that the results should always be fetched from the database, and caches should be ignored.

> Iterating over all item properties (e.g. when serializing) will lead to the infamous **N+1 problem**, because by default all **relational propertie are lazy-loaded**! Therefore the relations will be loaded on access. This can cause a lot of database queries and drastically decrease performance. **To avoid this, use the eager-fetch settings**!

### Serialization
Often it is required to exchange data with external systems - mostly using JSON or XML. The `SerializationService` is a convenient way to convert to or from **any java object to one of these formats**.

A new `User` item can be deserialized just with this one-liner:
```java
SerializationConfiguration config = new SerializationConfiguration();
config.setFormat(DataFormat.JSON);
User user = serializationService.deserialize(config, jsonString, User.class);
```

Relations can be setup by just using a JSON object that contains both a `id` and a `typeCode` property - **nothing else**:
```json
{
	"uid":"testerUser",
	"groups": [
		{
			"typeCode": "usergroup",
			"id": "4513155367448757049"
		}	
	]
}
```

It's also possible to overwrite an object's properties with the data from a JSON string:
```java
User userToUpdate = ....;
SerializationConfiguration config = new SerializationConfiguration();
config.setFormat(DataFormat.JSON);
User user = serializationService.deserialize(config, jsonString, userToUpdate);
```
> Only the target object's properties, that are defined in the JSON, are overwritten!

**Deserialized** `Item`s are automatically attached to the persistence context and can be used as if they were loaded using `ModelService` or `QueryService`.

When **serializing** `Item`s, their sub-items (relation properties) are only partially serialized - only the `id` and a `typeCode` JSON properties are included:

```java
{
	"id": "4003256542000269317",
	
	...

	"shortName": {
		"id": "8617539322739705278",
		"typeCode": "localizedstring"
	},
	"longName": {
		"id": "5123398329302468367",
		"typeCode": "localizedstring"
	},
	"phoneCountryCode": "43",
	"languages": []
}
```

This mechanism averts problems when serializing cyclic dependencies. But it only applies for objects of type `Item`!

> Currently the only other supported data format is XML. Although a custom `io.spotnext.core.infrastructure.strategy.SerializationStrategy` is easy to implement.

### Data import
The `SerializationService` is not the only way to import data into the system. The `ImportService` offers similar functionality, but it's focus is not on data transfer, but rather on local data that is being batch-imported, like during **system initialization**.

In alot of situations data is provided by business people and is therefore only available in a tablular format (CSV, Excel etc). For table-based imports this might be a good choice, but for importing objects with relations, this can get cumbersome pretty fast.

The **ImpEx** format solves this problem (although it has the same name, it is not 100% compatible to the implementation SAP Hybris Commerce Cloud offers).

An simple ImpEx file is a CSV-like format that can be "annotated" with import instruction:
```impex
INSERT_UPDATE Media ; id[unique=true] ; catalogVersion(catalog(id),id)[unique=true] ;
                    ; testMedia       ; Media:Staged                                ;
```

Every import block starts with a header line that specifies how the data should be imported.

The supported command are:
* `INSERT`: inserts new item, if it doesn't exist yet (based on the uniqueness criteria)
* `INSERT_UPDATE`: inserts a new item, if it doesn't exist, or updates an existing one. The unique properties have to be specified with the column modifier `unique=true`
* `UPDATE`: updates an existing item based on the specified unique columns, is ignored if there is not such item
* `REMOVE`: removes an item based on the specified unique column

> The commands are similar to the basic SQL commands

Every column (except for the first, which is the command) can be configured with a **selector** (`(catalog(id),id)`) and some **modifiers** (`[unique=true]`). These settings are used by the `io.spotnext.core.infrastructure.resolver.impex.ImpexValueResolver`s to interpret the column value.

The framework provides these resolvers out of the box:
* `ReferenceValueResolver`: used for resolving relation items
* `PrimitiveValueResolver`: converts the string column values to the target type. Supporter are numbers, date and time values
* `FileValueResolver`: reads the file content of the file specified as the column value.
* `LocalDateValueResolver`: parses the string of the format `2018-01-31` into a `java.time.LocalDate` object.
* `LocalTimeValueResolver`: parses the string of the format `10:15:30` into a `java.time.LocalTime` object.
* `LocalDateTimeResolver`: parses the string of the format `2018-01-31 10:15:30` into a `java.time.LocalDateTime` object.
* `FileValueResolver`: reads the file content of the file specified as the column value.

> The column resolver can be configured using the modifier `[resolver=<bean name>]` (not necessary for the first two resolvers). The bean names are usually the class names, staring with a lower first key.

The selector definition is only needed for relational data, like for the linked `CatalogVersion` object in the example above. The selector `(catalog(id),id)` defines how the column value (`Media:Staged`) should be interpreted:
* "Media" is resolved using the nested selector `catalog(id)`, which in turn leads to a `Catalog` item with the `id` "Media".
* "Staged" is a "primitive" value used for the `version` property of the aforementioned `CatalogVersion` item.

> Although in this example the `CatalogVersion` items unique properties are the same as the ones used in the example, the selector can use other non-unique properties too. **Altough, if more than one item is found using this selector, an exception is thrown!**

The supported modifiers are:
* `unique=true`: used by the `ReferenceValueResolver`, to specifiy that this column value is used to defined the "uniqueness" of the item to import
* `resolver=`: used to determine the responsible resolver (specified by using the Spring bean name)
* `default`: the default value used if there is no actualy value supplied. If the value is an empty string, it has to be surrounded with quotes
* `lang=<iso code>`: the `Locale` used for localized properties
* `PrimitiveValueResolver` and `Local*Resolver` support the `format` modifier to set a custom date format.

> (Custom) resolvers have access to both the selector and the modifiers!
> Allowed characters in the modifiers part are: letters, numbers and `,.:;+*'#&%$§!/ _-=`

Here is a quick demonstration of references and default values:

[users.impex](https://raw.githubusercontent.com/spot-next/spot-framework/develop/spot-core/src/main/resources/data/initial/users.impex ':include')

... and localized strings:

[localized_strings.impex](https://raw.githubusercontent.com/spot-next/spot-framework/develop/spot-core/src/main/resources/data/test/localized_string.impex ':include')

More examples can be found [**here**](https://github.com/spot-next/spot-framework/tree/develop/spot-core/src/main/resources/data/test)

The `ImpportService` API looks like:
```java
ImportConfiguration conf = new ImportConfiguration();
conf.setIgnoreErrors(false);
conf.setScriptIdentifier(path);

InputStream stream = CoreInit.class.getResourceAsStream(conf.getScriptIdentifier());
importService.importItems(ImportFormat.ImpEx, conf, stream);
```

The `ImportConfiguration` can be configured:
* `ignoreErrors`: if true, errors (like unresolvable item references) are ignored (although logged) and as many as possible items are being imported. By default this is disabled though.
* `scriptIdentifier`: is optional, but can be useful for debugging purposes (in the logs)

> During **system initialization** botj the essential and the sample data are imported using this functionality, eg. in `CoreInit`. Every custom `Init` class has to implement these methods:

```java
@Override
protected void importInitialData() throws ModuleInitializationException {
	super.importInitialData();
}

@Override
protected void importSampleData() throws ModuleInitializationException {
	super.importSampleData();
}
```

For conventience the `ModuleInit.importScript` method can be used:
```java
ModuleInit.importScript("/data/initial/countries.impex", "Importing countries");
```
### Other Services
#### Localization & internationalization

There are two services dealing with this topic:

* `I18NService`: is basically just a convenience wrapper to access `Locale` and `Currency` information
* `L10NService`: handles message translation/interpolation.

The `L10NService` both implements `org.springframework.context.MessageSource` and `javax.validation.MessageInterpolator`. Therefore it can easily be plugged into a lot of existing frameworks, or just used manually. At first, messages are looked up in the persistence store (`io.spotnext.itemtype.core.internationalization.LocalizationValue` items).
This allows for translations to be changed at runtime, e.g through the REST interface.

If the message key cannot be resolved, the fallback (Spring) parent message source is invoked. It looks up the keys in `messages_XX.properties` files.

#### Users and permissions

A lot of business logic revolves around users and their permissions. The `UserServer` aims at simplifying access to these objects. Besides various DAO-like features (like `getAllUserGroups(..)` or `isUserInGroup(..)`), it also allows to access the internal session user using `SessionService`.
This session (not to confused with a web session) is used to determine all sorts of permissions. In combination with the `AuthenticationService` integration into various security-related frameworks (like Spring Security) can easily be achived.
To integrate into Spring Security these classes can be used:

* `io.spotnext.spring.web.security.DefaultAuthenticationProvider`
* `io.spotnext.spring.web.session.WebSessionFilter`
* `io.spotnext.spring.web.session.WebSessionListener`

> Other Spring-related helper classes can be found in the `spot-spring-web-support` library.

#### Data validation

The `ValidationService` can be used to validate objects based on their JSR-303 annotation. It is designed to be a central point of validation. So if any custom validation is needed, this service should be extended!

#### TypeService

The `TypeService` provides functionality to access to the type definitions (from the `*-itemtypes.xml`s). It can also be used to retrieve a `Class` object for `typeCode` or vice-versa.

### ConfigurationService

A typical Spring application contains various sources of configuration settings, like `application.properties` or even command-line arguments. The `ConfigurationService` allows to access these settings in a type-safe manner, e.g. using `getInteger(String key, Integer defaultValue)`. It also allows to access settings based on a certain prefix `getPropertiesForPrefix()`.

> Settings injection using @Value("{property.key}") is supported too! Also see: [Configuration](api/#Configuration)

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
### (Initial) data import

Supported header

## Build process
### Maven 

## CRUD REST interface


