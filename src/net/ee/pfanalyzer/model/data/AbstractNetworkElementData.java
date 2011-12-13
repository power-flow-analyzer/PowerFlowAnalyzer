//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.13 at 07:50:27 PM MEZ 
//


package net.ee.pfanalyzer.model.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractNetworkElementData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractNetworkElementData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameter" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}NetworkParameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="flag" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}NetworkFlagData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="modelID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractNetworkElementData", propOrder = {
    "parameter",
    "flag"
})
public class AbstractNetworkElementData {

    protected List<NetworkParameter> parameter;
    protected List<NetworkFlagData> flag;
    @XmlAttribute
    protected String modelID;

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkParameter }
     * 
     * 
     */
    public List<NetworkParameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<NetworkParameter>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the flag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkFlagData }
     * 
     * 
     */
    public List<NetworkFlagData> getFlag() {
        if (flag == null) {
            flag = new ArrayList<NetworkFlagData>();
        }
        return this.flag;
    }

    /**
     * Gets the value of the modelID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelID() {
        return modelID;
    }

    /**
     * Sets the value of the modelID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelID(String value) {
        this.modelID = value;
    }

}
