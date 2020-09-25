
package com.ericdriggs.reportcard.xml.testng.testresult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}groups"/>
 *         &lt;element ref="{}test" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="duration-ms" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="finished-at" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="started-at" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "groups",
    "test"
})
@XmlRootElement(name = "suite")
public class Suite {

    @XmlElement(required = true)
    protected Groups groups;
    @XmlElement(required = true)
    protected List<Test> test;
    @XmlAttribute(name = "duration-ms", required = true)
    protected BigInteger durationMs;
    @XmlAttribute(name = "finished-at", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String finishedAt;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "started-at", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String startedAt;

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link Groups }
     *     
     */
    public Groups getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link Groups }
     *     
     */
    public void setGroups(Groups value) {
        this.groups = value;
    }

    /**
     * Gets the value of the test property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the test property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Test }
     * 
     * 
     */
    public List<Test> getTest() {
        if (test == null) {
            test = new ArrayList<Test>();
        }
        return this.test;
    }

    /**
     * Gets the value of the durationMs property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDurationMs() {
        return durationMs;
    }

    /**
     * Sets the value of the durationMs property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDurationMs(BigInteger value) {
        this.durationMs = value;
    }

    /**
     * Gets the value of the finishedAt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinishedAt() {
        return finishedAt;
    }

    /**
     * Sets the value of the finishedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinishedAt(String value) {
        this.finishedAt = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the startedAt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartedAt() {
        return startedAt;
    }

    /**
     * Sets the value of the startedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartedAt(String value) {
        this.startedAt = value;
    }

}
