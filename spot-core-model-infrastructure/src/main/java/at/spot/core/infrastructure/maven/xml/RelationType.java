
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RelationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RelationType">
 *   &lt;complexContent>
 *     &lt;extension base="{}BaseType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{}RelationNode"/>
 *         &lt;element name="target" type="{}RelationNode"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelationType", propOrder = {
    "source",
    "target"
})
public class RelationType
    extends BaseType
{

    @XmlElement(required = true)
    protected RelationNode source;
    @XmlElement(required = true)
    protected RelationNode target;

    /**
     * Ruft den Wert der source-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RelationNode }
     *     
     */
    public RelationNode getSource() {
        return source;
    }

    /**
     * Legt den Wert der source-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationNode }
     *     
     */
    public void setSource(RelationNode value) {
        this.source = value;
    }

    /**
     * Ruft den Wert der target-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RelationNode }
     *     
     */
    public RelationNode getTarget() {
        return target;
    }

    /**
     * Legt den Wert der target-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationNode }
     *     
     */
    public void setTarget(RelationNode value) {
        this.target = value;
    }

}
