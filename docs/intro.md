# Introduction
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.spot-next/spot-framework/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.spot-next/spot-framework)
[![Apache 2.0 License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Join the chat at https://gitter.im/spot-next/Lobby](https://badges.gitter.im/spot-next/Lobby.svg)](https://gitter.im/spot-next/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The idea of the spOt microservice framework is to take the good parts of the SAP Hybris framework and bring them to the next level. For those who don't know SAP Hybris: it's a enterprise-grade ecommerce framework with it's own custom object-oriented persistence layer. You don't need to setup database tables or anything like that - all models are generated from XML definitions. It allows you to extend all models, even those who are defined by the framework itself. This makes it pretty easy to add new properties to types like Product, Category or Customer.

Additionally, the framework also offers the so called "Accelerator". A demo-shop/website (B2C, B2B, telco, insurance) that can be used as a starting point for your ecommerce project.

**To sum up the good things:**
 
> * Very flexible/adaptable and well-defined ecommerce domain model
> * Services available for all sorts of problem domains: cart manipulation, product search, authentication and many more
> * Demo-shop can be used as starting point

Sound pretty cool, huh? .... WELL. It also has a lot of downsides.

Building the entire suite takes up to 3-7 minutes - depending on the project setup. Startup time is about 4-6 minutes too. Hot-code replacement - event with JRebel - doesn't work very well. At the end of the day you probably spent half of your day waiting for Hybris to boot up after you made changes to the code, and the other half debugging decompiled Hybris code ....

Also the readily-available services are often over-engineerd and hard to customize, sometimes the code quality is very poor, sometimes you simply don't know if it's better and faster to rewrite an entire function block or find out how the Hybris functionality works (hello approval process, I'm looking at you).

Of course, documentation is ... scarce and mostly useless. Out of frustration this project was born!

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

All these various technologies are integrated (actually abstracted away) and follow the **Principle of Least Surprise**. Ever tried to **serialize a Hibernate entity** using Gson or Jackson? Have fun!

Not only does serialization work as expected - spOt even provides a generic REST interface for you, to manipulate every aspect of your domain objects.
Every (new) type is automatically available as REST endpoint, offering functionality like search/filter, load, save, delete and update single properties.

### Sane defaults
The open-source world spawned some very awesome peaces of software. But sometimes they are a bit overengineered and hard to use for common scenarios. spOt tried to **dumb down the APIs** and make them more **usable by setting better defaults** and **hiding the most obscure features**.

Probably every newcomer to Hibernate is struck by awe when seeing the "FETCH JOIN" in work and a few days is struck by anger only to see it fail with some obscure exceptions like:

```
org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags
```

Don't worry, although Hibernate is used for persistence, such thing never can happen to you. I could go on for hours and whine about the hundreds of hours that I lost debugging the insane behaviour and pitfalls ... but I won't bore you with the details ;-)

You are still here? Well, then let's better get started!

## Contact & Support
* Create a [GitHub issue](https://github.com/spot-next/spot-framework/issues) for bug reports, feature requests, or questions
* Join [spot-next@gitter](https://gitter.im/spot-next/Lobby) for announcements
* Add a ⭐️ [star on GitHub](https://github.com/spot-next/spot-framework) or ❤️ to support the project!


## License
This project is licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)

Copyright (c) 2018 Matthias Fuchs

"spot next" and the "spot next" logo are a registered trademark of Matthias Fuchs


