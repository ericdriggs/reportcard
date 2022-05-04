package com.ericdriggs.reportcard.xml.surefire;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForProperty")
@Data
public class Property {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "value", required = true)
    protected String value;
}
