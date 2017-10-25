
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CollectionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{}BaseType">
 *       &lt;attribute name="elementType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="collectionType" use="required" type="{}CollectionsType" />
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
    @XmlAttribute(name = "collectionType", required = true)
    protected CollectionsType collectionType;

    /**
     * Ruft den Wert der elementType-Eigenschaft ab.
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
     * Legt den Wert der elementType-Eigenschaft fest.
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
     * Ruft den Wert der collectionType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CollectionsType }
     *     
     */
    public CollectionsType getCollectionType() {
        return collectionType;
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
