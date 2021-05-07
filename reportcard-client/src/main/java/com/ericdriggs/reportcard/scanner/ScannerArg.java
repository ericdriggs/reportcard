package com.ericdriggs.reportcard.scanner;

/**
 * Required Arguments<br>
 * {@link #REPORTCARD_HOST}<br>
 * {@link #REPORTCARD_USER}<br>
 * {@link #REPORTCARD_PASS}<br>
 * {@link #SCM_ORG}<br>
 * {@link #SCM_REPO}<br>
 * {@link #SCM_BRANCH}<br>
 * {@link #SCM_SHA}<br>
 * {@link #CONTEXT_HOST}<br>
 * {@link #TEST_REPORT_PATH}<br>
 * {@link #TEST_REPORT_REGEX}<br>
 *
 * Optional Arguments<br>
 * {@link #CONTEXT_APPLICATION}<br>
 * {@link #CONTEXT_PIPELINE}<br>
 *
 * {@link #EXTERNAL_LINKS}<br>
 */
public enum ScannerArg {

    /**
     * The base url of the reportcard host
     * (Required)
     */
    REPORTCARD_HOST,

    /**
     * The user for the reportcard host
     * (Required)
     */
    REPORTCARD_USER,

    /**
     * The pass for the reportcard host
     * (Required)
     */
    REPORTCARD_PASS,

    /**
     * A source control organization. Organizations have repositories.
     * (Required)
     */
    SCM_ORG,

    /**
     * A source control repository. Repositories belong to an org. Repositories have branches.
     * (Required)
     */
    SCM_REPO,

    /**
     * A source control branch. Branches belong to a repo. Branches have SHAs.
     * (Required)
     */
    SCM_BRANCH,

    /**
     * The source control sha. SHAs belong to a branch. Shas have contexts.
     * (Required)
     */
    SCM_SHA,

    /**
     * The host which the report was generated on.
     * A context includes host and optionally application/pipeline
     * (Required)
     */
    CONTEXT_HOST,

    /**
     * The application which the report was generated on.
     * A context includes host and optionally application/pipeline
     * (Optional)
     */
    CONTEXT_APPLICATION,

    /**
     * The host which the report was generated on.
     * A context includes host and optionally application/pipeline
     * (Optional)
     */
    CONTEXT_PIPELINE,

    /**
     * The external identifier for the execution (build). E.g. run number, generated uuid
     * Executions belong to a context. Executions have stages.
     * (Required)
     */
    EXECUTION_EXTERNAL_ID,
    /**
     * The stage, e.g. unit, integration, api.
     * Stages belong to an execution. A stage has a test result.
     * (Required)
     */
    STAGE,
    /**
     * The path to a single folder containing all test reports. Will not search sub-folders.
     * (Required)
     */
    TEST_REPORT_PATH,
    /**
     * A regex to restrict which xml filenames to publish
     * (Optional) Defaults to *.xml
     */
    TEST_REPORT_REGEX,
    /**
     * A map of links in the form: <code>description1|url1,description2|url2</code>
     * If description is missing, an ordinal value will be used in its place.
     * Duplicate descriptions will cause map entries to be overwritten.
     * URL supports all ScannerArgs (Except EXTERNAL_LINKS) as token replacements, e,g. :
     * <code>api html|https://myreportserver.com/<SCM_ORG>/<SCM_REPO>/<SCM_BRANCH>/<BUILD_IDENTIFIER></code>
     * (Optional) defaults to null
     */
    EXTERNAL_LINKS;

    public static String getToken(ScannerArg scannerArg) {
        return "<" + scannerArg.name() + ">";
    }

}
