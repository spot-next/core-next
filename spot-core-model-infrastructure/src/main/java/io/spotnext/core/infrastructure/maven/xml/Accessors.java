
package io.spotnext.core.infrastructure.maven.xml;

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

}
