
package com.ericdriggs.ragnarok.gen.testng;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{}include" minOccurs="0"/>
 *         &lt;element ref="{}exclude" minOccurs="0"/>
 *         &lt;element ref="{}parameter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "includeAndExcludeAndParameter"
})
@XmlRootElement(name = "methods")
public class Methods {

    @XmlElements({
        @XmlElement(name = "include", type = Include.class),
        @XmlElement(name = "exclude", type = Exclude.class),
        @XmlElement(name = "parameter", type = Parameter.class)
    })
    protected List<Any> includeAndExcludeAndParameter;

    /**
     * Gets the value of the includeAndExcludeAndParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeAndExcludeAndParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeAndExcludeAndParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Include }
     * {@link Exclude }
     * {@link Parameter }
     * 
     * 
     */
    public List<Any> getIncludeAndExcludeAndParameter() {
        if (includeAndExcludeAndParameter == null) {
            includeAndExcludeAndParameter = new ArrayList<Any>();
        }
        return this.includeAndExcludeAndParameter;
    }

}
