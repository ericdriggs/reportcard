//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.21 at 11:18:04 PM PDT 
//

package io.github.ericdriggs.reportcard.xml.junit;

import lombok.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "testsuites", propOrder = {"testsuite"})
@XmlRootElement(name = "testsuites")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForTestsuites")
@Data
public class Testsuites {

    protected List<Testsuite> testsuite;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "time")
    protected BigDecimal time;

    @XmlAttribute(name = "tests")
    protected int tests;

    @XmlAttribute(name = "failures")
    protected int failures;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlAttribute(name = "disabled")
    protected String disabled;

    @XmlAttribute(name = "errors")
    protected int errors;
}
