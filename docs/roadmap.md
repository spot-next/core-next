# Roadmap

## **For the future**
* Messaging service infrastructure
* Rule service infrastructure
* (Generic) Business processes
* Administration web interface (for CRUD operations)
* ACL support
* Audit logging
* Search integration (solr etc)

## **1.2.x** `~EOF June 2019`

* Better DTO projections (without the need for explicite aliases)
* Better LocalizedString support (other localized types too)
* XML support for REST interface
* REST interface for JPQL
* Spring MVC integration

## **1.1.x** `~EOF March 2018`

* Transparent persistence operation over REST interface
* More ImpEx features
* Cronjob scheduling
* OpenAPI REST interface documentation
* Generated API docs
* Javadoc

## **1.0.x** `~EOF January 2018`

The upcomping 1.0.x release contains the following features:
* Complete persistence layer based on Hibernate
 * Localized (string) properties
 * Bi-directional relational mapping (update relations from both side)
 * Type system initialization and update
* Import of initial and sample data (usng ImpEx format) 
* Core services: localization, persistence, validation, ...
* Generic REST CRUD service 
* Maven starter archetype   
