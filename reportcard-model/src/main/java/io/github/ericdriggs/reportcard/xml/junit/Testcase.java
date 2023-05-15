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
        "system_out",
        "system_err"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForTestcase")
@Data
public class Testcase {

    protected Skipped skipped;
    protected Error error;
    protected Failure failure;

    @XmlElement(name = "system-out")
    protected SystemOut system_out;
    @XmlElement(name = "system-err")
    protected SystemErr system_err;

    @XmlAttribute(name = "name", required = true)
    protected String name;

    @XmlAttribute(name = "assertions")
    protected String assertions;

    @XmlAttribute(name = "time")
    protected BigDecimal time;

    @XmlAttribute(name = "classname")
    protected String classname;

    @XmlAttribute(name = "status")
    protected String status;

}
