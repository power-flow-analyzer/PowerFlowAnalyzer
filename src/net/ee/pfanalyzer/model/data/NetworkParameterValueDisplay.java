//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.08 at 04:37:16 PM MESZ 
//


package net.ee.pfanalyzer.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkParameterValueDisplay complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetworkParameterValueDisplay">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="increment" type="{http://www.w3.org/2001/XMLSchema}double" default="1" />
 *       &lt;attribute name="precision" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="decimalFormatPattern" type="{http://www.w3.org/2001/XMLSchema}string" default="#.########" />
 *       &lt;attribute name="parameterID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="elementRestriction" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkParameterValueDisplay")
public class NetworkParameterValueDisplay {

    @XmlAttribute
    protected Double min;
    @XmlAttribute
    protected Double max;
    @XmlAttribute
    protected Double increment;
    @XmlAttribute
    protected Double precision;
    @XmlAttribute
    protected String decimalFormatPattern;
    @XmlAttribute
    protected String parameterID;
    @XmlAttribute
    protected String elementRestriction;

    /**
     * Gets the value of the min property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMin(Double value) {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMax(Double value) {
        this.max = value;
    }

    /**
     * Gets the value of the increment property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getIncrement() {
        if (increment == null) {
            return  1.0D;
        } else {
            return increment;
        }
    }

    /**
     * Sets the value of the increment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setIncrement(Double value) {
        this.increment = value;
    }

    /**
     * Gets the value of the precision property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the precision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPrecision(Double value) {
        this.precision = value;
    }

    /**
     * Gets the value of the decimalFormatPattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecimalFormatPattern() {
        if (decimalFormatPattern == null) {
            return "#.########";
        } else {
            return decimalFormatPattern;
        }
    }

    /**
     * Sets the value of the decimalFormatPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecimalFormatPattern(String value) {
        this.decimalFormatPattern = value;
    }

    /**
     * Gets the value of the parameterID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameterID() {
        return parameterID;
    }

    /**
     * Sets the value of the parameterID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameterID(String value) {
        this.parameterID = value;
    }

    /**
     * Gets the value of the elementRestriction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementRestriction() {
        return elementRestriction;
    }

    /**
     * Sets the value of the elementRestriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementRestriction(String value) {
        this.elementRestriction = value;
    }

}
