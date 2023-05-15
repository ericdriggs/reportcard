package io.github.ericdriggs.reportcard.xml.junit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "failure", propOrder = {"type", "message"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForFailure")
@Data
public class Failure {

    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "message")
    protected String message;

}
