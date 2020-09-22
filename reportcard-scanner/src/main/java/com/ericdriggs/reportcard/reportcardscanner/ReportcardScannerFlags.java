package com.ericdriggs.reportcard.reportcardscanner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import java.util.Properties;

@Component
public class ReportcardScannerFlags {

    public ReportMetaData getReportMetaData(ApplicationArguments applicationArguments) {
        final Properties varMap = getProperties(applicationArguments);

        ReportMetaData reportMetaData = new ReportMetaData();

        {
            final String org = varMap.getProperty(ReportMetaDataVariable.GIT_ORG.name());
            if (hasValue(org)) {
                reportMetaData.setOrg(org);
            }
        }
        {
            final String repo = varMap.getProperty(ReportMetaDataVariable.GIT_REPO.name());
            if (hasValue(repo)) {
                reportMetaData.setRepo(repo);
            }
        }
        {
            final String branch = varMap.getProperty(ReportMetaDataVariable.GIT_BRANCH.name());
            if (hasValue(branch)) {
                reportMetaData.setBranch(branch);
            }
        }
        {
            final String app = varMap.getProperty(ReportMetaDataVariable.BUILD_APP.name());
            if (hasValue(app)) {
                reportMetaData.setApp(app);
            }
        }
        {
            final String buildIdentifier = varMap.getProperty(ReportMetaDataVariable.BUILD_IDENTIFIER.name());
            if (hasValue(buildIdentifier)) {
                reportMetaData.setBuildIdentifier(buildIdentifier);
            }
        }
        {
            final String stage = varMap.getProperty(ReportMetaDataVariable.BUILD_STAGE.name());
            if (hasValue(stage)) {
                reportMetaData.setStage(stage);
            }
        }
        reportMetaData.validateAndSetDefaults();

        return reportMetaData;
    }

    public static Properties getProperties(ApplicationArguments applicationArguments) {
        final Properties fileProperties = getFileProperties(null);
        final Properties envProperties = getEnvProperties(fileProperties);
        return getArgProperties(applicationArguments, envProperties);
    }

    public static Properties getArgProperties(ApplicationArguments args, Properties defaultProperties) {
        Properties properties = new Properties(defaultProperties);
        for (ReportMetaDataVariable argument : ReportMetaDataVariable.values()) {
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
        for (ReportMetaDataVariable argument : ReportMetaDataVariable.values()) {
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