package io.github.ericdriggs.reportcard.xml.junit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "testcase", propOrder = {
        "skipped",
        "error",
        "failure",
        "systemOut",
        "systemErr",
        "assertions"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForTestcase")
@Data
public class Testcase {

    protected Skipped skipped;
    //potential bug: can be multiple error according to xsd -- have only seen single in the wild.
    protected Error error;
    //potential bug: can be multiple failure according to xsd -- have only seen single in the wild.
    protected Failure failure;

    @XmlElement(name = "system-out")
    protected String systemOut;

    @XmlElement(name = "system-err")
    protected String systemErr;

    @XmlElement(name = "assertions")
    protected String assertions;

    @XmlAttribute(name = "name", required = true)
    protected String name;


    @XmlAttribute(name = "time")
    protected BigDecimal time;

    @XmlAttribute(name = "classname")
    protected String classname;

    @XmlAttribute(name = "status")
    protected String status;

}
