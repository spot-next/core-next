//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.28 um 02:38:38 PM CEST 
//


package at.spot.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Property complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Property">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datatype" type="{}DataType"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modifiers" type="{}Modifiers" minOccurs="0"/>
 *         &lt;element name="accessors" type="{}Accessors" minOccurs="0"/>
 *         &lt;element name="relation" type="{}Relation" minOccurs="0"/>
 *         &lt;element name="validators" type="{}Validators" minOccurs="0"/>
 *         &lt;element name="defaultValue" type="{}DefaultValue" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", propOrder = {
    "datatype",
    "description",
    "modifiers",
    "accessors",
    "relation",
    "validators",
    "defaultValue"
})
public class Property {

    @XmlElement(required = true)
    protected DataType datatype;
    protected String description;
    protected Modifiers modifiers;
    protected Accessors accessors;
    protected Relation relation;
    protected Validators validators;
    protected DefaultValue defaultValue;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Ruft den Wert der datatype-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DataType }
     *     
     */
    public DataType getDatatype() {
        return datatype;
    }

    /**
     * Legt den Wert der datatype-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DataType }
     *     
     */
    public void setDatatype(DataType value) {
        this.datatype = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der modifiers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Modifiers }
     *     
     */
    public Modifiers getModifiers() {
        return modifiers;
    }

    /**
     * Legt den Wert der modifiers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Modifiers }
     *     
     */
    public void setModifiers(Modifiers value) {
        this.modifiers = value;
    }

    /**
     * Ruft den Wert der accessors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Accessors }
     *     
     */
    public Accessors getAccessors() {
        return accessors;
    }

    /**
     * Legt den Wert der accessors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Accessors }
     *     
     */
    public void setAccessors(Accessors value) {
        this.accessors = value;
    }

    /**
     * Ruft den Wert der relation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Relation }
     *     
     */
    public Relation getRelation() {
        return relation;
    }

    /**
     * Legt den Wert der relation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Relation }
     *     
     */
    public void setRelation(Relation value) {
        this.relation = value;
    }

    /**
     * Ruft den Wert der validators-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Validators }
     *     
     */
    public Validators getValidators() {
        return validators;
    }

    /**
     * Legt den Wert der validators-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Validators }
     *     
     */
    public void setValidators(Validators value) {
        this.validators = value;
    }

    /**
     * Ruft den Wert der defaultValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DefaultValue }
     *     
     */
    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    /**
     * Legt den Wert der defaultValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DefaultValue }
     *     
     */
    public void setDefaultValue(DefaultValue value) {
        this.defaultValue = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
