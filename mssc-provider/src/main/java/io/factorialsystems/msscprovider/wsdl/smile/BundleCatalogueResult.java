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
 * <p>Java class for BundleCatalogueResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BundleCatalogueResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://xml.smilecoms.com/schema/TPGW}TPGWObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BundleList" type="{http://xml.smilecoms.com/schema/TPGW}BundleList"/&gt;
 *         &lt;element name="NumberOfBundles" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BundleCatalogueResult", propOrder = {
    "bundleList",
    "numberOfBundles"
})
public class BundleCatalogueResult
    extends TPGWObject
{

    @XmlElement(name = "BundleList", required = true)
    protected BundleList bundleList;
    @XmlElement(name = "NumberOfBundles")
    protected int numberOfBundles;

    /**
     * Gets the value of the bundleList property.
     * 
     * @return
     *     possible object is
     *     {@link BundleList }
     *     
     */
    public BundleList getBundleList() {
        return bundleList;
    }

    /**
     * Sets the value of the bundleList property.
     * 
     * @param value
     *     allowed object is
     *     {@link BundleList }
     *     
     */
    public void setBundleList(BundleList value) {
        this.bundleList = value;
    }

    /**
     * Gets the value of the numberOfBundles property.
     * 
     */
    public int getNumberOfBundles() {
        return numberOfBundles;
    }

    /**
     * Sets the value of the numberOfBundles property.
     * 
     */
    public void setNumberOfBundles(int value) {
        this.numberOfBundles = value;
    }

}
