//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.12.19 at 01:39:42 PM WAT 
//


package io.factorialsystems.msscprovider.wsdl.ekedp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validatePaymentResponseV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validatePaymentResponseV2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://soap.convergenceondemand.net/TMP/}baseResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="validationInfo" type="{http://soap.convergenceondemand.net/TMP/}validationInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validatePaymentResponseV2", propOrder = {
    "validationInfo"
})
public class ValidatePaymentResponseV2
    extends BaseResponse
{

    protected ValidationInfo validationInfo;

    /**
     * Gets the value of the validationInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ValidationInfo }
     *     
     */
    public ValidationInfo getValidationInfo() {
        return validationInfo;
    }

    /**
     * Sets the value of the validationInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidationInfo }
     *     
     */
    public void setValidationInfo(ValidationInfo value) {
        this.validationInfo = value;
    }

}