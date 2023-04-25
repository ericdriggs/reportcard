package io.github.ericdriggs.reportcard.gen.db;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public enum TestData {
    ;
    public final static String org = "org1";
    public final static String repo = "repo1";
    public final static String branch = "master";
    public final static String sha = "bdd15b6fae26738ca58f0b300fc43f5872b429bf";

    public final static Map<String, String> metadata = new HashMap<>();

    static {
        metadata.put("host", "foocorp.jenkins.com");
        metadata.put("application", "fooapp");
        metadata.put("pipeline", "foopipeline");
    }

    public final static String host = "host1";
    public final static String application = "application1";
    public final static String pipieline = "pipeline1";
    public final static String externalExecutionId = "externalId1";
    public final static String stage = "api";
}