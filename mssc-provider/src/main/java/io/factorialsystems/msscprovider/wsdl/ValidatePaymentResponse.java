//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.12.19 at 01:39:42 PM WAT 
//


package io.factorialsystems.msscprovider.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validatePaymentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validatePaymentResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="response" type="{http://soap.convergenceondemand.net/TMP/}validatePaymentResponseV2" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validatePaymentResponse", propOrder = {
    "response"
})
public class ValidatePaymentResponse {

    protected ValidatePaymentResponseV2 response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link ValidatePaymentResponseV2 }
     *     
     */
    public ValidatePaymentResponseV2 getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidatePaymentResponseV2 }
     *     
     */
    public void setResponse(ValidatePaymentResponseV2 value) {
        this.response = value;
    }

}
