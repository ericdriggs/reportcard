package com.ericdriggs.reportcard.scanner;

/**
 * The arguments needed to submit a scanner request<br>
 * {@link #REPORTCARD_HOST}<br>
 * {@link #REPORTCARD_USER}<br>
 * {@link #REPORTCARD_PASS}<br>
 * {@link #SCM_ORG}<br>
 * {@link #SCM_REPO}<br>
 * {@link #SCM_BRANCH}<br>
 * {@link #BUILD_APP}<br>
 * {@link #BUILD_IDENTIFIER}<br>
 * {@link #BUILD_STAGE}<br>
 * {@link #TEST_REPORT_PATH}<br>
 * {@link #TEST_REPORT_REGEX}<br>
 *
 */
public enum ScannerArgs {
    /**
     * The base url of the reportcard host (Required)
     */
    REPORTCARD_HOST,
    /**
     * The user for the reportcard host (Required)
     */
    REPORTCARD_USER,
    /**
     * The pass for the reportcard host
     * (Required)
     */
    REPORTCARD_PASS,
    /**
     * The source control organization. Organizations have repositories.
     * (Required)
     */
    SCM_ORG,
    /**
     * The source control repository. Repositories belong to an org. Repositories have branches.
     * (Required)
     */
    SCM_REPO,
    /**
     * The source control branch. Branches belong to a branch. Reposities have branches and applications.
     * (Required)
     */
    SCM_BRANCH,
    /**
     * The build application. Build applications belong to a repository.
     * (Optional) Defaults to repository value if not provided.
     */
    BUILD_APP,
    /**
     * A unique identifier for a build. Multiple reports may be submitted with the same identifier.
     * (Optional) Defaults to a generated guid if not provided.
     */
    BUILD_IDENTIFIER,
    /**
     * The build stage, e.g. unit, integration, api
     * (Required)
     */
    BUILD_STAGE,
    /**
     * The path to a single folder containing all test reports. Will not search sub-folders.
     * (Required)
     */
    TEST_REPORT_PATH,
    /**
     * A regex to restrict which xml filenames to publish
     * (Optional) Defaults to *.xml
     */
    TEST_REPORT_REGEX;

}
