
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationshipCardinality.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RelationshipCardinality">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="one"/>
 *     &lt;enumeration value="many"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RelationshipCardinality")
@XmlEnum
public enum RelationshipCardinality {

    @XmlEnumValue("one")
    ONE("one"),
    @XmlEnumValue("many")
    MANY("many");
    private final String value;

    RelationshipCardinality(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelationshipCardinality fromValue(String v) {
        for (RelationshipCardinality c: RelationshipCardinality.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
