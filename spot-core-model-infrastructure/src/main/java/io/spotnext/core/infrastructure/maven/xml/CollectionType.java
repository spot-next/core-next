
package io.spotnext.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CollectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{}BaseType">
 *       &lt;attribute name="elementType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="collectionType" type="{}CollectionsType" default="List" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectionType")
public class CollectionType
    extends BaseType
{

    @XmlAttribute(name = "elementType", required = true)
    protected String elementType;
    @XmlAttribute(name = "collectionType")
    protected CollectionsType collectionType;

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementType(String value) {
        this.elementType = value;
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
            return CollectionsType.LIST;
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
