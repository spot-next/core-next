# Roadmap

## For the future
* Messaging service infrastructure
* Rule service infrastructure
* (Generic) Business processes
* Administration web interface (for CRUD operations)
* ACL support
* Audit logging
* Search integration (solr etc)

## 1.2.x
> Release date: approx. end of February 2018
* Better DTO projections (without the need for explicite aliases)
* Better LocalizedString support (other localized types too)
* XML support for REST interface

## 1.1.x
> Release date: approx. end of December 2018
* Transparent persistence operation over REST interface
* More ImpEx features
* Cronjob scheduling

## 1.0.x
> Release date: approx. end of August 2018

The upcomping 1.0.x release contains the following features:
* Complete persistence layer based on Hibernate
 * Localized (string) properties
 * Bi-directional relational mapping (update relations from both side)
 * Type system initialization and update
* Import of initial and sample data (usng ImpEx format) 
* Core services: localization, persistence, validation, ...
* Generic REST CRUD service 
* Maven starter archetype   