package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.TestResultUploadService;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.xml.XmlUtil;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Component
public class ReportControllerUtil {

    @Autowired
    public ReportControllerUtil(TestResultUploadService uploadService) {
        this.uploadService = uploadService;
    }

    private final TestResultUploadService uploadService;

    public TestResult getTestResult(Long testResultId) {
        return uploadService.getTestResult(testResultId);
    }

    public Map<StagePath,TestResult> doPostXml(ReportMetaData reportMetatData, MultipartFile[] files) {
        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(fileToString(file)));

        return doPostXmlStrings(reportMetatData, xmlStrings);
    }

    public Map<StagePath,TestResult> doPostXmlStrings(ReportMetaData reportMetatData, List<String> xmlStrings) {
        Map<StagePath,TestResult> stagePathTestResultMap = null;
        if (xmlStrings == null || xmlStrings.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing files");
        } else if (xmlStrings.size() > 1) {
            for (String xmlString : xmlStrings) {
                if (!"testsuite".equals(XmlUtil.getXmlRootElementName(xmlString))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Multiple files only supported with surefire format");
                }
            }
            stagePathTestResultMap = doPostXmlMultiple(reportMetatData, xmlStrings);
        } else { //xmlString.size == 1
            String xmlString = xmlStrings.get(0);
            String rootElementName = XmlUtil.getXmlRootElementName(xmlString);
            if ("testsuite".equals(rootElementName)) {
                stagePathTestResultMap = doPostXmlMultiple(reportMetatData, xmlStrings);
            } else if ("testsuites".equals(rootElementName)) {
                stagePathTestResultMap = doPostXmlSingle(reportMetatData, xmlString);
            }
        }
        return stagePathTestResultMap;
    }

    protected Map<StagePath,TestResult> doPostXmlSingle(ReportMetaData reportMetatData, String xmlString) {
        reportMetatData.validateAndSetDefaults();
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        TestResult testResult = JunitConvertersUtil.modelMapper.map(testsuites, TestResult.class);
        StagePath stagePath = uploadService.getOrInsertStagePath(reportMetatData);
        testResult.setStageFk(stagePath.getStage().getStageId());
        testResult.setExternalLinks(reportMetatData.getExternalLinksJson());
        TestResult inserted = uploadService.insertTestResult(testResult);
        return Collections.singletonMap(stagePath, inserted);
    }

    public Map<StagePath,TestResult> doPostXmlMultiple(ReportMetaData reportMetatData, List<String> xmlStrings) {
        reportMetatData.validateAndSetDefaults();
        Testsuites testsuites = JunitParserUtil.parseTestSuiteList(xmlStrings);
        TestResult testResult = JunitConvertersUtil.doFromJunitToModelTestResult(testsuites);
        StagePath stagePath = uploadService.getOrInsertStagePath(reportMetatData);
        testResult.setStageFk(stagePath.getStage().getStageId());
        TestResult inserted = uploadService.insertTestResult(testResult);
        return Collections.singletonMap(stagePath, inserted);
    }

    public static String fileToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
