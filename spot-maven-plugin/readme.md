# spOt Item type generation maven mojo

Generate itemtype DTOs from XSD:
1) go to: spot-core-infrastructure/src/main/java/
2) xjc -no-header -p io.spotnext.infrastructure.maven.xml ../../../../docs/schemas/v2/itemtypes.xsd
