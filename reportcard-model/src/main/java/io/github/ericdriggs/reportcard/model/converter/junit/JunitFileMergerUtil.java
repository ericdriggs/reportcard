package io.github.ericdriggs.reportcard.model.converter.junit;

import io.github.ericdriggs.file.FileUtils;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.github.ericdriggs.file.FileUtils.regexForExtension;

public class JunitFileMergerUtil {

    public static Testsuites mergeJunitFiles(String absoluteFolderPath) {
        List<String> junitXmlStrings = FileUtils.fileContentsFromPathAndRegex(absoluteFolderPath, regexForExtension("xml"));
        Testsuites merged = new Testsuites();
        merged.setTestsuite(new ArrayList<>());
        for (String junitXmlString : junitXmlStrings) {
            try {
                Testsuites testsuites = JunitParserUtil.parseTestSuites(junitXmlString);
                merged.getTestsuite().addAll(testsuites.getTestsuite());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return merged;
    }

    public static void writeMergedJunitXml(String absoluteFolderPath, String newFilePath) {
        Testsuites testsuites = mergeJunitFiles(absoluteFolderPath);
        marshalJunit(testsuites, newFilePath);
    }

    @SneakyThrows(JAXBException.class)
    public static void marshalJunit(Testsuites testsuites, String pathName) {
        JAXBContext jaxbContext = JAXBContext.newInstance(Testsuites.class);
        Marshaller mar = jaxbContext.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(testsuites, new File(pathName));
    }
}
