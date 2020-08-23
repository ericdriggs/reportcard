package com.ericdriggs.reportcard;

import java.util.List;

import com.ericdriggs.reportcard.db.tables.pojos.*;
import org.jooq.DSLContext;
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

}