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

With this command you can directly initializes an empty spOt project.
```bash
mvn archetype:generate -B \
		-DarchetypeGroupId=io.spot-next.archetypes \
		-DarchetypeArtifactId=archetype-empty \
		-DgroupId=io.spot-next.test \
		-DartifactId=test-project \
		-Dpackage=io.spotnext.test \
		-Dversion=1.0-SNAPSHOT
```
> The maven artifact values are directly passed as command line arguments

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
Cache-Control: no-cache
Postman-Token: e1cfcf59-aa45-40ea-9931-29f1e5990ae7
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

Currently we don't have any custom domain model types configured. So let's head on to the next chapture.

### Adapt domain model
Let's say our project goal is to offer a party guest lits service:
* CRUD-REST interface to create parties, locations add guests
* Automatically send party confirmation emails as soon as a party's date, location and guest list has been fixed
* Automatically send party invidation emails when new guests are registered
* Offer a way to customize email templates

Before we are going to implement the actual functionality, we start by modelling the domain objects. Among the predefined types `User` and `Address` seem suitable candidates for "party guest" and "location".
But we definitely need a new model type `Party`.
> In the JPA/HIbernate world this would be called an "Entity". The corresponding spOt terminology is "Item type".

Open the `src/main/resources/test-project-itemtypes.xml` file - it contains the type definitions.     
Common Java IDEs like Eclipse or IntelliJ offer auto-completion in XML files. This comes in handy to explore the possible constructs (in a type-safe and correct way!).

First we add the new `Party` type:
```xml
<type name="Party" package="io.spotnext.test.itemtype.Party">
	<properties>
		<property name="title" type="String">
			<description>The unique title of the party</description>
			<validators>
				<validator javaClass="javax.validation.constraints.NotNull" />
			</validators>
			<modifiers unique="true" />
		</property>
		<property name="location" type="Address">
			<description>The location the party will take place</description>
		</property>
	</properties>
</type>
```
Both the **name and the package are mandatory** as some java code is generated out of this XML snippet. Every item has a unique `PK` to distinquish different objects in the database. Additionally we added the unique`-modifier to the `title` property. This creates another database constraint that only allows one `Party` with the same title.
The validator element `javax.validation.constraints.NotNull` adds a JSR-303 validation to the property. Basicalyl it means that the value may not be `null` when saving.
The description elements will be rendered as Javadoc.

What we are still missing is list of guests. Therea are two options to model collections and maps: a  


### Implement service


### Summary



