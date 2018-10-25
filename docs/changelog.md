# Change Log

## **1.0.13-BETA** `2018-11-31`
- Fix for Hibernate indexes
- Documentation update

## **1.0.12-BETA** `2018-10-23`
- Fix for ´spot:transform-types` not working properly on windows

## **1.0.11-BETA** `2018-10-17`
- Bugfix, REST interface authentication was not checked properly
- Added SSL to REST-interface
- Hibernate performance improvements

## **1.0.10-BETA** `2018-09-23`
- Lots of fixes related to transaction handling
- Updated/fixed quickstart
- Improved Eclipse/m2e integration (types are generated and woven/transformed automatically from eclipse on workspace refresh)

## **1.0.9-BETA** `2018-09-12`
- Lots of impex fixes
- Build system fixes (type generation and transformation)
- Work on Eclipse/m2e integration
- Start of CMS infrastructure
- Archetype fixes

## **1.0.8-BETA** `2018-09-01`
- Work on the Impex infrastructure
- Archetype fixes

## **1.0.4-BETA** `2018-08-12`
- Fixed archetype-empty

## **1.0.3-BETA** `2018-08-12`
- Fixed archetype-empty
- Fixed flattened POM (deployed parent POM was unusable)

## **1.0.2-BETA** `2018-08-08`
- Fixed corrupt pom.xmls (by adding flatten-maven-plugin)
- Added simple sample app
- Fixed setting of default value during item type generation
- Fixed launch params not used when bootstraping custom module
- Fixed logging real classname in logging aspect for proxies 

## **1.0.1-BETA** `2018-08-07`
- Added "default value" handling for impex importer

## **1.0.0-BETA** `2018-08-07`
- First beta release, contains most of the planned functionality
- Basic ImpEx support
- Generic REST-service
- Basic domain model
- System initialization, update and initial/sample data import 

