package com.ericdriggs.reportcard.xml.surefire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "property"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderForProperties")
@Data
public class Properties {
    protected List<Property> property;
}
