
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the description property.
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
     * Sets the value of the description property.
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
     * Gets the value of the modifiers property.
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
     * Sets the value of the modifiers property.
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
     * Gets the value of the validators property.
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
     * Sets the value of the validators property.
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
     * Gets the value of the itemType property.
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
     * Sets the value of the itemType property.
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
     * Gets the value of the mappedBy property.
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
     * Sets the value of the mappedBy property.
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
     * Gets the value of the cardinality property.
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
     * Sets the value of the cardinality property.
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
     * Gets the value of the collectionType property.
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
     * Sets the value of the collectionType property.
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
