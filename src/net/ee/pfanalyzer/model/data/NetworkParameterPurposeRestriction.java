//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.11.06 at 04:24:27 PM MEZ 
//


package net.ee.pfanalyzer.model.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkParameterPurposeRestriction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NetworkParameterPurposeRestriction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="parameter"/>
 *     &lt;enumeration value="result"/>
 *     &lt;enumeration value="scenario"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NetworkParameterPurposeRestriction")
@XmlEnum
public enum NetworkParameterPurposeRestriction {

    @XmlEnumValue("parameter")
    PARAMETER("parameter"),
    @XmlEnumValue("result")
    RESULT("result"),
    @XmlEnumValue("scenario")
    SCENARIO("scenario");
    private final String value;

    NetworkParameterPurposeRestriction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NetworkParameterPurposeRestriction fromValue(String v) {
        for (NetworkParameterPurposeRestriction c: NetworkParameterPurposeRestriction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
