//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.10.24 um 08:58:46 PM CEST 
//


package at.spot.core.infrastructure.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Accessors complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
     * Ruft den Wert der valueProvider-Eigenschaft ab.
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
     * Legt den Wert der valueProvider-Eigenschaft fest.
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
     * Ruft den Wert der getter-Eigenschaft ab.
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
     * Legt den Wert der getter-Eigenschaft fest.
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
     * Ruft den Wert der setter-Eigenschaft ab.
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
     * Legt den Wert der setter-Eigenschaft fest.
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
