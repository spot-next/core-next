
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DatabaseColumnType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DatabaseColumnType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DEFAULT"/>
 *     &lt;enumeration value="CHAR"/>
 *     &lt;enumeration value="VARCHAR"/>
 *     &lt;enumeration value="LONGVARCHAR"/>
 *     &lt;enumeration value="BLOB"/>
 *     &lt;enumeration value="CLOB"/>
 *     &lt;enumeration value="TIME"/>
 *     &lt;enumeration value="TIMESTAMP"/>
 *     &lt;enumeration value="DATE"/>
 *     &lt;enumeration value="VARBINARY"/>
 *     &lt;enumeration value="BIT"/>
 *     &lt;enumeration value="DOUBLE"/>
 *     &lt;enumeration value="FLOAT"/>
 *     &lt;enumeration value="TINYINT"/>
 *     &lt;enumeration value="SMALLINT"/>
 *     &lt;enumeration value="INTEGER"/>
 *     &lt;enumeration value="BIGINT"/>
 *     &lt;enumeration value="NUMERIC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DatabaseColumnType")
@XmlEnum
public enum DatabaseColumnType {


    /**
     * Empty placeholder, type will be automatically detected.
     * 
     */
    DEFAULT,

    /**
     * Single char
     * 
     */
    CHAR,

    /**
     * Short string
     * 
     */
    VARCHAR,

    /**
     * Long string or text
     * 
     */
    LONGVARCHAR,

    /**
     * Large binary object
     * 
     */
    BLOB,

    /**
     * Large character object
     * 
     */
    CLOB,

    /**
     * Time
     * 
     */
    TIME,

    /**
     * Timestamp
     * 
     */
    TIMESTAMP,

    /**
     * Date
     * 
     */
    DATE,

    /**
     * Byte array
     * 
     */
    VARBINARY,

    /**
     *  0 or 1, like a boolean
     * 
     */
    BIT,

    /**
     * Double
     * 
     */
    DOUBLE,

    /**
     * Float
     * 
     */
    FLOAT,

    /**
     * Byte
     * 
     */
    TINYINT,

    /**
     * Short
     * 
     */
    SMALLINT,

    /**
     * Integer
     * 
     */
    INTEGER,

    /**
     * BigInteger
     * 
     */
    BIGINT,

    /**
     * BigDecimal
     * 
     */
    NUMERIC;

    public String value() {
        return name();
    }

    public static DatabaseColumnType fromValue(String v) {
        return valueOf(v);
    }

}
