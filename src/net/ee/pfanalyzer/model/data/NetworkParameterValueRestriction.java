//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.09.13 at 12:20:21 PM MESZ 
//


package net.ee.pfanalyzer.model.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkParameterValueRestriction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NetworkParameterValueRestriction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="bus-number"/>
 *     &lt;enumeration value="list"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NetworkParameterValueRestriction")
@XmlEnum
public enum NetworkParameterValueRestriction {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("bus-number")
    BUS_NUMBER("bus-number"),
    @XmlEnumValue("list")
    LIST("list");
    private final String value;

    NetworkParameterValueRestriction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NetworkParameterValueRestriction fromValue(String v) {
        for (NetworkParameterValueRestriction c: NetworkParameterValueRestriction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
