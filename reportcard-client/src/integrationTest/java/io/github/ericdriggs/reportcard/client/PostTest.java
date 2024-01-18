package io.github.ericdriggs.reportcard.client;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostTest {

    final static Random random = new Random();

    final static String reportCardHost = "http://localhost:8080";
    final static String reportCardUser = "user";
    final static String reportCardPass = "password";

    final static String company = "company1";
    final static String org = "org1";
    final static String repo = "repo1";
    final static String branch = "branch1";
    final static String sha = "83c1593bb6e785437bb55dd3de787ab6b09a0a37";

    final static Map<String,String> metadata = new HashMap<>();
    static {
        metadata.put("host","www.foo.com");
        metadata.put("application","app1");
        metadata.put("pipeline","pipeline1");
    }
    final static String stage = "apiTest";
    final static String testReportPath = "src/integrationTest/resources/format-samples/surefire-reports/";
    final static String testReportRegex = ".*[.]xml";

    @Test
    public void postClientTest() {
        PostWebClient postClient = new PostWebClient();
        PostRequest postRequest = getTestPostRequest();
        Mono<String> response = postClient.postTestReport(postRequest);
        response.subscribe(result -> assertNotNull(result));
        response.subscribe(result -> System.out.println(result));

    }

    private static PostRequest getTestPostRequest() {
        long randomLong = random.nextLong();
        final String runReference = Long.toString(randomLong);

        final Map<String, String> externalLinks = new HashMap<>();
        externalLinks.put("build", "www.foo.com/build/" + randomLong);
        externalLinks.put("foobar", "www.foo.com/bar/" + randomLong);


        ReportMetaData reportMetaData =
                new ReportMetaData()
                        .setCompany(company)
                        .setOrg(org)
                        .setRepo(repo)
                        .setBranch(branch)
                        .setSha(sha)
                        .setJobInfo(metadata)
                        .setRunReference(runReference)
                        .setExternalLinks(externalLinks)
                        .setStage(stage);

        return new PostRequest(reportMetaData, new ReportCardServerData(reportCardHost, reportCardUser, reportCardPass));
    }
}
