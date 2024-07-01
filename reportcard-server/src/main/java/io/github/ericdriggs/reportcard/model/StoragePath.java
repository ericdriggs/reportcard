package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.util.truncate.TruncateUtils;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Value
public class StoragePath {

    private final static int maxLength = 200;
    private final static int hashLength = 22;
    private final static int maxStringBytes = 19;

    String company;
    String org;
    String repo;
    String branch;

    String date;
    long jobId;
    int runCount;

    String stage;
    String label;

    final static DateTimeFormatter dateYmd = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC"));

    public StoragePath(CompanyOrgRepoBranchJobRunStageDTO stagePath, Integer jobRunCount, String label) {
        this.company = sanitize(stagePath.getCompany());
        this.org = sanitize(stagePath.getOrg());
        this.repo = sanitize(stagePath.getRepo());
        this.branch = sanitize(stagePath.getBranch());
        this.date = dateYmd.format(Instant.now());
        this.jobId = stagePath.getJobId();
        this.runCount = jobRunCount == null ? 0 : jobRunCount;
        this.stage = sanitize(stagePath.getStageName());
        this.label = sanitize(label);
    }

    public StoragePath(StagePath stagePath, String label) {
        stagePath.throwIfIncomplete();
        this.company = sanitize(stagePath.getCompany().getCompanyName());
        this.org = sanitize(stagePath.getOrg().getOrgName());
        this.repo = sanitize(stagePath.getRepo().getRepoName());
        this.branch = sanitize(stagePath.getBranch().getBranchName());
        this.date = dateYmd.format(Instant.now());
        this.jobId = stagePath.getJob().getJobId();
        Integer jobRunCount = stagePath.getRun().getJobRunCount();
        this.runCount = jobRunCount == null ? 0 : jobRunCount;
        this.stage = sanitize(stagePath.getStage().getStageName());
        this.label = sanitize(label);
    }

    public String getPrefix() {
        {
            final String fullPath = getPath(company, org, repo, branch, date, jobId, runCount, stage, label);
            if (fullPath.getBytes(StandardCharsets.UTF_8).length <= maxLength) {
                return fullPath;
            }
        }

        return getPath(
                truncateBytes(company),
                truncateBytes(org),
                truncateBytes(repo),
                truncateBytes(branch),
                date, //10
                jobId, //6
                runCount, //6
                truncateBytes(stage),
                truncateBytes(label)
        );
    }

    static String truncateBytes(String str) {
        return TruncateUtils.truncateBytes(str, maxStringBytes);
    }

    static String getPath(
            String company,
            String org,
            String repo,
            String branch,
            String date,
            long jobId,
            int runCount,
            String stage,
            String label) {

        return "rc" +
                "/" + company +
                "/" + org +
                "/" + repo +
                "/" + branch +
                "/" + date +
                "/" + jobId +
                "/" + runCount +
                "/" + stage +
                "/" + label
                ;
    }

    //https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-keys.html
    static String sanitize(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return str;
        }

        str = restrictedPattern.matcher(str).replaceAll("_");
        str = str.replaceAll("__", "_").replaceAll("__", "_");
        return str;
    }

    final static String replacementCharacter = "_";
    final static Set<String> restrictedCharacters = new HashSet<>(
            List.of("&", "$", "@", "=", ";", "/", ":", "+", " ", "?",
                    "\\", "{", "^", "}", "%", "`", "]", ">", "[", "~", "<", "#", "|")
    );

    final static Pattern restrictedPattern = Pattern.compile("[" + String.join("", restrictedCharacters)+ "]");

}
