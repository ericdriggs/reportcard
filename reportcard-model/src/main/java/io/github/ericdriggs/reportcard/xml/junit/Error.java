package io.github.ericdriggs.reportcard.xml.junit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "error", propOrder = {"type", "message", "value"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForError")
@Data
public class Error implements HasValueMessageTypeJunit {

    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "message")
    protected String message;
    @XmlValue
    protected String value;
}
