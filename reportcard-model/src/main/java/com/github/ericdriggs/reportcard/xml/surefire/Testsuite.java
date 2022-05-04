//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.21 at 11:24:30 PM PDT 
//


package io.github.ericdriggs.reportcard.xml.surefire;
import lombok.*;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "properties",
        "testcase"
})
@XmlRootElement(name = "testsuite")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForTestsuite")
@Data
public class Testsuite {

    protected List<Properties> properties;
    protected List<Testcase> testcase;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "time")
    protected BigDecimal time;
    @XmlAttribute(name = "tests", required = true)
    protected Integer tests;
    @XmlAttribute(name = "errors", required = true)
    protected Integer errors;
    @XmlAttribute(name = "skipped", required = true)
    protected Integer skipped;
    @XmlAttribute(name = "failures", required = true)
    protected Integer failures;
    @XmlAttribute(name = "group")
    protected String group;


}
