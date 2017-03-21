//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.21 um 12:13:10 PM CET 
//


package at.spot.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Defines the relation settings of the property.
 * 			
 * 
 * <p>Java-Klasse für Relation complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Relation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="type" type="{}RelationType" default="OneToMany" />
 *       &lt;attribute name="referencedType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mappedTo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="casacadeOnDelete" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Relation")
public class Relation {

    @XmlAttribute(name = "type")
    protected RelationType type;
    @XmlAttribute(name = "referencedType", required = true)
    protected String referencedType;
    @XmlAttribute(name = "mappedTo", required = true)
    protected String mappedTo;
    @XmlAttribute(name = "casacadeOnDelete")
    protected Boolean casacadeOnDelete;

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RelationType }
     *     
     */
    public RelationType getType() {
        if (type == null) {
            return RelationType.ONE_TO_MANY;
        } else {
            return type;
        }
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationType }
     *     
     */
    public void setType(RelationType value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der referencedType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencedType() {
        return referencedType;
    }

    /**
     * Legt den Wert der referencedType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencedType(String value) {
        this.referencedType = value;
    }

    /**
     * Ruft den Wert der mappedTo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMappedTo() {
        return mappedTo;
    }

    /**
     * Legt den Wert der mappedTo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMappedTo(String value) {
        this.mappedTo = value;
    }

    /**
     * Ruft den Wert der casacadeOnDelete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCasacadeOnDelete() {
        if (casacadeOnDelete == null) {
            return false;
        } else {
            return casacadeOnDelete;
        }
    }

    /**
     * Legt den Wert der casacadeOnDelete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCasacadeOnDelete(Boolean value) {
        this.casacadeOnDelete = value;
    }

}
