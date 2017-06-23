//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.28 um 02:38:38 PM CEST 
//


package at.spot.maven.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Modifiers complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Modifiers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="unique" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="readable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="writable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="isReference" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="initial" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Modifiers")
public class Modifiers {

    @XmlAttribute(name = "unique")
    protected Boolean unique;
    @XmlAttribute(name = "readable")
    protected Boolean readable;
    @XmlAttribute(name = "writable")
    protected Boolean writable;
    @XmlAttribute(name = "isReference")
    protected Boolean isReference;
    @XmlAttribute(name = "initial")
    protected Boolean initial;

    /**
     * Ruft den Wert der unique-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUnique() {
        if (unique == null) {
            return false;
        } else {
            return unique;
        }
    }

    /**
     * Legt den Wert der unique-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnique(Boolean value) {
        this.unique = value;
    }

    /**
     * Ruft den Wert der readable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReadable() {
        if (readable == null) {
            return true;
        } else {
            return readable;
        }
    }

    /**
     * Legt den Wert der readable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadable(Boolean value) {
        this.readable = value;
    }

    /**
     * Ruft den Wert der writable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isWritable() {
        if (writable == null) {
            return true;
        } else {
            return writable;
        }
    }

    /**
     * Legt den Wert der writable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWritable(Boolean value) {
        this.writable = value;
    }

    /**
     * Ruft den Wert der isReference-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsReference() {
        if (isReference == null) {
            return false;
        } else {
            return isReference;
        }
    }

    /**
     * Legt den Wert der isReference-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsReference(Boolean value) {
        this.isReference = value;
    }

    /**
     * Ruft den Wert der initial-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isInitial() {
        if (initial == null) {
            return false;
        } else {
            return initial;
        }
    }

    /**
     * Legt den Wert der initial-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInitial(Boolean value) {
        this.initial = value;
    }

}
