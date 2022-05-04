//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.21 at 11:26:30 PM PDT 
//


package io.github.ericdriggs.reportcard.xml.testng.suite;

import lombok.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}groups" minOccurs="0"/&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element ref="{}listeners"/&gt;
 *           &lt;element ref="{}packages"/&gt;
 *           &lt;element ref="{}test"/&gt;
 *           &lt;element ref="{}parameter"/&gt;
 *           &lt;element ref="{}method-selectors"/&gt;
 *           &lt;element ref="{}suite-files"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="junit" default="false"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="false"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="verbose" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="parallel" default="none"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="false"/&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="none"/&gt;
 *             &lt;enumeration value="methods"/&gt;
 *             &lt;enumeration value="tests"/&gt;
 *             &lt;enumeration value="classes"/&gt;
 *             &lt;enumeration value="instances"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="parent-module" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="guice-stage" default="DEVELOPMENT"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="DEVELOPMENT"/&gt;
 *             &lt;enumeration value="PRODUCTION"/&gt;
 *             &lt;enumeration value="TOOL"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="configfailurepolicy" default="skip"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="skip"/&gt;
 *             &lt;enumeration value="continue"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="thread-count" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="5" /&gt;
 *       &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="time-out" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="skipfailedinvocationcounts" default="false"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="false"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="data-provider-thread-count" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="10" /&gt;
 *       &lt;attribute name="object-factory" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="group-by-instances" default="false"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="false"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="preserve-order" default="true"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="false"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="allow-return-values" default="false"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="false"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForSuite")
@Data
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

}
