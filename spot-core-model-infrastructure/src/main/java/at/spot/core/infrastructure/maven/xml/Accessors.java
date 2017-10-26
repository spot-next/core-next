
package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Accessors complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Accessors">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="valueProvider" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="getter" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="setter" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Accessors")
public class Accessors {

    @XmlAttribute(name = "valueProvider")
    protected String valueProvider;
    @XmlAttribute(name = "getter")
    protected Boolean getter;
    @XmlAttribute(name = "setter")
    protected Boolean setter;

    /**
     * Gets the value of the valueProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueProvider() {
        return valueProvider;
    }

    /**
     * Sets the value of the valueProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueProvider(String value) {
        this.valueProvider = value;
    }

    /**
     * Gets the value of the getter property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isGetter() {
        if (getter == null) {
            return true;
        } else {
            return getter;
        }
    }

    /**
     * Sets the value of the getter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGetter(Boolean value) {
        this.getter = value;
    }

    /**
     * Gets the value of the setter property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSetter() {
        if (setter == null) {
            return true;
        } else {
            return setter;
        }
    }

    /**
     * Sets the value of the setter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSetter(Boolean value) {
        this.setter = value;
    }

}
