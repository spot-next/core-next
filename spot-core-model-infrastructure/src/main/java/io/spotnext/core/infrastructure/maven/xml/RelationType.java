
package io.spotnext.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the source property.
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
     * Sets the value of the source property.
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
     * Gets the value of the target property.
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
     * Sets the value of the target property.
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
