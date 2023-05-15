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
@XmlType(name = "system-err", propOrder = {"type"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForSystemErr")
@Data
public class SystemErr {

    @XmlAttribute(name = "type", required = true)
    protected String type;

}