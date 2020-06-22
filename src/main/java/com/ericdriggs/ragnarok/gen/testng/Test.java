//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.21 at 11:26:30 PM PDT 
//


package com.ericdriggs.ragnarok.gen.testng;

import lombok.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
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
 *         &lt;element ref="{}method-selectors" minOccurs="0"/&gt;
 *         &lt;element ref="{}parameter" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}groups" minOccurs="0"/&gt;
 *         &lt;element ref="{}packages" minOccurs="0"/&gt;
 *         &lt;element ref="{}classes" minOccurs="0"/&gt;
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
 *       &lt;attribute name="parallel"&gt;
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
 *       &lt;attribute name="thread-count" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="time-out" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="enabled"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="true"/&gt;
 *             &lt;enumeration value="false"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="skipfailedinvocationcounts" default="false"&gt;
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
 *       &lt;attribute name="group-by-instances" default="false"&gt;
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
    "methodSelectors",
    "parameter",
    "groups",
    "packages",
    "classes"
})
@XmlRootElement(name = "test")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForTest")
@Data
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
}
