//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.08.15 at 06:01:37 PM WAT 
//


package io.factorialsystems.msscprovider.wsdl.smile;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AuthenticateResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthenticateResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Done" type="{http://xml.smilecoms.com/schema/TPGW}stDone" minOccurs="0"/&gt;
 *         &lt;element name="SessionId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticateResult", propOrder = {
    "done",
    "sessionId"
})
public class AuthenticateResult {

    @XmlElement(name = "Done")
    @XmlSchemaType(name = "string")
    protected StDone done;
    @XmlElement(name = "SessionId", required = true)
    protected String sessionId;

    /**
     * Gets the value of the done property.
     * 
     * @return
     *     possible object is
     *     {@link StDone }
     *     
     */
    public StDone getDone() {
        return done;
    }

    /**
     * Sets the value of the done property.
     * 
     * @param value
     *     allowed object is
     *     {@link StDone }
     *     
     */
    public void setDone(StDone value) {
        this.done = value;
    }

    /**
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

}
