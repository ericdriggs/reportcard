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
@XmlType(name = "system-out", propOrder = {"type"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForSystemOut")
@Data
public class SystemOut {

    @XmlAttribute(name = "type", required = true)
    protected String type;

}