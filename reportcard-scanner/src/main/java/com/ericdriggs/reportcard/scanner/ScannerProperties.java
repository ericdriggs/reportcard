package com.ericdriggs.reportcard.scanner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 *
 * Gets all request variable values listed in ScannerArgs
 * from the following precedence
 * 1. command-line argument
 * 2. environment variable
 * 3. property file
 * @see ScannerArgs
 */
@Component
public class ScannerProperties {

    public ScannerPostRequest getReportPostPayload(ApplicationArguments applicationArguments) {
        final Properties props = getProperties(applicationArguments);

        ScannerPostRequest payload = new ScannerPostRequest();

        {
            final String host = props.getProperty(ScannerArgs.REPORTCARD_HOST.name());
            if (hasValue(host)) {
                payload.setHost(host);
            }
        }
        {
            final String user = props.getProperty(ScannerArgs.REPORTCARD_USER.name());
            if (hasValue(user)) {
                payload.setUser(user);
            }
        }


        {
            final String pass = props.getProperty(ScannerArgs.REPORTCARD_PASS.name());
            if (hasValue(pass)) {
                payload.setPass(pass);
            }
        }

        {
            final String org = props.getProperty(ScannerArgs.SCM_ORG.name());
            if (hasValue(org)) {
                payload.setOrg(org);
            }
        }
        {
            final String repo = props.getProperty(ScannerArgs.SCM_REPO.name());
            if (hasValue(repo)) {
                payload.setRepo(repo);
            }
        }
        {
            final String branch = props.getProperty(ScannerArgs.GIT_BRANCH.name());
            if (hasValue(branch)) {
                payload.setBranch(branch);
            }
        }
        {
            final String app = props.getProperty(ScannerArgs.BUILD_APP.name());
            if (hasValue(app)) {
                payload.setApp(app);
            }
        }
        {
            final String buildIdentifier = props.getProperty(ScannerArgs.BUILD_IDENTIFIER.name());
            if (hasValue(buildIdentifier)) {
                payload.setBuildIdentifier(buildIdentifier);
            }
        }
        {
            final String stage = props.getProperty(ScannerArgs.BUILD_STAGE.name());
            if (hasValue(stage)) {
                payload.setStage(stage);
            }
        }

        {
            final String testReportPath = props.getProperty(ScannerArgs.TEST_REPORT_PATH.name());
            if (hasValue(testReportPath)) {
                payload.setTestReportPath(testReportPath);
            }
        }

        {
            final String testReportRegex = props.getProperty(ScannerArgs.TEST_REPORT_REGEX.name());
            if (hasValue(testReportRegex)) {
                payload.setTestReportRegex(testReportRegex);
            }
        }
        payload.prepare();

        return payload;
    }

    public static Properties getProperties(ApplicationArguments applicationArguments) {
        final Properties fileProperties = getFileProperties(null);
        final Properties envProperties = getEnvProperties(fileProperties);
        return getArgProperties(applicationArguments, envProperties);
    }

    public static Properties getArgProperties(ApplicationArguments args, Properties defaultProperties) {
        Properties properties = new Properties(defaultProperties);
        for (ScannerArgs argument : ScannerArgs.values()) {
            if (args.getOptionNames().contains(argument.name())) {
                String value = getLastValue(args.getOptionValues(argument.name()));
                if (!StringUtils.isEmpty(value) && !StringUtils.isBlank(value)) {
                    properties.put(argument.name(), value);
                }
            }
        }
        return properties;
    }

    public static Properties getEnvProperties(Properties defaultProperties) {
        Properties properties = new Properties(defaultProperties);
        for (ScannerArgs argument : ScannerArgs.values()) {
            String value = System.getenv(argument.name());
            if (hasValue(value)) {
                properties.put(argument.name(), value);
            }
        }
        return properties;
    }

    public static Properties getFileProperties(Properties defaultProperties) {
        Properties properties = new Properties(defaultProperties);
        File file = new File("reportcard-scanner.properties");

        if (file.exists()) {
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    public static String getLastValue(List<String> values) {
        String value = null;
        for (String v : values) {
            if (hasValue(v)) {
                value = v.trim();
            }
        }
        return value;
    }

    public static boolean hasValue(String value) {
        return !StringUtils.isEmpty(value) && !StringUtils.isBlank(value);
    }
}