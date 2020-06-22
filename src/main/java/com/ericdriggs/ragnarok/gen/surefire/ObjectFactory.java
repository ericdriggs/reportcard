
package com.ericdriggs.ragnarok.gen.surefire;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ericdriggs.ragnarok.gen.surefire package. 
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

    private final static QName _TestsuiteTestcaseError_QNAME = new QName("", "error");
    private final static QName _TestsuiteTestcaseSkipped_QNAME = new QName("", "skipped");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ericdriggs.ragnarok.gen.surefire
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Testsuite }
     * 
     */
    public Testsuite createTestsuite() {
        return new Testsuite();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase }
     * 
     */
    public Testsuite.Testcase createTestsuiteTestcase() {
        return new Testsuite.Testcase();
    }

    /**
     * Create an instance of {@link Testsuite.Properties }
     * 
     */
    public Testsuite.Properties createTestsuiteProperties() {
        return new Testsuite.Properties();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Failure }
     * 
     */
    public Testsuite.Testcase.Failure createTestsuiteTestcaseFailure() {
        return new Testsuite.Testcase.Failure();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.RerunFailure }
     * 
     */
    public Testsuite.Testcase.RerunFailure createTestsuiteTestcaseRerunFailure() {
        return new Testsuite.Testcase.RerunFailure();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.FlakyFailure }
     * 
     */
    public Testsuite.Testcase.FlakyFailure createTestsuiteTestcaseFlakyFailure() {
        return new Testsuite.Testcase.FlakyFailure();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Skipped }
     * 
     */
    public Testsuite.Testcase.Skipped createTestsuiteTestcaseSkipped() {
        return new Testsuite.Testcase.Skipped();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Error }
     * 
     */
    public Testsuite.Testcase.Error createTestsuiteTestcaseError() {
        return new Testsuite.Testcase.Error();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.RerunError }
     * 
     */
    public Testsuite.Testcase.RerunError createTestsuiteTestcaseRerunError() {
        return new Testsuite.Testcase.RerunError();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.FlakyError }
     * 
     */
    public Testsuite.Testcase.FlakyError createTestsuiteTestcaseFlakyError() {
        return new Testsuite.Testcase.FlakyError();
    }

    /**
     * Create an instance of {@link Testsuite.Properties.Property }
     * 
     */
    public Testsuite.Properties.Property createTestsuitePropertiesProperty() {
        return new Testsuite.Properties.Property();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Testsuite.Testcase.Error }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "error", scope = Testsuite.Testcase.class)
    public JAXBElement<Testsuite.Testcase.Error> createTestsuiteTestcaseError(Testsuite.Testcase.Error value) {
        return new JAXBElement<Testsuite.Testcase.Error>(_TestsuiteTestcaseError_QNAME, Testsuite.Testcase.Error.class, Testsuite.Testcase.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Testsuite.Testcase.Skipped }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "skipped", scope = Testsuite.Testcase.class)
    public JAXBElement<Testsuite.Testcase.Skipped> createTestsuiteTestcaseSkipped(Testsuite.Testcase.Skipped value) {
        return new JAXBElement<Testsuite.Testcase.Skipped>(_TestsuiteTestcaseSkipped_QNAME, Testsuite.Testcase.Skipped.class, Testsuite.Testcase.class, value);
    }

}
