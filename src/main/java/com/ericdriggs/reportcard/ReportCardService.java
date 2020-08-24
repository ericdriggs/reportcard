package com.ericdriggs.reportcard;

import java.util.List;

import com.ericdriggs.reportcard.db.tables.pojos.*;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static com.ericdriggs.reportcard.db.Tables.*;

@Service
@SuppressWarnings("unused")
/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */
public class ReportCardService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    DSLContext dsl;

    public List<Org> getOrgs() {
        return dsl.select().from(ORG)
                .fetch()
                .into(Org.class);
    }

    public Org getOrg(String org) {
        Org ret = dsl.select().from(ORG)
                .where(ORG.ORG_NAME.eq(org))
                .fetchOne()
                .into(Org.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org);
        }
        return ret;

    }

    public List<Repo> getRepos(String org) {
        return dsl.
                select(REPO.fields())
                .from(REPO.join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .fetch()
                .into(Repo.class);
    }

    public Repo getRepo(String org, String repo) {
        Repo ret = dsl.
                select(REPO.fields()).from(REPO).join(ORG)
                .on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetchOne()
                .into(Repo.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo);
        }
        return ret;
    }

    public List<App> getApps(String org, String repo) {
        return dsl.
                select(APP.fields())
                .from(APP.join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(App.class);
    }

    public App getApp(String org, String repo, String app) {
        App ret = dsl.
                select(APP.fields())
                .from(APP.join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .fetchOne()
                .into(App.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo + ", app: " + app);
        }
        return ret;
    }

    public List<Branch> getBranches(String org, String repo) {
        return dsl.
                select(BRANCH.fields())
                .from(BRANCH.join(REPO)
                        .on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(Branch.class);
    }

    public Branch getBranch(String org, String repo, String branch) {
        Branch ret = dsl.
                select(BRANCH.fields())
                .from(BRANCH.join(REPO)
                        .on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(Branch.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo + ", branch: " + branch);
        }
        return ret;
    }

    public AppBranch getAppBranch(String org, String repo, String app, String branch) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");
        AppBranch ret = dsl.
                select(APP_BRANCH.fields())
                .from(APP_BRANCH
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO2)
                        .on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(AppBranch.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch);
        }
        return ret;
    }

    public List<Build> getBuilds(String org, String repo, String app, String branch) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        List<Build> ret = dsl.
                select(BUILD.fields())
                .from(BUILD
                        .join(APP_BRANCH).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetch()
                .into(Build.class);
        return ret;
    }


    public Build getBuild(String org, String repo, String app, String branch, Integer appBranchBuildOrdinal) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        Build ret = dsl.
                select(BUILD.fields())
                .from(BUILD
                        .join(APP_BRANCH).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(BUILD.APP_BRANCH_BUILD_ORDINAL.eq(appBranchBuildOrdinal))
                .fetchOne()
                .into(Build.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", appBranchBuildOrdinal: " + appBranchBuildOrdinal);
        }
        return ret;
    }

    public List<Stage> getStages(String org, String repo, String app, String branch) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        List<Stage> ret = dsl.
                select(STAGE.fields())
                .from(STAGE
                        .join(APP_BRANCH).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetch()
                .into(Stage.class);
        return ret;
    }


    public Stage getStage(String org, String repo, String app, String branch, String stage) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        Stage ret = dsl.
                select(STAGE.fields())
                .from(STAGE
                        .join(APP_BRANCH).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(STAGE.STAGE_NAME.eq(stage))
                .fetchOne()
                .into(Stage.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", stage: " + stage);
        }
        return ret;
    }

    public Stage getBuildStage(String org, String repo, String app, String branch, Integer buildOrdinal, String stage) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        Stage ret = dsl.
                select(BUILD_STAGE.fields())
                .from(BUILD_STAGE
                        .join(BUILD).on(BUILD_STAGE.BUILD_FK.eq(BUILD.BUILD_ID))
                        .join(STAGE).on(BUILD_STAGE.STAGE_FK.eq(STAGE.STAGE_ID))
                        .join(APP_BRANCH).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(BUILD.APP_BRANCH_BUILD_ORDINAL.eq(buildOrdinal))
                .and(STAGE.STAGE_NAME.eq(stage))
                .fetchOne()
                .into(Stage.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", stage: " + stage);
        }
        return ret;
    }

    public BuildStagePath getBuildStagePath(String org, String repo, String app, String branch, Integer buildOrdinal, String stage) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        /**
         * USE REPORTCARD;
         * SELECT *
         * FROM ORG
         * LEFT JOIN REPO ON ORG.ORG_ID = REPO.ORG_FK
         * LEFT JOIN APP on APP.REPO_FK = REPO.REPO_ID
         * LEFT JOIN BRANCH on BRANCH.REPO_FK = REPO.REPO_ID
         * LEFT JOIN APP_BRANCH on (APP_BRANCH.APP_FK = APP.APP_ID) and (APP_BRANCH.BRANCH_FK = BRANCH.BRANCH_ID)
         * LEFT JOIN BUILD ON BUILD.APP_BRANCH_FK = APP_BRANCH.APP_BRANCH_ID
         * LEFT JOIN STAGE ON STAGE.APP_BRANCH_FK = APP_BRANCH.APP_BRANCH_ID
         * LEFT JOIN BUILD_STAGE on (BUILD_STAGE.BUILD_FK = BUILD.BUILD_ID) and (BUILD_STAGE.STAGE_FK = STAGE.STAGE_ID)
         * WHERE ORG_NAME = 'default'
         * AND REPO_NAME = 'default'
         * AND APP_NAME = 'app1'
         * AND BRANCH_NAME = 'master'
         * AND APP_BRANCH_BUILD_ORDINAL = 1
         * AND STAGE_NAME = 'unit'
         */
        Record record = dsl.
                select()
                .from(ORG
                        .leftJoin(REPO).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                        .leftJoin(APP).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .leftJoin(APP_BRANCH).on(APP_BRANCH.APP_FK.eq(APP.APP_ID).and(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID)))
                        .leftJoin(BUILD).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .leftJoin(STAGE).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .leftJoin(BUILD_STAGE).on(BUILD_STAGE.BUILD_FK.eq(BUILD.BUILD_ID).and(BUILD_STAGE.STAGE_FK.eq(STAGE.STAGE_ID)))
                        .where(ORG.ORG_NAME.eq(org)
                                .and(REPO.REPO_NAME.eq(repo))
                                .and(BRANCH.BRANCH_NAME.eq(branch))
                                .and(BUILD.APP_BRANCH_BUILD_ORDINAL.eq(buildOrdinal))
                                .and(STAGE.STAGE_NAME.eq(stage))
                        )
                )
                .fetchOne();


        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", buildOrdinal: " + buildOrdinal +
                            ", stage: " + stage);
        }

        BuildStagePath buildStagePath = new BuildStagePath();
        {
            //TODO: may have to wrap intos with try / catch if throws instead of null
            Org _org = record.into(Org.class);
            Repo _repo = record.into(Repo.class);
            Branch _branch = record.into(Branch.class);
            App _app = record.into(App.class);
            Build _build = record.into(Build.class);
            Stage _stage = record.into(Stage.class);

            buildStagePath.setOrg(_org);
            buildStagePath.setRepo(_repo);
            buildStagePath.setApp(_app);
            buildStagePath.setBranch(_branch);
            buildStagePath.setBuild(_build);
            buildStagePath.setStage(_stage);
        }
        return buildStagePath;
    }

    @Data
    public static class BuildStagePath {
        private Org org;
        private Repo repo;
        private App app;
        private Branch branch;
        private Build build;
        private Stage stage;

    }


}