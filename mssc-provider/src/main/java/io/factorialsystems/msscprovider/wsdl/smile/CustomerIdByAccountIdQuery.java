//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.08.15 at 06:01:37 PM WAT 
//


package io.factorialsystems.msscprovider.wsdl.smile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerIdByAccountIdQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerIdByAccountIdQuery"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://xml.smilecoms.com/schema/TPGW}TPGWObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AccountId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="Hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerIdByAccountIdQuery", propOrder = {
    "accountId",
    "hash"
})
public class CustomerIdByAccountIdQuery
    extends TPGWObject
{

    @XmlElement(name = "AccountId")
    protected long accountId;
    @XmlElement(name = "Hash")
    protected String hash;

    /**
     * Gets the value of the accountId property.
     * 
     */
    public long getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     */
    public void setAccountId(long value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

}
