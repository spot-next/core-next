//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.21 um 12:13:10 PM CET 
//


package at.spot.maven.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RelationType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="RelationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OneToMany"/>
 *     &lt;enumeration value="OneToOne"/>
 *     &lt;enumeration value="ManyToMany"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RelationType")
@XmlEnum
public enum RelationType {

    @XmlEnumValue("OneToMany")
    ONE_TO_MANY("OneToMany"),
    @XmlEnumValue("OneToOne")
    ONE_TO_ONE("OneToOne"),
    @XmlEnumValue("ManyToMany")
    MANY_TO_MANY("ManyToMany");
    private final String value;

    RelationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelationType fromValue(String v) {
        for (RelationType c: RelationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
