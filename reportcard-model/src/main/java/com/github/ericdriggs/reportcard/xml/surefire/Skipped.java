package com.github.ericdriggs.reportcard.xml.surefire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "value"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForSkipped")
@Data
public class Skipped {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "message")
    protected String message;
}
