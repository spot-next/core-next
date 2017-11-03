
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RelationshipType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="RelationshipType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OneToMany"/>
 *     &lt;enumeration value="OneToOne"/>
 *     &lt;enumeration value="ManyToMany"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RelationshipType")
@XmlEnum
public enum RelationshipType {

    @XmlEnumValue("OneToMany")
    ONE_TO_MANY("OneToMany"),
    @XmlEnumValue("OneToOne")
    ONE_TO_ONE("OneToOne"),
    @XmlEnumValue("ManyToMany")
    MANY_TO_MANY("ManyToMany");
    private final String value;

    RelationshipType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelationshipType fromValue(String v) {
        for (RelationshipType c: RelationshipType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
