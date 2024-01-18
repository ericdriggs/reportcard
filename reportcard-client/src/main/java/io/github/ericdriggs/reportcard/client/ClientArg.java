package io.github.ericdriggs.reportcard.client;
//TODO: use a boolean for required and use that logic when validating arguments

import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Required Arguments<br>
 * {@link ClientArg#REPORTCARD_HOST}<br>
 * {@link ClientArg#REPORTCARD_USER}<br>
 * {@link ClientArg#REPORTCARD_PASS}<br>
 * {@link ClientArg#SCM_COMPANY}<br>
 * {@link ClientArg#SCM_ORG}<br>
 * {@link ClientArg#SCM_REPO}<br>
 * {@link ClientArg#SCM_BRANCH}<br>
 * {@link ClientArg#SCM_SHA}<br>
 * {@link ClientArg#TEST_REPORT_PATH}<br>

 * <p>
 * Optional Arguments<br>
 * {@link ClientArg#METADATA}<br>
 * {@link ClientArg#EXTERNAL_LINKS}<br>
 * {@link ClientArg#TEST_REPORT_REGEX}<br>
 */
public enum ClientArg {

    /**
     * The base url of the reportcard host (Required)
     */
    REPORTCARD_HOST(true),

    /**
     * The user for the reportcard host
     * (Required)
     */
    REPORTCARD_USER(true),

    /**
     * The pass for the reportcard host
     * (Required)
     */
    REPORTCARD_PASS(true),

    /**
     * A source control company. Companies have orgs. (Required)
     */
    SCM_COMPANY(true),

    /**
     * A source control organization. Organizations have repositories.(Required)
     */
    SCM_ORG(true),

    /**
     * A source control repository. Repositories belong to an org. Repositories have branches.
     * (Required)
     */
    SCM_REPO(true),

    /**
     * A source control branch. Branches belong to a repo. Branches have SHAs.
     * (Required)
     */
    SCM_BRANCH(true),

    /**
     * The source control sha. SHAs belong to a branch. Shas have contexts. (Required)
     */
    SCM_SHA(true),

    /**
     * Single layer map of metadata about run, e.g. host, application, pipeline, committer
     * (Optional)
     */
    METADATA(false),


    /**
     * A string identifier for a run/execution/build in a given context.  E.g. run number, generated uuid
     * Runs belong to a context. runs have stages.
     * (Optional). Will default to generated UUID if not provided.
     */
    RUN_REFERENCE(false),
    /**
     * The stage, e.g. unit, integration, api.
     * Stages belong to a run. A stage has a test result.
     * (Required)
     */
    STAGE(true),
    /**
     * The path to a single folder containing all test reports. Will not search sub-folders.
     * (Required)
     */
    TEST_REPORT_PATH(true),
    /**
     * A regex to restrict which xml filenames to publish
     * (Optional) Defaults to *.xml
     */
    TEST_REPORT_REGEX(false),
    /**
     * A map of links in the form: <code>description1|url1,description2|url2</code>
     * If description is missing, an ordinal value will be used in its place.
     * Duplicate descriptions will cause map entries to be overwritten.
     * Only 1 comma per description. Data after second commas will be ignored.
     * URL supports all ScannerArgs (Except EXTERNAL_LINKS) as token replacements, e,g. :
     * <pre>api html|https://myreportserver.com/{SCM_ORG}/{SCM_REPO}/{SCM_BRANCH}{BUILD_IDENTIFIER}</pre>
     * (Optional) defaults to null
     */
    EXTERNAL_LINKS(false);

    private boolean isRequired;

    ClientArg(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public static String getToken(ClientArg scannerArg) {
        return "<" + scannerArg.name() + ">";
    }

    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Validates all required ClientArg have a value in the provided map.
     *
     * @param clientArgStringMap a map of ClientArg and their values
     */
    public static void validateRequiredArgsPresent(Map<ClientArg, String> clientArgStringMap) {

        //Prepare errors
        Map<String, String> validationErrors = new TreeMap<>();

        for (ClientArg clientArg : ClientArg.values()) {
            if (clientArg.isRequired) {
                if (ObjectUtils.isEmpty(clientArgStringMap.get(clientArg))) {
                    validationErrors.put(clientArg.name(), "missing required field");
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new BadRequestException(validationErrors);
        }
    }

}
