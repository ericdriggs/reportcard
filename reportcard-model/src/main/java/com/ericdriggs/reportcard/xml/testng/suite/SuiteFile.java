//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.21 at 11:26:30 PM PDT 
//


package com.ericdriggs.reportcard.xml.testng.suite;

import lombok.*;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}any"&gt;
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "suite-file")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForSuiteFile")
@Data
public class SuiteFile
    extends Any
{

    @XmlAttribute(name = "path", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String path;

}
