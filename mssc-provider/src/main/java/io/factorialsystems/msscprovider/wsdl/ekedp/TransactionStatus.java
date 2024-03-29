//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.12.19 at 01:39:42 PM WAT 
//


package io.factorialsystems.msscprovider.wsdl.ekedp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PENDING"/&gt;
 *     &lt;enumeration value="CONFIRMED"/&gt;
 *     &lt;enumeration value="DECLINED"/&gt;
 *     &lt;enumeration value="EXPIRED"/&gt;
 *     &lt;enumeration value="CORRUPTED"/&gt;
 *     &lt;enumeration value="REVERSED"/&gt;
 *     &lt;enumeration value="STAGED_FOR_CREDITING"/&gt;
 *     &lt;enumeration value="CANCELLED_BEFORE_EXECUTION"/&gt;
 *     &lt;enumeration value="TOKEN_VEND_FAILED"/&gt;
 *     &lt;enumeration value="TOKEN_VEND_DELAYED"/&gt;
 *     &lt;enumeration value="AWAITING_SERVICE_PROVIDER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "transactionStatus")
@XmlEnum
public enum TransactionStatus {

    PENDING,
    CONFIRMED,
    DECLINED,
    EXPIRED,
    CORRUPTED,
    REVERSED,
    STAGED_FOR_CREDITING,
    CANCELLED_BEFORE_EXECUTION,
    TOKEN_VEND_FAILED,
    TOKEN_VEND_DELAYED,
    AWAITING_SERVICE_PROVIDER;

    public String value() {
        return name();
    }

    public static TransactionStatus fromValue(String v) {
        return valueOf(v);
    }

}
