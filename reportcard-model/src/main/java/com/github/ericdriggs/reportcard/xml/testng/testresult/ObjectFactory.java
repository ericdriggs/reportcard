
package com.github.ericdriggs.reportcard.xml.testng.testresult;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.github.ericdriggs.reportcard.xml.testng.testresult package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FullStacktrace_QNAME = new QName("", "full-stacktrace");
    private final static QName _Line_QNAME = new QName("", "line");
    private final static QName _Message_QNAME = new QName("", "message");
    private final static QName _Value_QNAME = new QName("", "value");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.github.ericdriggs.reportcard.xml.testng.testresult
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link TestngResults }
     * 
     */
    public TestngResults createTestngResults() {
        return new TestngResults();
    }

    /**
     * Create an instance of {@link ReporterOutput }
     * 
     */
    public ReporterOutput createReporterOutput() {
        return new ReporterOutput();
    }

    /**
     * Create an instance of {@link Suite }
     * 
     */
    public Suite createSuite() {
        return new Suite();
    }

    /**
     * Create an instance of {@link Groups }
     * 
     */
    public Groups createGroups() {
        return new Groups();
    }

    /**
     * Create an instance of {@link Group }
     * 
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link Method }
     * 
     */
    public Method createMethod() {
        return new Method();
    }

    /**
     * Create an instance of {@link Test }
     * 
     */
    public Test createTest() {
        return new Test();
    }

    /**
     * Create an instance of {@link Class }
     * 
     */
    public Class createClass() {
        return new Class();
    }

    /**
     * Create an instance of {@link TestMethod }
     * 
     */
    public TestMethod createTestMethod() {
        return new TestMethod();
    }

    /**
     * Create an instance of {@link Params }
     * 
     */
    public Params createParams() {
        return new Params();
    }

    /**
     * Create an instance of {@link Param }
     * 
     */
    public Param createParam() {
        return new Param();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "full-stacktrace")
    public JAXBElement<String> createFullStacktrace(String value) {
        return new JAXBElement<>(_FullStacktrace_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "line")
    public JAXBElement<String> createLine(String value) {
        return new JAXBElement<>(_Line_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "message")
    public JAXBElement<String> createMessage(String value) {
        return new JAXBElement<>(_Message_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "value")
    public JAXBElement<String> createValue(String value) {
        return new JAXBElement<>(_Value_QNAME, String.class, null, value);
    }

}
