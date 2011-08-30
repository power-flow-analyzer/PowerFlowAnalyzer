//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.30 at 05:21:08 PM MESZ 
//


package net.ee.pfanalyzer.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CaseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CaseData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="model-db" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}ModelDBData"/>
 *         &lt;element name="network" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}NetworkData"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" default="1.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CaseData", propOrder = {
    "modelDb",
    "network"
})
public class CaseData {

    @XmlElement(name = "model-db", required = true)
    protected ModelDBData modelDb;
    @XmlElement(required = true)
    protected NetworkData network;
    @XmlAttribute
    protected String version;

    /**
     * Gets the value of the modelDb property.
     * 
     * @return
     *     possible object is
     *     {@link ModelDBData }
     *     
     */
    public ModelDBData getModelDb() {
        return modelDb;
    }

    /**
     * Sets the value of the modelDb property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelDBData }
     *     
     */
    public void setModelDb(ModelDBData value) {
        this.modelDb = value;
    }

    /**
     * Gets the value of the network property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkData }
     *     
     */
    public NetworkData getNetwork() {
        return network;
    }

    /**
     * Sets the value of the network property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkData }
     *     
     */
    public void setNetwork(NetworkData value) {
        this.network = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
