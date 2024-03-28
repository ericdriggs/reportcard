package io.github.ericdriggs.reportcard.model.converter;

import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public enum JunitSurefireXmlParseUtil {

    ;//static methods only

    public static TestResultModel parseTestXml(List<String> testXmlContents) {
        List<TestSuiteModel> testSuiteModels = new ArrayList<>();
        for (String testXmlContent : testXmlContents) {
            if (isJunit(testXmlContent)) {
                testSuiteModels.addAll(JunitConvertersUtil.fromXmlContents(testXmlContent));
            } else {
                testSuiteModels.add(SurefireConvertersUtil.fromTestXmlContent(testXmlContent));
            }
        }
        return new TestResultModel(testSuiteModels);
    }

    private final static DocumentBuilder builder = getDocumentBuilder();

    @SneakyThrows(ParserConfigurationException.class)
    private static DocumentBuilder getDocumentBuilder() {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    @SneakyThrows({IOException.class, SAXException.class})
    public static boolean isJunit(String testXmlContents) {
        InputStream inputStream = IOUtils.toInputStream(testXmlContents, StandardCharsets.UTF_8);

        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if ("testsuites".equals(root.getTagName())) {
            return true;
        }
        if (!"testsuite".equals(root.getTagName())) {
            throw new IllegalArgumentException("not a junit or surefire xml");
        }

        NodeList nodeList = root.getChildNodes();
        Node current;
        for (int i = 0; i < nodeList.getLength(); i++) {
            current = nodeList.item(i);
            if (!"testsuite".equals(current.getNodeName())) {
                continue;
            }

            if (hasJunitTestSuiteAttribute(current)) {
                return true;
            }

            if (hasErrOrOut(current)) {
                return false;
            }

        }
        return false;

    }

    protected static boolean hasJunitTestSuiteAttribute(Node node) {
        final Set<String> nodeAttributes = getAttributes(node);

        //removeAll returns true if any matching attributes found, changing the new set
        return new HashSet<>(nodeAttributes).removeAll(junitAttributes);
    }

    protected static Set<String> getAttributes(Node node) {
        NamedNodeMap attributesMap = node.getAttributes();
        Set<String> attributeNames = new TreeSet<>();
        for (int i = 0; i < attributesMap.getLength(); i++) {
            Node attributeNode = attributesMap.item(i);
            attributeNames.add(attributeNode.getNodeName());
        }
        return attributeNames;
    }

    protected static boolean hasErrOrOut(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node current;
        for (int i = 0; i < nodeList.getLength(); i++) {
            current = nodeList.item(i);
            if ("system-err".equals(current.getNodeName())) {
                return true;
            }
            if ("system-out".equals(current.getNodeName())) {
                return true;
            }
        }
        return false;
    }

    final static Set<String> junitAttributes = Set.of("disabled", "hostname", "id", "package", "timestamp");
}

