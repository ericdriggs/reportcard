
package com.ericdriggs.ragnarok.gen.testng;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *         &lt;element ref="{}groups" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{}listeners"/>
 *           &lt;element ref="{}packages"/>
 *           &lt;element ref="{}test"/>
 *           &lt;element ref="{}parameter"/>
 *           &lt;element ref="{}method-selectors"/>
 *           &lt;element ref="{}suite-files"/>
 *         &lt;/choice>
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
 *       &lt;attribute name="parallel" default="none">
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
 *       &lt;attribute name="parent-module" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="guice-stage" default="DEVELOPMENT">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="DEVELOPMENT"/>
 *             &lt;enumeration value="PRODUCTION"/>
 *             &lt;enumeration value="TOOL"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="configfailurepolicy" default="skip">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="skip"/>
 *             &lt;enumeration value="continue"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="thread-count" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="5" />
 *       &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="time-out" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="skipfailedinvocationcounts" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="data-provider-thread-count" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="10" />
 *       &lt;attribute name="object-factory" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="group-by-instances" default="false">
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
    "groups",
    "listenersOrPackagesOrTest"
})
@XmlRootElement(name = "suite")
public class Suite {

    protected Groups groups;
    @XmlElements({
        @XmlElement(name = "listeners", type = Listeners.class),
        @XmlElement(name = "packages", type = Packages.class),
        @XmlElement(name = "test", type = Test.class),
        @XmlElement(name = "parameter", type = Parameter.class),
        @XmlElement(name = "method-selectors", type = MethodSelectors.class),
        @XmlElement(name = "suite-files", type = SuiteFiles.class)
    })
    protected List<Object> listenersOrPackagesOrTest;
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
    @XmlAttribute(name = "parent-module")
    @XmlSchemaType(name = "anySimpleType")
    protected String parentModule;
    @XmlAttribute(name = "guice-stage")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String guiceStage;
    @XmlAttribute(name = "configfailurepolicy")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String configfailurepolicy;
    @XmlAttribute(name = "thread-count")
    @XmlSchemaType(name = "anySimpleType")
    protected String threadCount;
    @XmlAttribute(name = "annotations")
    @XmlSchemaType(name = "anySimpleType")
    protected String annotations;
    @XmlAttribute(name = "time-out")
    @XmlSchemaType(name = "anySimpleType")
    protected String timeOut;
    @XmlAttribute(name = "skipfailedinvocationcounts")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String skipfailedinvocationcounts;
    @XmlAttribute(name = "data-provider-thread-count")
    @XmlSchemaType(name = "anySimpleType")
    protected String dataProviderThreadCount;
    @XmlAttribute(name = "object-factory")
    @XmlSchemaType(name = "anySimpleType")
    protected String objectFactory;
    @XmlAttribute(name = "group-by-instances")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String groupByInstances;
    @XmlAttribute(name = "preserve-order")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String preserveOrder;
    @XmlAttribute(name = "allow-return-values")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String allowReturnValues;

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
     * Gets the value of the listenersOrPackagesOrTest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listenersOrPackagesOrTest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListenersOrPackagesOrTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Listeners }
     * {@link Packages }
     * {@link Test }
     * {@link Parameter }
     * {@link MethodSelectors }
     * {@link SuiteFiles }
     * 
     * 
     */
    public List<Object> getListenersOrPackagesOrTest() {
        if (listenersOrPackagesOrTest == null) {
            listenersOrPackagesOrTest = new ArrayList<Object>();
        }
        return this.listenersOrPackagesOrTest;
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
        if (parallel == null) {
            return "none";
        } else {
            return parallel;
        }
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
     * Gets the value of the parentModule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentModule() {
        return parentModule;
    }

    /**
     * Sets the value of the parentModule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentModule(String value) {
        this.parentModule = value;
    }

    /**
     * Gets the value of the guiceStage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuiceStage() {
        if (guiceStage == null) {
            return "DEVELOPMENT";
        } else {
            return guiceStage;
        }
    }

    /**
     * Sets the value of the guiceStage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuiceStage(String value) {
        this.guiceStage = value;
    }

    /**
     * Gets the value of the configfailurepolicy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigfailurepolicy() {
        if (configfailurepolicy == null) {
            return "skip";
        } else {
            return configfailurepolicy;
        }
    }

    /**
     * Sets the value of the configfailurepolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigfailurepolicy(String value) {
        this.configfailurepolicy = value;
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
        if (threadCount == null) {
            return "5";
        } else {
            return threadCount;
        }
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
     * Gets the value of the dataProviderThreadCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataProviderThreadCount() {
        if (dataProviderThreadCount == null) {
            return "10";
        } else {
            return dataProviderThreadCount;
        }
    }

    /**
     * Sets the value of the dataProviderThreadCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataProviderThreadCount(String value) {
        this.dataProviderThreadCount = value;
    }

    /**
     * Gets the value of the objectFactory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectFactory() {
        return objectFactory;
    }

    /**
     * Sets the value of the objectFactory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectFactory(String value) {
        this.objectFactory = value;
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
