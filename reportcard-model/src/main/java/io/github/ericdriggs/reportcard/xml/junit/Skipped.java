package io.github.ericdriggs.reportcard.xml.junit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skipped", propOrder = {"type"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForSkipped")
@Data
public class Skipped {

    @XmlAttribute(name = "type", required = true)
    protected String type;

}
