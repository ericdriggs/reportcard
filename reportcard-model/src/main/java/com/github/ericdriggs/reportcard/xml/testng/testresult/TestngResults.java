
package io.github.ericdriggs.reportcard.xml.testng.testresult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}reporter-output"/>
 *         &lt;element ref="{}suite" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="failed" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="ignored" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="passed" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="skipped" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="total" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "reporterOutput",
    "suite"
})
@XmlRootElement(name = "testng-results")
public class TestngResults {

    @XmlElement(name = "reporter-output", required = true)
    protected ReporterOutput reporterOutput;
    @XmlElement(required = true)
    protected List<Suite> suite;
    @XmlAttribute(name = "failed", required = true)
    protected BigInteger failed;
    @XmlAttribute(name = "ignored", required = true)
    protected BigInteger ignored;
    @XmlAttribute(name = "passed", required = true)
    protected BigInteger passed;
    @XmlAttribute(name = "skipped", required = true)
    protected BigInteger skipped;
    @XmlAttribute(name = "total", required = true)
    protected BigInteger total;

    /**
     * Gets the value of the reporterOutput property.
     * 
     * @return
     *     possible object is
     *     {@link ReporterOutput }
     *     
     */
    public ReporterOutput getReporterOutput() {
        return reporterOutput;
    }

    /**
     * Sets the value of the reporterOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReporterOutput }
     *     
     */
    public void setReporterOutput(ReporterOutput value) {
        this.reporterOutput = value;
    }

    /**
     * Gets the value of the suite property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the suite property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSuite().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Suite }
     * 
     * 
     */
    public List<Suite> getSuite() {
        if (suite == null) {
            suite = new ArrayList<>();
        }
        return this.suite;
    }

    /**
     * Gets the value of the failed property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFailed() {
        return failed;
    }

    /**
     * Sets the value of the failed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFailed(BigInteger value) {
        this.failed = value;
    }

    /**
     * Gets the value of the ignored property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIgnored() {
        return ignored;
    }

    /**
     * Sets the value of the ignored property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIgnored(BigInteger value) {
        this.ignored = value;
    }

    /**
     * Gets the value of the passed property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPassed() {
        return passed;
    }

    /**
     * Sets the value of the passed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPassed(BigInteger value) {
        this.passed = value;
    }

    /**
     * Gets the value of the skipped property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSkipped() {
        return skipped;
    }

    /**
     * Sets the value of the skipped property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSkipped(BigInteger value) {
        this.skipped = value;
    }

    /**
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotal(BigInteger value) {
        this.total = value;
    }

}
