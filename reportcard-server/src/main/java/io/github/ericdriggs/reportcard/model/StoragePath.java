package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.persist.StorageType;
import lombok.Data;
import lombok.SneakyThrows;
import software.amazon.awssdk.utils.Md5Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;

@Data
public class StoragePath {

    private final static int maxLength = 200;
    private final static int hashLength = 22;
    private final static int maxStringBytes = 20;

    private final String org;
    private final String repo;
    private final String branch;

    private final String date;
    private final String jobInfoHash;
    private final int runCount;

    private final String sha;
    private final String stage;

    private final static SimpleDateFormat dateYmd = new SimpleDateFormat("yyyy-MM-dd");

    public StoragePath(StagePath stagePath) {
        stagePath.throwIfIncomplete();
        this.org = stagePath.getOrg().getOrgName();
        this.repo = stagePath.getRepo().getRepoName();
        this.branch = stagePath.getBranch().getBranchName();
        this.date = dateYmd.format(Instant.now());
        this.jobInfoHash = getJobInfoHash(stagePath.getJob().getJobInfo());
        this.runCount = stagePath.getRun().getJobRunCount();
        this.sha = stagePath.getRun().getSha();
        this.stage = stagePath.getStage().getStageName();
    }

    public String getPrefix(StorageType storageType) {
        {
            final String fullPath = getPath(org, repo, branch, date, jobInfoHash, runCount, sha, stage, storageType);
            if (fullPath.getBytes(StandardCharsets.UTF_8).length <= maxLength) {
                return fullPath;
            }
        }

        //TODO:
        return getPath(
                truncateBytes(org, maxStringBytes),
                truncateBytes(repo, maxStringBytes),
                truncateBytes(branch, maxStringBytes),
                date, //10
                jobInfoHash, //22
                runCount, //6
                sha, //40
                truncateBytes(stage, maxStringBytes),
                storageType);

    }

    @SneakyThrows(UnsupportedEncodingException.class)
    protected static String truncateBytes(String string, int maxBytes) {

        //Nothing to do
        final byte[] bytes = string.getBytes("UTF-8");
        if (bytes.length <= maxBytes) {
            return string;
        }

        //Iteratively trim until below target length
        for (int i = maxBytes; i > 0; i--) {
            final String subString = string.substring(0, i);
            final byte[] subStringBytes = subString.getBytes("UTF-8");
            if (subStringBytes.length <= maxBytes) {
                return subString;
            }
        }
        throw new IllegalStateException("truncation coding error -- should be unreachable code");
    }

    protected static String getJobInfoHash(String jobInfo) {

        return Md5Utils.md5AsBase64(jobInfo.getBytes())
                .replace("/", "-") // base64 encoding includes '/' path character
                .substring(0, hashLength);
    }

    protected static String getPath(
            String org,
            String repo,
            String branch,
            String date,
            String jobInfoHash,
            int runCount,
            String sha,
            String stage,
            StorageType storageType) {

        return "/rc" +
                "/" + org +
                "/" + repo +
                "/" + branch +
                "/" + date +
                "/" + jobInfoHash +
                "/" + runCount +
                "/" + sha +
                "/" + stage +
                "/" + storageType.name().toLowerCase();
    }

}
