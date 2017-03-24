# spOt micro framework

## Idea
The idea of the spOt micro framework is to take the good parts of the SAP Hybris framework and bring them to the next level.
For those who don't know SAP Hybris: it's a enterprise-grade ecommerce framework with it's own custom object-oriented persistence layer.
You don't need to setup database tables or anything like that - all models are generated from XML definitions. It allows to extend all models, even those who are defined by the framework itself. This makes it pretty easy to add new properties to types like *Product*, *Category* or *Customer*.

Additionally the framework also offers the so called "Accelerator". A demo-shop/website (B2C, B2B, telco, insurance) that can be used as starting point for your ecommerce project.

To sum up the good things:
* Very flexible/adaptable and well-defined ecommerce domain model
* Services available for all sorts of problem domains: cart manipulation, product search, authentication and many more
* Demo-shop can be used as starting point

Sound pretty cool, huh?
.... WELL. It also has a lot of downsides.

Building (ant clean all) the entire suite takes up to 5 minutes. Startup time is about 4 minutes. Hot-code replacement - event with JRebel - doesn't work very well.
At the end of the day you probably spent half of your day waiting for Hybris to boot up after your changes and the other half debugging decompiled code ....

Also the readily-available services are often over-engineerd and hard to customize, sometimes the code quality is very poor, sometimes you simply don't know if it's better and faster to rewrite an entire function block or find out how the Hybris functionality works (hello approval process, I'm looking at you).

Of course, documentation is ... scarce and mostly useless.

So what's the idea behind spOt?

* Pure-java based persistence layer
* Flexible and adaptable type system
* Lightweight service-oriented architecture
* Microservice-oriented approach: for services both a "local" and "remote" implemenatation is available. So spOt nodes can interact naturally with each other

## Current state
* Persistence layer implemented with MapDB (quite stable and fast)
* Type generation from XML (maven plugin)
* Basic services infrastructure for model model handling and quering, localization/internationalization, security/authentication, serialization
* REST services for querying type system and models (read-write)
* Tomcat/spring-mvc integration (spOt is loaded as spring parent context)

## Roadmap
* Expose model-related services as REST endpoints, so spOt nodes can interact with each other
* Basic framework for ecommerce-related functionality


# spOt micro framework

## Idea
The idea of the spOt micro framework is to take the good parts of the SAP Hybris framework and bring them to the next level.
For those who don't know SAP Hybris: it's a enterprise-grade ecommerce framework with it's own custom object-oriented persistence layer.
You don't need to setup database tables or anything like that - all models are generated from XML definitions. It allows to extend all models, even those who are defined by the framework itself. This makes it pretty easy to add new properties to types like *Product*, *Category* or *Customer*.

Additionally the framework also offers the so called "Accelerator". A demo-shop/website (B2C, B2B, telco, insurance) that can be used as starting point for your ecommerce project.

To sum up the good things:
* Very flexible/adaptable and well-defined ecommerce domain model
* Services available for all sorts of problem domains: cart manipulation, product search, authentication and many more
* Demo-shop can be used as starting point

Sound pretty cool, huh?
.... WELL. It also has a lot of downsides.

Building (ant clean all) the entire suite takes up to 5 minutes. Startup time is about 4 minutes. Hot-code replacement - event with JRebel - doesn't work very well.
At the end of the day you probably spent half of your day waiting for Hybris to boot up after your changes and the other half debugging decompiled code ....

Also the readily-available services are often over-engineerd and hard to customize, sometimes the code quality is very poor, sometimes you simply don't know if it's better and faster to rewrite an entire function block or find out how the Hybris functionality works (hello approval process, I'm looking at you).

Of course, documentation is ... scarce and mostly useless.

So what's the idea behind spOt?

* Pure-java based persistence layer
* Flexible and adaptable type system
* Lightweight service-oriented architecture
* Microservice-oriented approach: for services both a "local" and "remote" implemenatation is available. So spOt nodes can interact naturally with each other

## Current state
* Persistence layer implemented with MapDB (quite stable and fast)
* Type generation from XML (maven plugin)
* Basic services infrastructure for model model handling and quering, localization/internationalization, security/authentication, serialization
* REST services for querying type system and models (read-write)
* Tomcat/spring-mvc integration (spOt is loaded as spring parent context)

## Roadmap
* Expose model-related services as REST endpoints, so spOt nodes can interact with each other
* Basic framework for ecommerce-related functionality


