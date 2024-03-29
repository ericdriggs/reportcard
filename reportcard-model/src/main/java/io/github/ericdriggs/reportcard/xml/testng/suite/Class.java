//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.21 at 11:26:30 PM PDT 
//


package io.github.ericdriggs.reportcard.xml.testng.suite;

import lombok.*;

import javax.xml.bind.annotation.*;
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
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{}methods"/&gt;
 *         &lt;element ref="{}parameter"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "methodsOrParameter"
})
@XmlRootElement(name = "class")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForClass")
@Data
public class Class {

    @XmlElements({
        @XmlElement(name = "methods", type = Methods.class),
        @XmlElement(name = "parameter", type = Parameter.class)
    })
    protected List<Object> methodsOrParameter;
    @XmlAttribute(name = "name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;

}
