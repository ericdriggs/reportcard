package com.ericdriggs.reportcard.xml.surefire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "stackTrace",
        "systemOut",
        "systemErr"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForRerunFailure")
@Data
public class RerunFailure {

    @XmlElement(required = true)
    protected String stackTrace;
    @XmlElement(name = "system-out")
    protected String systemOut;
    @XmlElement(name = "system-err")
    protected String systemErr;
    @XmlAttribute(name = "message")
    protected String message;
    @XmlAttribute(name = "type", required = true)
    protected String type;

}
