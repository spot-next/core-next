
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für Persistence complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Persistence">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="columnType" use="required" type="{}DatabaseColumnType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Persistence", propOrder = {
    "content"
})
public class Persistence {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "columnType", required = true)
    protected DatabaseColumnType columnType;

    /**
     * Ruft den Wert der content-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Legt den Wert der content-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Ruft den Wert der columnType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseColumnType }
     *     
     */
    public DatabaseColumnType getColumnType() {
        return columnType;
    }

    /**
     * Legt den Wert der columnType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseColumnType }
     *     
     */
    public void setColumnType(DatabaseColumnType value) {
        this.columnType = value;
    }

}
