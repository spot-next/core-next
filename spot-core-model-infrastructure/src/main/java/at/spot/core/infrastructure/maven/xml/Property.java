
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modifiers" type="{}Modifiers" minOccurs="0"/>
 *         &lt;element name="accessors" type="{}Accessors" minOccurs="0"/>
 *         &lt;element name="validators" type="{}Validators" minOccurs="0"/>
 *         &lt;element name="defaultValue" type="{}DefaultValue" minOccurs="0"/>
 *         &lt;element name="persistence" type="{}Persistence" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", propOrder = {
    "description",
    "modifiers",
    "accessors",
    "validators",
    "defaultValue",
    "persistence"
})
public class Property {

    protected String description;
    protected Modifiers modifiers;
    protected Accessors accessors;
    protected Validators validators;
    protected DefaultValue defaultValue;
    protected Persistence persistence;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected String type;

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
     * Ruft den Wert der persistence-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Persistence }
     *     
     */
    public Persistence getPersistence() {
        return persistence;
    }

    /**
     * Legt den Wert der persistence-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Persistence }
     *     
     */
    public void setPersistence(Persistence value) {
        this.persistence = value;
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

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
