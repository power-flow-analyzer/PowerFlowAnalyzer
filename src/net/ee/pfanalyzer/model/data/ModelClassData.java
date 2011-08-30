//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.29 at 02:57:02 PM MESZ 
//


package net.ee.pfanalyzer.model.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ModelClassData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ModelClassData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mehg.net/schema/PowerFlowAnalyzer}AbstractModelElementData">
 *       &lt;sequence>
 *         &lt;element name="modelClass" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}ModelClassData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="model" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}ModelData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="baseType" type="{http://www.mehg.net/schema/PowerFlowAnalyzer}BaseElementType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModelClassData", propOrder = {
    "modelClass",
    "model"
})
public class ModelClassData
    extends AbstractModelElementData
{

    protected List<ModelClassData> modelClass;
    protected List<ModelData> model;
    @XmlAttribute
    protected BaseElementType baseType;

    /**
     * Gets the value of the modelClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModelClassData }
     * 
     * 
     */
    public List<ModelClassData> getModelClass() {
        if (modelClass == null) {
            modelClass = new ArrayList<ModelClassData>();
        }
        return this.modelClass;
    }

    /**
     * Gets the value of the model property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the model property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModelData }
     * 
     * 
     */
    public List<ModelData> getModel() {
        if (model == null) {
            model = new ArrayList<ModelData>();
        }
        return this.model;
    }

    /**
     * Gets the value of the baseType property.
     * 
     * @return
     *     possible object is
     *     {@link BaseElementType }
     *     
     */
    public BaseElementType getBaseType() {
        return baseType;
    }

    /**
     * Sets the value of the baseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseElementType }
     *     
     */
    public void setBaseType(BaseElementType value) {
        this.baseType = value;
    }

}
