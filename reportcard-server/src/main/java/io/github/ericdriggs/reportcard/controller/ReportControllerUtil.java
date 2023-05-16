package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.AbstractReportCardService;
import io.github.ericdriggs.reportcard.UploadService;
import io.github.ericdriggs.reportcard.model.RunStagePath;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil;
import io.github.ericdriggs.reportcard.xml.XmlUtil;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import io.github.ericdriggs.reportcard.xml.surefire.SurefireParserUtil;
import io.github.ericdriggs.reportcard.xml.surefire.Testsuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ReportControllerUtil {

    @Autowired
    public ReportControllerUtil(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    private final UploadService uploadService;

    public TestResult getTestResult(Long testResultId) {
        return uploadService.getTestResult(testResultId);
    }

    public TestResult doPostXml(ReportMetaData reportMetatData, MultipartFile[] files) {
        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(fileToString(file)));

        return doPostXmlStrings(reportMetatData, xmlStrings);
    }

    public TestResult doPostXmlStrings(ReportMetaData reportMetatData, List<String> xmlStrings) {
        TestResult inserted = null;
        if (xmlStrings == null || xmlStrings.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing files");
        } else if (xmlStrings.size() > 1) {
            for (String xmlString : xmlStrings) {
                if (!"testsuite".equals(XmlUtil.getXmlRootElementName(xmlString))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Multiple files only supported with surefire format");
                }
            }
            inserted = doPostXmlSurefire(reportMetatData, xmlStrings);
        } else { //xmlString.size == 1
            String xmlString = xmlStrings.get(0);
            String rootElementName = XmlUtil.getXmlRootElementName(xmlString);
            if ("testsuite".equals(rootElementName)) {
                inserted = doPostXmlSurefire(reportMetatData, xmlStrings);
            } else if ("testsuites".equals(rootElementName)) {
                inserted = doPostXmlJunit(reportMetatData, xmlString);
            }
        }
        return inserted;
    }

    public TestResult doPostXmlJunit(ReportMetaData reportMetatData, MultipartFile file) {
        return doPostXmlJunit(reportMetatData, fileToString(file));
    }

    public TestResult doPostXmlJunit(ReportMetaData reportMetatData, String xmlString) {
        reportMetatData.validateAndSetDefaults();
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        TestResult testResult = JunitConvertersUtil.modelMapper.map(testsuites, TestResult.class);
        RunStagePath buildStagePath = uploadService.getOrInsertRunStagePath(reportMetatData);
        testResult.setStageFk(buildStagePath.getStage().getStageId());
        testResult.setExternalLinks(reportMetatData.getExternalLinksJson());
        return uploadService.insertTestResult(testResult);
    }

    public TestResult doPostXmlSurefire(ReportMetaData reportMetatData, MultipartFile[] files) {

        List<String> xmlStrings = new ArrayList<>();
        for (MultipartFile file : files) {
            xmlStrings.add(file.toString());
        }
        return doPostXmlSurefire(reportMetatData, xmlStrings);
    }

    public TestResult doPostXmlSurefire(ReportMetaData reportMetatData, List<String> xmlStrings) {
        reportMetatData.validateAndSetDefaults();
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuites(xmlStrings);
        TestResult testResult = SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
        RunStagePath buildStagePath = uploadService.getOrInsertRunStagePath(reportMetatData);
        testResult.setStageFk(buildStagePath.getStage().getStageId());
        return uploadService.insertTestResult(testResult);
    }

    public static String fileToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
