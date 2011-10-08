//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.08 at 04:37:16 PM MESZ 
//


package net.ee.pfanalyzer.model.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkParameterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NetworkParameterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="text"/>
 *     &lt;enumeration value="integer"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="boolean"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NetworkParameterType")
@XmlEnum
public enum NetworkParameterType {

    @XmlEnumValue("text")
    TEXT("text"),
    @XmlEnumValue("integer")
    INTEGER("integer"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("boolean")
    BOOLEAN("boolean");
    private final String value;

    NetworkParameterType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NetworkParameterType fromValue(String v) {
        for (NetworkParameterType c: NetworkParameterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
