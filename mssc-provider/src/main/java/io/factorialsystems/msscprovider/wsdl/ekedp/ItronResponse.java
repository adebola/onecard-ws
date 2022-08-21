//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.12.19 at 01:39:42 PM WAT 
//


package io.factorialsystems.msscprovider.wsdl.ekedp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for itronResponse.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="itronResponse"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="elec000"/&gt;
 *     &lt;enumeration value="elec001"/&gt;
 *     &lt;enumeration value="elec002"/&gt;
 *     &lt;enumeration value="elec003"/&gt;
 *     &lt;enumeration value="elec004"/&gt;
 *     &lt;enumeration value="elec005"/&gt;
 *     &lt;enumeration value="elec010"/&gt;
 *     &lt;enumeration value="elec011"/&gt;
 *     &lt;enumeration value="elec012"/&gt;
 *     &lt;enumeration value="elec013"/&gt;
 *     &lt;enumeration value="elec014"/&gt;
 *     &lt;enumeration value="elec015"/&gt;
 *     &lt;enumeration value="elec016"/&gt;
 *     &lt;enumeration value="elec017"/&gt;
 *     &lt;enumeration value="elec018"/&gt;
 *     &lt;enumeration value="elec019"/&gt;
 *     &lt;enumeration value="elec020"/&gt;
 *     &lt;enumeration value="elec021"/&gt;
 *     &lt;enumeration value="elec022"/&gt;
 *     &lt;enumeration value="elec023"/&gt;
 *     &lt;enumeration value="elec028"/&gt;
 *     &lt;enumeration value="elec029"/&gt;
 *     &lt;enumeration value="elec030"/&gt;
 *     &lt;enumeration value="elec031"/&gt;
 *     &lt;enumeration value="elec032"/&gt;
 *     &lt;enumeration value="elec033"/&gt;
 *     &lt;enumeration value="elec034"/&gt;
 *     &lt;enumeration value="elec041"/&gt;
 *     &lt;enumeration value="elec900"/&gt;
 *     &lt;enumeration value="elec901"/&gt;
 *     &lt;enumeration value="elec902"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "itronResponse")
@XmlEnum
public enum ItronResponse {

    @XmlEnumValue("elec000")
    ELEC_000("elec000"),
    @XmlEnumValue("elec001")
    ELEC_001("elec001"),
    @XmlEnumValue("elec002")
    ELEC_002("elec002"),
    @XmlEnumValue("elec003")
    ELEC_003("elec003"),
    @XmlEnumValue("elec004")
    ELEC_004("elec004"),
    @XmlEnumValue("elec005")
    ELEC_005("elec005"),
    @XmlEnumValue("elec010")
    ELEC_010("elec010"),
    @XmlEnumValue("elec011")
    ELEC_011("elec011"),
    @XmlEnumValue("elec012")
    ELEC_012("elec012"),
    @XmlEnumValue("elec013")
    ELEC_013("elec013"),
    @XmlEnumValue("elec014")
    ELEC_014("elec014"),
    @XmlEnumValue("elec015")
    ELEC_015("elec015"),
    @XmlEnumValue("elec016")
    ELEC_016("elec016"),
    @XmlEnumValue("elec017")
    ELEC_017("elec017"),
    @XmlEnumValue("elec018")
    ELEC_018("elec018"),
    @XmlEnumValue("elec019")
    ELEC_019("elec019"),
    @XmlEnumValue("elec020")
    ELEC_020("elec020"),
    @XmlEnumValue("elec021")
    ELEC_021("elec021"),
    @XmlEnumValue("elec022")
    ELEC_022("elec022"),
    @XmlEnumValue("elec023")
    ELEC_023("elec023"),
    @XmlEnumValue("elec028")
    ELEC_028("elec028"),
    @XmlEnumValue("elec029")
    ELEC_029("elec029"),
    @XmlEnumValue("elec030")
    ELEC_030("elec030"),
    @XmlEnumValue("elec031")
    ELEC_031("elec031"),
    @XmlEnumValue("elec032")
    ELEC_032("elec032"),
    @XmlEnumValue("elec033")
    ELEC_033("elec033"),
    @XmlEnumValue("elec034")
    ELEC_034("elec034"),
    @XmlEnumValue("elec041")
    ELEC_041("elec041"),
    @XmlEnumValue("elec900")
    ELEC_900("elec900"),
    @XmlEnumValue("elec901")
    ELEC_901("elec901"),
    @XmlEnumValue("elec902")
    ELEC_902("elec902");
    private final String value;

    ItronResponse(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ItronResponse fromValue(String v) {
        for (ItronResponse c: ItronResponse.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}