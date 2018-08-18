# Introduction

The idea of the spOt microservice framework is to take the good parts of the SAP Hybris framework and bring them to the next level. For those who don't know SAP Hybris: it's a enterprise-grade ecommerce framework with it's own custom object-oriented persistence layer. You don't need to setup database tables or anything like that - all models are generated from XML definitions. It allows you to extend all models, even those who are defined by the framework itself. This makes it pretty easy to add new properties to types like Product, Category or Customer.

Additionally, the framework also offers the so called "Accelerator". A demo-shop/website (B2C, B2B, telco, insurance) that can be used as a starting point for your ecommerce project.

**To sum up the good things:**
 
> * Very flexible/adaptable and well-defined ecommerce domain model
> * Services available for all sorts of problem domains: cart manipulation, product search, authentication and many more
> * Demo-shop can be used as starting point

Sound pretty cool, huh? .... WELL. It also has a lot of downsides.

Building the entire suite takes up to 3-7 minutes - depending on the project setup. Startup time is about 4-6 minutes too. Hot-code replacement - event with JRebel - doesn't work very well. At the end of the day you probably spent half of your day waiting for Hybris to boot up after you made changes to the code, and the other half debugging decompiled Hybris code ....

Also the readily-available services are often over-engineerd and hard to customize, sometimes the code quality is very poor, sometimes you simply don't know if it's better and faster to rewrite an entire function block or find out how the Hybris functionality works (hello approval process, I'm looking at you).

Of course, documentation is ... scarce and mostly useless.

Out of frustration this project was born!


## Core concepts
The three fundamental principles of spOt revolves around the idea of
* **Everything is an object**: from a basic localization value to a configuration entry
* **Consistency across all places**:  every API feels the same way
* **Sane defaults**: less configurability, better default settings

Together with the **spring-based service-oriented architecture** it offers you a very flexible and customizable (and most importantly fun-to-use) base for you application.


### Everything is an object

The **predefined domain models comprises basic functionality**: users, localization, configuration, ... Every type can be extended with custom properties. New types can be added easily. The persistence layer handles relations, proxying, lazy-loading, database initialization and even serialization transparently. Forget the **JPA annotation hell** (or XML for that matter).

> Nearly every aspect of system can be configured using objects (or so called **Items**).


### Consistency across all places

There are services alot of default services available for: user authentication, persistence, i18n, l10n, data import, serialization.
All those services are not re-implemented from scratch but rather "wrap" well-known technologies into a new API, eg. Hibernate is used for persistence, Jackson is used for serialization.

All these various technologies are integrated (actually abstracted away) and follows the **Principle of Least Surprise**. Every tried to **serialize a Hibernate entity** using Gson or Jackson? Have fun!

Not only does serialization work as expected - spOt even provides a generic REST interface for you, to manipulate every aspect of your domain objects.
Every (new) type is automatically available as REST endpoint, offering functionality like search/filter, load, save, delete and update single properties.

### Sane defaults
The open-source world spawned some very awesome peaces of software. But sometimes they are a bit overengineered and hard to use for common scenarios. spOt tried to **dumb down the APIs** and make them more **usable by setting better defaults** and **hiding the most obscure features**.

Probably every newcomer to Hibernate is struck by awe when seeing the "FETCH JOIN" in work and a few days is truck by anger only to see it fail with some obscure exceptions like:

```
org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags
```

Don't worry, although Hibernate is used for persistence, such thin nevery can happen to you. I could go on for hours and whine about the hundreds of hours that I lost debugging the insane behaviour and pitfalls ... but I won't bore you with the details ;-)

You are still here? Well, then let's better get started!

## Quick start

First add this to your local **~/.m2/settings.xml* to enable the spOt snapshop repository:
```xml
<profiles>
	<profile>
		<id>sonatype-staging</id>
		<repositories>
			<repository>
				<id>sonatype-staging</id>
				<url>https://oss.sonatype.org/content/repositories/staging/</url>
			</repository>
		</repositories>
	</profile>
</profiles>
```

With this command you can directly initializes an empty spOt project:
```bash
mvn -Psonatype-staging archetype:generate -B \
		-DarchetypeGroupId=io.spot-next.archetypes \
		-DarchetypeArtifactId=archetype-empty \
		-DarchetypeVersion=1.0-SNAPSHOT
		-DgroupId=io.spot-next.test \
		-DartifactId=test-project \
		-Dpackage=io.spotnext.test \
		-Dversion=1.0-SNAPSHOT
```

> Right now we have to use the snapshopt repository, as maven central has a problem indexing new archetypes. ([ARCHETYPE-555](https://issues.apache.org/jira/browse/ARCHETYPE-555)). Therefore the `-DarchetypeVersion` parameter is only necessary until maven central has fixed their issues. If you have used it you always have to include the `-Psonatype-staging` for maven to be able to resolve the `1.0-SNAPSHOT` version of the project dependencies. Or otherwise update the **pom.xml** `parent` tag with the current version.

The project does not yet define any custom types, nor does it contain any special functionality. But after `mvn clean install` you can already boot it with (cd into the project directory first:
```bash
java -jar target/test-project-1.0-SNAPSHOT-jar-with-dependencies.jar \
		-initializetypesystem -importinitialdata -importsampledata```
> If you changed the variables above you also have to adapt the JAR-filename!

So what is the meaning of the command line arguments?
* **initializetypesystem** creates the necessary database schema based on the domain model
* **importinitialdata** imports some basic data, like the admin user, country and language data
* **importsampledata** imports some test users and media files

> The **first two** arguments are **necessary** if you boot up spOt the first time.

During boot you will then get some output similar to this:
```
21:59:04.856 [main] INFO  i.s.c.i.support.init.Bootstrap - Bootstrapping done.
21:59:05.472 [main] INFO  io.spotnext.test.Init - Starting Init on zelek.office.getit.de with PID 32712 (/private/var/tmp/test-project/target/test-project-1.0-SNAPSHOT-jar-with-dependencies.jar started by matthias.fuchs in /private/var/tmp/test-project)
21:59:05.473 [main] INFO  io.spotnext.test.Init - No active profile set, falling back to default profiles: default
21:59:08.121 [main] INFO  i.s.c.i.s.impl.DefaultTypeService - Detected application root path: jar:file:/private/var/tmp/test-project/target/test-project-1.0-SNAPSHOT-jar-with-dependencies.jar!/BOOT-INF/classes!/
21:59:08.321 [main] INFO  i.s.c.i.s.impl.DefaultTypeService - Registered item types: currency, localizationvalue, addresstype, filemedia, configentry, localizedstring, usergroup, user, configuration, country, contactdetailstype, media, language, catalogversion, catalog, useraddress, mediacontainer
21:59:09.886 [main] INFO  hsqldb.db.HSQLDB652A90F714.ENGINE - checkpointClose start
21:59:09.889 [main] INFO  hsqldb.db.HSQLDB652A90F714.ENGINE - checkpointClose synched
21:59:09.896 [main] INFO  hsqldb.db.HSQLDB652A90F714.ENGINE - checkpointClose script done
21:59:09.902 [main] INFO  hsqldb.db.HSQLDB652A90F714.ENGINE - checkpointClose end
21:59:11.352 [main] INFO  i.s.c.p.h.i.HibernatePersistenceService - Initializing type system schema ...
21:59:11.381 [main] WARN  i.s.c.p.h.i.HibernatePersistenceService - Could not drop type system schema.
21:59:11.590 [main] WARN  i.s.c.p.h.i.HibernatePersistenceService - Type system schema needs to be initialized/updated
21:59:11.831 [main] INFO  i.s.c.m.s.i.TypeSystemServiceRestEndpoint - Initiating remote type system REST service on port 19000
21:59:11.887 [Thread-4] INFO  org.eclipse.jetty.util.log - Logging initialized @7939ms to org.eclipse.jetty.util.log.Slf4jLog
21:59:12.017 [Thread-4] INFO  s.e.jetty.EmbeddedJettyServer - == Spark has ignited ...
21:59:12.018 [Thread-4] INFO  s.e.jetty.EmbeddedJettyServer - >> Listening on 0.0.0.0:19000
21:59:12.025 [Thread-4] INFO  org.eclipse.jetty.server.Server - jetty-9.4.6.v20170531
21:59:12.081 [Thread-4] INFO  org.eclipse.jetty.server.session - DefaultSessionIdManager workerName=node0
21:59:12.082 [Thread-4] INFO  org.eclipse.jetty.server.session - No SessionScavenger set, using defaults
21:59:12.085 [Thread-4] INFO  org.eclipse.jetty.server.session - Scavenging every 660000ms
21:59:12.182 [Thread-4] INFO  o.e.jetty.server.AbstractConnector - Started ServerConnector@520ffd6f{HTTP/1.1,[http/1.1]}{0.0.0.0:19000}
21:59:12.182 [Thread-4] INFO  org.eclipse.jetty.server.Server - Started @8235ms
21:59:12.961 [main] INFO  io.spotnext.test.Init - Started Init in 8.087 seconds (JVM running for 9.013)
21:59:13.020 [main] INFO  io.spotnext.core.CoreInit - Importing initial data for CoreInit
21:59:15.859 [main] INFO  io.spotnext.core.CoreInit - Importing sample data for CoreInit
21:59:16.305 [main] WARN  i.s.c.i.s.i.DefaultImpexImportStrategy - Ignoring empty file /data/sample/medias.impex
21:59:16.305 [main] INFO  i.s.c.i.support.init.ModuleInit - Initialization complete
21:59:16.342 [main] INFO  io.spotnext.test.Init - No active profile set, falling back to default profiles: default
21:59:17.196 [main] INFO  io.spotnext.test.Init - Started Init in 0.89 seconds (JVM running for 13.248)
21:59:17.199 [main] INFO  i.s.t.Init$$EnhancerBySpringCGLIB$$579770a4 - Importing initial data for Init
21:59:17.200 [main] INFO  i.s.t.Init$$EnhancerBySpringCGLIB$$579770a4 - Importing sample data for Init
21:59:17.200 [main] INFO  i.s.c.i.support.init.ModuleInit - Initialization complete
```

The last line indicates that the system has booted and can now be used. But what for?

A core spOt project does not do any useful stuff - overall it's a framework. So you got to do the work ;-)
Actually that's not entirely true. As mentioned earlier, spOt provides a generic REST CRUD service that allows you to manipulate all available domain models.

Fire up [Postman](https://www.getpostman.com/) (or a REST-client of your choosing) and issue this request:
```http
GET /v1/models/user HTTP/1.1
Host: localhost:19000
Authorization: Basic YWRtaW46TUQ1OmVlMTBjMzE1ZWJhMmM3NWI0MDNlYTk5MTM2ZjViNDhk
```
> The REST endpoints are secured with basic authentication. The default password for the user `admin` is `nimda`

It's easy to guess what this does: list all user objects:

```json
{
    "errors": [],
    "warnings": [],
    "data": {
        "objects": [
            {
                "pk": 22207630153109145,
                "createdAt": 1534017556159,
                "createdBy": "<system>",
                "lastModifiedAt": 1534017556159,
                "lastModifiedBy": "<system>",
                "version": 0,
                "deleted": false,
                "uniquenessHash": -1146271689,
                "id": "tester92",
                "shortName": null,
                "groups": [
                    {
                        "pk": 2672514096731801604,
                        "typeCode": "usergroup"
                    }
                ],
                "emailAddress": null,
                "password": "MD5:16d7a4fca7442dda3ad93c9a726597e4",
                "addresses": []
            },
            ...
        ]
    }
}
```


> TODO: Download and import this [Postman config]() for a full set of available endpoints

Currently we don't have any custom domain model types configured. So let's head on to the next chapter.

### Adapt domain model
Let's say our project goal is to offer a party guest list service:
* CRUD-REST interface to create parties, locations add guests
* Automatically send party confirmation emails as soon as a party's date, location and guest list has been fixed
* Automatically send party invitation emails when new guests are registered

Before we are going to implement the actual functionality, we start by modelling the domain objects. Among the predefined types `User` and `Address` seem suitable candidates for "party guest" and "location".
But we definitely need a new model type `Party`.
> In the JPA/HIbernate world this would be called an "Entity". The corresponding spOt terminology is "Item type".

Open the `src/main/resources/test-project-itemtypes.xml` file - it contains the type definitions.     
Common Java IDEs like Eclipse or IntelliJ offer auto-completion in XML files. This comes in handy to explore the possible constructs (in a type-safe and correct way!).

First we add the new `Party` type:
```xml
<type name="Party" package="io.spotnext.test.types.itemtypes">
	<properties>
		<property name="title" type="String">
			<description>The unique title of the party</description>
			<modifiers unique="true" />
			<validators>
				<validator javaClass="javax.validation.constraints.NotNull" />
			</validators>
		</property>
		<property name="motto" type="LocalizedString" localized="true">
			<description>The localized motto of the party</description>
		</property>
		<property name="location" type="Address">
			<description>The location the party will take place</description>
		</property>
		<property name="date" type="Date">
			<description>The date the party will take place</description>
		</property>
		<property name="fixed" type="boolean">
			<description>Defines that the party has been fixed and should not be changed anymore.</description>
			<defaultValue>false</defaultValue>
		</property>
	</properties>
</type>
```
> The `motto` property is somewhat special as it is a `LocalizedString`, which allows you to store a separate string per `Locale` 
> Each time the *itemtypes.xml is changed, we can either run a full `mvn install` or the faster `mvn spot:generate-types spot:transform-types`
> Make sure that the package you choose is a subpackge of the one defined in the `Init` class:
> ```java
> Bootstrap.bootstrap(Init.class, new String[] { "io.spotnext.test.types" }, args).run();
> ```

Both the **name and the package are mandatory** as some java code is generated out of this XML snippet. Every item has a unique `PK` to distinquish different objects in the database. Additionally we added the "unique-modifier" to the `title` property. This creates another database constraint that only allows one `Party` with the same title.
The validator element `javax.validation.constraints.NotNull` adds a [JSR-303 validation](https://beanvalidation.org/1.0/spec/) to the property. Basically it means that the value may not be `null` when saving.
The description elements will be rendered as Javadoc.

What we are still missing is list of guests. In general there are two options to add a collection property to an Item type:
* Use a `CollectionType` or a `MapType`
* Create a relation between to Item types   

We have to create a new relation, as the plain collection properties only support non-Item types (`AtomicType` and `EnumType`):

```xml
<relation name="Party2InvitedUser">
	<source itemType="Party" mappedBy="parties" cardinality="many">
		<description>The parties this user is invited.</description>
	</source>
	<target itemType="User" mappedBy="invitedGuests" cardinality="many">
		<description>The guests that are invited to this party.</description>
	</target>
</relation>
```
> A relation definition always has a source and a target, although it doesn't matter what type is on what side.

In our case we declared a many-to-many relation between `Party` and `User`. Both types will be extended with the corresponding properties (the `mappedBy` attribute), the descriptions will be rendered as Javadoc.

> The name of the relation doesn't really matter, but it is a good practice to name it **Source2Target** or **Source_property2Target_property**. 

To generate the Java classes, issue this command: `mvn clean install`. This creates a new class `Party`, and adds the `parties` property to the `User` class.  

Now it's time to fire up the system and create some parties. This time we use this command:

```bash
java -jar target/test-project-1.0-SNAPSHOT-jar-with-dependencies.jar -updatetypesystem
```
> There is no need to initialize the whole type system anymore. Instead we can use the parameter `updatetypesystem`. This adds new properties and types to the database.

To create a new party we issue the following request:
```http
POST /v1/models/party HTTP/1.1
Host: localhost:19000
Content-Type: application/json
Authorization: Basic YWRtaW46TUQ1OmVlMTBjMzE1ZWJhMmM3NWI0MDNlYTk5MTM2ZjViNDhk

{
    "title": "spOt test party",
     "motto": {
        "en": "Let's do it",
        "de": "Auf geht's",
        "typeCode": "localizedstring"
    },
    "location": {
	    	"typeCode": "useraddress",
	    	"streetName": "Test street",
	    	"streetNumber": "100",
	    	"city": "Vienna",
	    	"postalCode": "1030",
	    	"country": "at"
    },
    "invitedGuests": [
	    	{
	    		"typeCode": "user",
	    		"id": "guest-01@test.at",
	    		"shortName": "Guest user #1"
	    	}
    ]
}
```

**STOP!** Do you see the error (or can guess what it is?). Let's try it out:
```json
{
    "errors": [
        {
            "code": "error.internal",
            "message": "Cannot deserialize object: Cannot construct instance of `io.spotnext.itemtype.core.internationalization.Country` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('at')\n at [Source: (String)\"{\n    \"title\": \"spOt test party\",\n    \"location\": {\n    \t\"typeCode\": \"useraddress\",\n    \t\"streetName\": \"Test street\",\n    \t\"streetNumber\": \"100\",\n    \t\"city\": \"Vienna\",\n    \t\"postalCode\": \"1030\",\n    \t\"country\": \"at\"\n    },\n    \"guests\": [\n    \t{\n    \t\t\"typeCode\": \"user\",\n    \t\t\"id\": \"guest-01\",\n    \t\t\"shortName\": \"Guest user #1\"\n    \t}\n    ]\n}\"; line: 18, column: 1] (through reference chain: io.spotnext.test.types.itemtypes.Party[\"location\"]->io.spotnext.itemtype.core.user.UserAddress[\"country\"])"
        }
    ],
    "warnings": []
}
```

You probably guest it already: we cannot pass a country by it's ISO code. `Country` is a separate type and therefore has to be referenced with a `PK`.
To obtain this `PK` we issue a quick search:
```http
GET /v1/models/country/query/?q=isoCode = 'AT' HTTP/1.1
Host: localhost:19000
Authorization: Basic YWRtaW46TUQ1OmVlMTBjMzE1ZWJhMmM3NWI0MDNlYTk5MTM2ZjViNDhk
```

> Please mind the `/` after "country/query"!

This results in something like this:
```json
{
    "errors": [],
    "warnings": [],
    "data": {
        "results": [
            {
                "pk": 4003256542000269317,
                "createdAt": 1534017553477,
                "createdBy": "<system>",
                "lastModifiedAt": 1534017553477,
                "lastModifiedBy": "<system>",
                "version": 0,
                "deleted": false,
                "uniquenessHash": 2130,
                "isoCode": "AT",
                "iso3Code": "AUT",
                "shortName": {
                    "pk": 8617539322739705278,
                    "typeCode": "localizedstring"
                },
                "longName": {
                    "pk": 5123398329302468367,
                    "typeCode": "localizedstring"
                },
                "phoneCountryCode": "43",
                "languages": []
            }
        ],
        "pageSize": 2147483647,
        "page": 0
    }
}
``` 

After fixing our POST request the creation of a `Party` item succeeds:
```json
{
    "title": "spOt test party",
     "motto": {
        "en": "Let's do it",
        "de": "Auf geht's",
        "typeCode": "localizedstring"
    },
    "location": {
	    	"typeCode": "useraddress",
	    	"streetName": "Test street",
	    	"streetNumber": "100",
	    	"city": "Vienna",
	    	"postalCode": "1030",
	    	"country": {
	    		"typeCode": "country",
	    		"pk": 4003256542000269317
	    	}
    },
    "invitedGuests": [
	    	{
	    		"typeCode": "User",
	    		"id": "guest-01@test.at",
	    		"shortName": "Guest user #1"
	    	}
    ]
}
```

> Please keep in mind that for each property that hold a reference to an `Item` an additional JSON property `typeCode` is necessary. If not defined explicitly in the `*-itemtypes.xml` file, the lowercase class name is used (eg. for the `Party` type has the typeCode `party`).

The REST interface returns the following JSON:
```json
{
    "errors": [],
    "warnings": [],
    "data": {
        "pk": 5653851201092010438
    }
} 
```

Only the `PK` and errors or warnings (if any) are returned. Go ahead and try to inspect the [newly created Party item](http://localhost:19000/v1/models/party).

So now that we can create new items, let's head over to the next chapter.

### Implement functionality
One of our requirements that we defined, is to send an email as soon as the party location, date and guestlist is fixed.
Basically what we have to do is to react to item modifications. There are two ways how to do that:
* Using on of the subclasses of `io.spotnext.core.infrastructure.interceptor.ItemInterceptor<T>` (prepare, validate, load, remove)
* Subscribing to an `ItemModificationEvent`

The first option is a **synchronous approach** - an **interceptor** can slow down and even break a persistence operation like save, load or remove (it runs in the same thread just before or after). As sending mails potentially might be taking longer than a few milliseconds, we go for the seconds option. Among the various events we can listen to, there is also an `ItemModificationEvent`. **Events** are generally delivered **asynchronously** in a different thread.

> Internally Spring's `ApplicationEventMulticaster` is used, although the `EventService` also offers synchronous event delivery

A stub implementation looks like this:
```java
@Service
public class PartyModificationListener {
	@EventListener
	public void handleEvent(final ItemModificationEvent<Party> event) {
		final Party fixedParty = event.getItem();
	}
}
```
> If you are using Eclipse or some other IDE, you have to make sure that the **src/gen/java** source folder is on the buildpath!

As we are only interested in **save** events of parties that have the `fixed` property set to `true`, we add a condition to the `@EventListener` annotation:
```java
@EventListener(condition = "#event.item.fixed == true && #event.modificationType.name() == 'SAVE'")
public void handleEvent(final ItemModificationEvent<Party> event) {
	final Party fixedParty = event.getItem();
}
```

This could of course also be one with a simle `if` statement too.

> Here you can find [further details regarding spring events](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#context-functionality-events-annotation).

Right now spOt doesn't contain any messaging services (but this is definitely something on the roadmap), so for now we just *Simple Java Mail* for that. Include that in the `pom.xml`: 
```xml
<dependency>
    <groupId>org.simplejavamail</groupId>
    <artifactId>simple-java-mail</artifactId>
    <version>5.0.3</version>
</dependency>
``` 

We warp the library in a little service:
```java
@Service
public class EmailService extends AbstractService {

	@Value("${mail.smtp.host}")
	private String mailHost;

	@Value("${mail.smtp.port}")
	private Integer mailPort;

	@Value("${mail.smtp.user}")
	private String mailUser;

	@Value("${mail.smtp.password}")
	private String mailPassword;

	private Mailer mailer;

	@PostConstruct
	public void init() {
		mailer = MailerBuilder.withSMTPServer(mailHost, mailPort, mailUser, mailPassword).buildMailer();
	}

	public void send(final Email email) {
		mailer.sendMail(email);
	}
}
```

> By default the whole root package is enabled for spring's autowire detection mechanism (see the annotation `@SpringBootApplication(scanBasePackages = { "io.spotnext.test" })` on the `Init` class.

The configuration properties have to be defined in the file **src/main/resource** is/application.properties:

```
mail.smtp.host=localhost
mail.smtp.port=2525
mail.smtp.user=
mail.smtp.password=
```

Now we can extend the `PartyModificationListener` to actually create `mail` objects and send them:
```java
@Service
public class PartyModificationListener {

	@Resource
	private EmailService emailService;

	@EventListener(condition = "#event.item.fixed == true && #event.modificationType.name() == 'SAVE'")
	public void handleEvent(final ItemModificationEvent<Party> event) {
		final Party fixedParty = event.getItem();

		// build email for every guest
		for (final User guest : fixedParty.getInvitedGuests()) {
			final Email email = EmailBuilder.startingBlank().to(guest.getShortName(), guest.getId())
					.from("Party Service", "service@party.at")
					.withSubject(fixedParty.getTitle() + " is on " + fixedParty.getDate())
					.withHTMLText("Let's get this party started!").buildEmail();

			emailService.send(email);
		}
	}
}
```
> For the sake of simplicity we directly use the implementation class. In a real world scenario **interfaces should be used instead**! 

To be able to test this, we need to actually have a running SMTP server on port 2525. On option might be  to use [FAkeSMTP](http://nilhcem.com/FakeSMTP/).

Time to test. Issue this HTTP request to update the `fixed` property of the party we created earlier:
```http
PATCH /v1/models/party/5653851201092010438 HTTP/1.1
Host: localhost:19000
Authorization: Basic YWRtaW46TUQ1OmVlMTBjMzE1ZWJhMmM3NWI0MDNlYTk5MTM2ZjViNDhk
Content-Type: application/javascript

{
    "fixed": true
}
```

> The `PK` part of the URL has to be adapted! Use the same as from above or run another `GET` request.

The listener will execute and you should see a new email in your input:
![fakeSMTTP shows the received email](resources/fakeSMTP_email_in_inbox.png "fakeSMTTP shows the received email")

But what's that? We forgot to set the `date` property. As we didn't validate it in the listener, we sent out an erronious mail. There are two options to prevent his from happending again:
* Add anothe condition to the listener
* Implement a custom validation interceptor (`io.spotnext.core.infrastructure.interceptor.ItemValidateInterceptor`)

> **Interceptors** allow you to **inject custom behaviour into persistence operations**.  

Current these interceptors are provided:
* `ItemCreateInterceptor`: runs after `ModelService.create` instantiated a new `Item` instance
* `ItemPrepareInterceptor`: runs before an `Item` is being validated and persisted
* `ItemValidateInterceptor`: runs before an `Item` is being persisted and after it has been prepared
* `ItemLoadInterceptor`: run after an `Item` has been fetched from the database
* `ItemRemoveInterceptor`: runs before a `Item` is being removed

The difference to an `ItemModificationListener` is that the interceptors are called synchronously. If they throw an exception, the whole persistence operation is interrupted.

We will now implement a custom `PartyValidationInterceptor` that validates that both `date` and `location` are set, and that at least one guest is invited.  
```java
@Service
public class PartyValidateInterceptor extends AbstractItemInterceptor<Party> implements ItemValidateInterceptor<Party> {

	@Override
	public void onValidate(final Party item) throws ModelValidationException {
		if (item.isFixed()
				&& (item.getLocation() == null || (item.getDate() == null || item.getDate().isBefore(LocalDate.now()))
						|| item.getInvitedGuests().size() == 0)) {
			throw new ModelValidationException(
					"Party cannot be fixed as not all necessary properties (date, location, invitedGuests) are defined yet.");
		}
	}

	@Override
	public Class<Party> getItemType() {
		return Party.class;
	}
}
```

Let's try to set the `fixed` to `true` property again:
```http
```http
PATCH /v1/models/party/5653851201092010438 HTTP/1.1
Host: localhost:19000
Authorization: Basic YWRtaW46TUQ1OmVlMTBjMzE1ZWJhMmM3NWI0MDNlYTk5MTM2ZjViNDhk
Content-Type: application/javascript

{
    "fixed": true
}
```
> The `PK` has to be adapted!

This time the REST-interface responds with an error:
```json
{
    "errors": [
        {
            "code": "error.onpartialupdate",
            "message": "Party cannot be fixed as not all necessary properties (date, location, invitedGuests) are defined yet."
        }
    ],
    "warnings": []
}
```

So now let's quickly update the date:
```http
PATCH /v1/models/party/5653851201092010438 HTTP/1.1
Host: localhost:19000
Authorization: Basic YWRtaW46TUQ1OmVlMTBjMzE1ZWJhMmM3NWI0MDNlYTk5MTM2ZjViNDhk
Content-Type: application/javascript

{
	"date": "2019-01-01"
    "fixed": true
}
```
> The property type is `java.time.LocalDate`, so we can use the simple date format **yyyy-MM-dd**

Now we should see the date being shown in the email subject.

Most likely you will have to search and manipulate items in the source code as well, not just via REST-interface. Dealing with them will be shown in the next chapter. 

###	Persistence operations
There are several ways to query for items:
* JPQL queries
* Using example objects (can be items or maps, filled with the desired properties)
* Filtering using lambda expressions

This integration test demonstrates how to use them:
```java
@IntegrationTest(initClass = Init.class)
@SpringBootTest(classes = { Init.class, CoreInit.class })
public class PersistenceTest extends AbstractIntegrationTest {

	private static final String PARTY_TITLE = "test party";

	@Resource
	QueryService queryService;

	@Override
	protected void prepareTest() {
		final User guest = modelService.create(User.class);
		guest.setShortName("test user");
		guest.setId("guest-01@gmail.com");

		final Party party = modelService.create(Party.class);
		party.setTitle(PARTY_TITLE);
		party.setDate(LocalDate.of(2019, 1, 1));

		// collections are never null!
		party.getInvitedGuests().add(guest);

		// only the main items has to be saved explicitly, all dependencies
		// will be saved too
		modelService.save(party);
	}

	@Override
	protected void teardownTest() {
		// no need to remove any items, integration tests are run in a separate
		// database and additionally each test changes will be roll-backed!
	}

	@Test
	public void queryPartyByTitle() {
		final JpqlQuery<Party> query = new JpqlQuery<>("SELECT p FROM Party AS p WHERE p.title = :title", Party.class);
		query.addParam("title", PARTY_TITLE);

		final QueryResult<Party> result = queryService.query(query);

		Assert.assertEquals(1, result.getResultList().size());
		Assert.assertEquals(PARTY_TITLE, result.getResultList().get(0).getTitle());
	}

	@Test
	public void queryPartyByTypeSafeQuery() {
		final LambdaQuery<Party> query = new LambdaQuery<>(Party.class);
		query.filter(q -> PARTY_TITLE.equals(q.getTitle()));

		final QueryResult<Party> result = queryService.query(query);

		Assert.assertEquals(1, result.getResultList().size());
		Assert.assertEquals(PARTY_TITLE, result.getResultList().get(0).getTitle());
	}

	@Test
	public void queryPartyByGenericAttribute() {
		final ModelQuery<Party> query = new ModelQuery<>(Party.class, Collections.singletonMap("title", PARTY_TITLE));

		final Party result = modelService.get(query);

		Assert.assertEquals(PARTY_TITLE, result.getTitle());
	}
	
	@Test
	public void queryPartyByGenericAttributeToDTO() {
		// selected columns are resolved via constructor or reflection field access!
		final JpqlQuery<PartyData> query = new JpqlQuery<>(
				"SELECT p.title AS partyTitle, p.date as partyDate FROM Party AS p WHERE p.title = :title",
				PartyData.class);
		query.addParam("title", PARTY_TITLE);

		final QueryResult<PartyData> result = queryService.query(query);

		Assert.assertEquals(1, result.getResultList().size());
		Assert.assertEquals(PartyData.class, result.getResultList().get(0).getClass());
		Assert.assertEquals(PARTY_TITLE, result.getResultList().get(0).getPartyTitle());
	}

	public static class PartyData {
		private String partyTitle;
		private LocalDate partyDate;

		public String getPartyTitle() {
			return partyTitle;
		}

		public LocalDate getPartyDate() {
			return partyDate;
		}
	}
}
```
> There are multiple ways to query data, even directly into Data Transfer Objects (DTO).

There is a whole lot of other interesting features that we haven't touched yet, like:
* Using interceptors to generate sequence numbers
* Localization features (both property and database based)
* Authentication
* Serialization
* Data import
* Spring MVC integration


### Summary
Hopefully this little quickstart tutorial gave you a (good) first impression what spOt is all about. For more infos, just dig through the (ever-growing) documentation. 


