# spOt Item type generation maven mojo

Generate itemtype DTOs from XSD:
1) go to: spot-core-model-infrastructure/src/main/java/
2) xjc -no-header -p at.spot.core.infrastructure.maven.xml ../../../../spot-core-base-models/src/main/resources/itemtypes.xsd
