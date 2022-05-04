package com.github.ericdriggs.reportcard.xml.surefire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "failure",
        "rerunFailure",
        "flakyFailure",
        "skipped",
        "error",
        "rerunError",
        "flakyError",
        "systemOut",
        "systemErr"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForTestcase")
@Data
public class Testcase {

    @XmlElement(nillable = true)
    protected List<Failure> failure;
    protected List<RerunFailure> rerunFailure;
    protected List<FlakyFailure> flakyFailure;
    @XmlElementRef(name = "skipped", type = JAXBElement.class, required = false)
    protected JAXBElement<Skipped> skipped;
    @XmlElementRef(name = "error", type = JAXBElement.class, required = false)
    protected JAXBElement<Error> error;
    protected List<RerunError> rerunError;
    protected List<FlakyError> flakyError;
    @XmlElement(name = "system-out")
    protected String systemOut;
    @XmlElement(name = "system-err")
    protected String systemErr;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "classname")
    protected String classname;
    @XmlAttribute(name = "group")
    protected String group;
    @XmlAttribute(name = "time", required = true)
    protected String time;


}
