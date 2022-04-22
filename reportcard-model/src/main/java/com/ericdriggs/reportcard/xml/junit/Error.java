package com.ericdriggs.reportcard.xml.junit;

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
@Builder(builderMethodName = "builderForError")
@Data
public class Error {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "message")
    protected String message;
    @XmlAttribute(name = "type", required = true)
    protected String type;
}
