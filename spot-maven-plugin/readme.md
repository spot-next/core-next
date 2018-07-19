# spOt Item type generation maven mojo

Generate itemtype DTOs from XSD:
1) go to: spot-core-model-infrastructure/src/main/java/
2) xjc -no-header -p at.spot.core.infrastructure.maven.xml https://raw.githubusercontent.com/mojo2012/spot/master/itemtypes/v1/itemtypes.xsd
