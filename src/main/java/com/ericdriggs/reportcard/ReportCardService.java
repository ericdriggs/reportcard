package com.ericdriggs.reportcard;

import java.util.List;

import com.ericdriggs.reportcard.db.tables.daos.*;
import com.ericdriggs.reportcard.db.tables.pojos.*;
import com.ericdriggs.reportcard.db.tables.records.*;
import com.ericdriggs.reportcard.model.BuildStagePath;
import com.ericdriggs.reportcard.model.BuildStagePathRequest;
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

    //TODO: use constructor wiring
    
    OrgDao orgDao = new OrgDao();
    RepoDao repoDao = new RepoDao();
    AppDao appDao = new AppDao();
    BranchDao branchDao = new BranchDao();
    AppBranchDao appBranchDao = new AppBranchDao();
    BuildDao buildDao = new BuildDao();
    StageDao stageDao = new StageDao();
    BuildStageDao buildStageDao = new BuildStageDao();
    
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

    public BuildStagePath getBuildStagePath(BuildStagePathRequest request) {

        //String org, String repo, String app, String branch, Integer buildOrdinal, String stage

        Record record = dsl.
                select()
                .from(ORG
                        .leftJoin(REPO).on(ORG.ORG_ID.eq(REPO.ORG_FK)).and(REPO.REPO_NAME.eq(request.getRepoName()))
                        .leftJoin(APP).on(APP.REPO_FK.eq(REPO.REPO_ID)).and(APP.APP_NAME.eq(request.getAppName()))
                        .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)).and(BRANCH.BRANCH_NAME.eq(request.getBranchName()))
                        .leftJoin(APP_BRANCH).on(APP_BRANCH.APP_FK.eq(APP.APP_ID).and(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID)))
                        .leftJoin(BUILD).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID)).and(BUILD.APP_BRANCH_BUILD_ORDINAL.eq(request.getBuildOrdinal()))
                        .leftJoin(STAGE).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID)).and(STAGE.STAGE_NAME.eq(request.getStageName()))
                        .leftJoin(BUILD_STAGE).on(BUILD_STAGE.BUILD_FK.eq(BUILD.BUILD_ID).and(BUILD_STAGE.STAGE_FK.eq(STAGE.STAGE_ID)))
                ).where(ORG.ORG_NAME.eq(request.getOrgName()))
                .fetchOne();

        if (record == null) {
            return null;
        }

        BuildStagePath buildStagePath = new BuildStagePath();
        {
            Org _org = null;
            Repo _repo = null;
            Branch _branch = null;
            App _app = null;
            AppBranch _appBranch = null;
            Build _build = null;
            Stage _stage = null;
            BuildStage _buildStage = null;

            if (record.get(ORG.ORG_ID.getName()) != null) {
                _org = record.into(OrgRecord.class).into(Org.class);
            }
            if (record.get(REPO.REPO_ID.getName()) != null) {
                _repo = record.into(RepoRecord.class).into(Repo.class);
            }
            if (record.get(APP.APP_ID.getName()) != null) {
                _app = record.into(AppRecord.class).into(App.class);
            }
            if (record.get(BRANCH.BRANCH_ID.getName()) != null) {
                _branch = record.into(BranchRecord.class).into(Branch.class);
            }
            if (record.get(APP_BRANCH.APP_BRANCH_ID.getName()) != null) {
                _appBranch = record.into(AppBranchRecord.class).into(AppBranch.class);
            }
            if (record.get(BUILD.BUILD_ID.getName()) != null) {
                _build = record.into(BuildRecord.class).into(Build.class);
            }
            if (record.get(STAGE.STAGE_ID.getName()) != null) {
                _stage = record.into(StageRecord.class).into(Stage.class);
            }
            if (record.get(BUILD_STAGE.BUILD_STAGE_ID.getName()) != null) {
                _buildStage = record.into(BuildStageRecord.class).into(BuildStage.class);
            }

            buildStagePath.setOrg(_org);
            buildStagePath.setRepo(_repo);
            buildStagePath.setApp(_app);
            buildStagePath.setBranch(_branch);
            buildStagePath.setAppBranch(_appBranch);
            buildStagePath.setBuild(_build);
            buildStagePath.setStage(_stage);
            buildStagePath.setBuildStage(_buildStage);

        }
        return buildStagePath;
    }

    public BuildStagePath getOrInsertBuildStagePath(BuildStagePathRequest request) {
        BuildStagePath path = getBuildStagePath(request);

        if (path.getOrg() == null) {
            Org org = new Org().setOrgName(request.getOrgName());
            orgDao.insert(org);
            path.setOrg(org);
        }

        if (path.getRepo() == null) {
            Repo repo = new Repo()
                    .setRepoName(request.getOrgName())
                    .setOrgFk(path.getOrg().getOrgId());
            repoDao.insert(repo);
            path.setRepo(repo);
        }

        if (path.getApp() == null) {
            App app = new App()
                    .setAppName(request.getAppName())
                    .setRepoFk(path.getRepo().getRepoId());
            appDao.insert(app);
            path.setApp(app);
        }

        if (path.getBranch() == null) {
            Branch branch = new Branch()
                    .setBranchName(request.getBranchName())
                    .setRepoFk(path.getRepo().getRepoId());
            branchDao.insert(branch);
            path.setBranch(branch);
        }

        return path;
    }


}