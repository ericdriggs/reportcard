
package com.ericdriggs.ragnarok.gen.testng;

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
 *         &lt;element ref="{}method-selectors" minOccurs="0"/>
 *         &lt;element ref="{}parameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}groups" minOccurs="0"/>
 *         &lt;element ref="{}packages" minOccurs="0"/>
 *         &lt;element ref="{}classes" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="junit" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="verbose" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="parallel">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="false"/>
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="none"/>
 *             &lt;enumeration value="methods"/>
 *             &lt;enumeration value="tests"/>
 *             &lt;enumeration value="classes"/>
 *             &lt;enumeration value="instances"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="thread-count" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="time-out" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="enabled">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="skipfailedinvocationcounts" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="preserve-order" default="true">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="group-by-instances" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="allow-return-values" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "methodSelectors",
    "parameter",
    "groups",
    "packages",
    "classes"
})
@XmlRootElement(name = "test")
public class Test {

    @XmlElement(name = "method-selectors")
    protected MethodSelectors methodSelectors;
    protected List<Parameter> parameter;
    protected Groups groups;
    protected Packages packages;
    protected Classes classes;
    @XmlAttribute(name = "name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;
    @XmlAttribute(name = "junit")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String junit;
    @XmlAttribute(name = "verbose")
    @XmlSchemaType(name = "anySimpleType")
    protected String verbose;
    @XmlAttribute(name = "parallel")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String parallel;
    @XmlAttribute(name = "thread-count")
    @XmlSchemaType(name = "anySimpleType")
    protected String threadCount;
    @XmlAttribute(name = "annotations")
    @XmlSchemaType(name = "anySimpleType")
    protected String annotations;
    @XmlAttribute(name = "time-out")
    @XmlSchemaType(name = "anySimpleType")
    protected String timeOut;
    @XmlAttribute(name = "enabled")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String enabled;
    @XmlAttribute(name = "skipfailedinvocationcounts")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String skipfailedinvocationcounts;
    @XmlAttribute(name = "preserve-order")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String preserveOrder;
    @XmlAttribute(name = "group-by-instances")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String groupByInstances;
    @XmlAttribute(name = "allow-return-values")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowReturnValues;

    /**
     * Gets the value of the methodSelectors property.
     * 
     * @return
     *     possible object is
     *     {@link MethodSelectors }
     *     
     */
    public MethodSelectors getMethodSelectors() {
        return methodSelectors;
    }

    /**
     * Sets the value of the methodSelectors property.
     * 
     * @param value
     *     allowed object is
     *     {@link MethodSelectors }
     *     
     */
    public void setMethodSelectors(MethodSelectors value) {
        this.methodSelectors = value;
    }

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Parameter }
     * 
     * 
     */
    public List<Parameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<Parameter>();
        }
        return this.parameter;
    }

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
     * Gets the value of the packages property.
     * 
     * @return
     *     possible object is
     *     {@link Packages }
     *     
     */
    public Packages getPackages() {
        return packages;
    }

    /**
     * Sets the value of the packages property.
     * 
     * @param value
     *     allowed object is
     *     {@link Packages }
     *     
     */
    public void setPackages(Packages value) {
        this.packages = value;
    }

    /**
     * Gets the value of the classes property.
     * 
     * @return
     *     possible object is
     *     {@link Classes }
     *     
     */
    public Classes getClasses() {
        return classes;
    }

    /**
     * Sets the value of the classes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Classes }
     *     
     */
    public void setClasses(Classes value) {
        this.classes = value;
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
     * Gets the value of the junit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJunit() {
        if (junit == null) {
            return "false";
        } else {
            return junit;
        }
    }

    /**
     * Sets the value of the junit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJunit(String value) {
        this.junit = value;
    }

    /**
     * Gets the value of the verbose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerbose() {
        return verbose;
    }

    /**
     * Sets the value of the verbose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerbose(String value) {
        this.verbose = value;
    }

    /**
     * Gets the value of the parallel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParallel() {
        return parallel;
    }

    /**
     * Sets the value of the parallel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParallel(String value) {
        this.parallel = value;
    }

    /**
     * Gets the value of the threadCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThreadCount() {
        return threadCount;
    }

    /**
     * Sets the value of the threadCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThreadCount(String value) {
        this.threadCount = value;
    }

    /**
     * Gets the value of the annotations property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnnotations() {
        return annotations;
    }

    /**
     * Sets the value of the annotations property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnnotations(String value) {
        this.annotations = value;
    }

    /**
     * Gets the value of the timeOut property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeOut() {
        return timeOut;
    }

    /**
     * Sets the value of the timeOut property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeOut(String value) {
        this.timeOut = value;
    }

    /**
     * Gets the value of the enabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnabled(String value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the skipfailedinvocationcounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkipfailedinvocationcounts() {
        if (skipfailedinvocationcounts == null) {
            return "false";
        } else {
            return skipfailedinvocationcounts;
        }
    }

    /**
     * Sets the value of the skipfailedinvocationcounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkipfailedinvocationcounts(String value) {
        this.skipfailedinvocationcounts = value;
    }

    /**
     * Gets the value of the preserveOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreserveOrder() {
        if (preserveOrder == null) {
            return "true";
        } else {
            return preserveOrder;
        }
    }

    /**
     * Sets the value of the preserveOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreserveOrder(String value) {
        this.preserveOrder = value;
    }

    /**
     * Gets the value of the groupByInstances property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupByInstances() {
        if (groupByInstances == null) {
            return "false";
        } else {
            return groupByInstances;
        }
    }

    /**
     * Sets the value of the groupByInstances property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupByInstances(String value) {
        this.groupByInstances = value;
    }

    /**
     * Gets the value of the allowReturnValues property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllowReturnValues() {
        if (allowReturnValues == null) {
            return "false";
        } else {
            return allowReturnValues;
        }
    }

    /**
     * Sets the value of the allowReturnValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowReturnValues(String value) {
        this.allowReturnValues = value;
    }

}
