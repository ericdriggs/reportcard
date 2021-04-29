package com.ericdriggs.reportcard.gen.db;

import com.ericdriggs.reportcard.model.HostApplicationPipeline;

public enum TestData {
    ;
    public static String org = "org1";
    public static String repo = "repo1";
    public static String branch = "master";
    public static String sha = "bdd15b6fae26738ca58f0b300fc43f5872b429bf";
    public static String host = "host1";
    public static String application = "application1";
    public static String pipieline = "pipeline1";
    public static HostApplicationPipeline hostApplicationPipeline = new HostApplicationPipeline(host, application, pipieline);
    public static String externalExecutionId = "externalId1";
    public static String stage = "api";
}