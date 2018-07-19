
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RelationNode complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RelationNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modifiers" type="{}Modifiers" minOccurs="0"/>
 *         &lt;element name="validators" type="{}Validators" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="itemType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mappedBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cardinality" use="required" type="{}RelationshipCardinality" />
 *       &lt;attribute name="collectionType" type="{}CollectionsType" default="Set" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelationNode", propOrder = {
    "description",
    "modifiers",
    "validators"
})
public class RelationNode {

    protected String description;
    protected Modifiers modifiers;
    protected Validators validators;
    @XmlAttribute(name = "itemType", required = true)
    protected String itemType;
    @XmlAttribute(name = "mappedBy")
    protected String mappedBy;
    @XmlAttribute(name = "cardinality", required = true)
    protected RelationshipCardinality cardinality;
    @XmlAttribute(name = "collectionType")
    protected CollectionsType collectionType;

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
     * Ruft den Wert der itemType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Legt den Wert der itemType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemType(String value) {
        this.itemType = value;
    }

    /**
     * Ruft den Wert der mappedBy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMappedBy() {
        return mappedBy;
    }

    /**
     * Legt den Wert der mappedBy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMappedBy(String value) {
        this.mappedBy = value;
    }

    /**
     * Ruft den Wert der cardinality-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RelationshipCardinality }
     *     
     */
    public RelationshipCardinality getCardinality() {
        return cardinality;
    }

    /**
     * Legt den Wert der cardinality-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationshipCardinality }
     *     
     */
    public void setCardinality(RelationshipCardinality value) {
        this.cardinality = value;
    }

    /**
     * Ruft den Wert der collectionType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CollectionsType }
     *     
     */
    public CollectionsType getCollectionType() {
        if (collectionType == null) {
            return CollectionsType.SET;
        } else {
            return collectionType;
        }
    }

    /**
     * Legt den Wert der collectionType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CollectionsType }
     *     
     */
    public void setCollectionType(CollectionsType value) {
        this.collectionType = value;
    }

}
