package com.ericdriggs.reportcard;

import java.util.List;

import com.ericdriggs.reportcard.db.Tables;


import com.ericdriggs.reportcard.db.tables.AppBranch;
import com.ericdriggs.reportcard.db.tables.pojos.App;
import com.ericdriggs.reportcard.db.tables.pojos.Branch;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import com.ericdriggs.reportcard.db.tables.pojos.Repo;
import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ericdriggs.reportcard.db.Tables.*;
//import static com.ericdriggs.reportcard.db.tables.*;
//import static com.ericdriggs.reportcard.db.tables..*;

@Service
@SuppressWarnings("unused")
public class ReportcardService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    DSLContext dsl;

    public List<Org> getOrgs() {
        return dsl.select().from(ORG)
                .fetch()
                .into(Org.class);
    }

    /**
     *
     * @param org the org to find
     * @return matching org org or <code>NULL</code>
     */
    public Org getOrg(String org) {
        return dsl.select().from(ORG)
                .where(ORG.ORG_NAME.eq(org))
                .fetchOne()
                .into(Org.class);
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

    /**
     *
     * @param repo the repo to find
     * @return matching repo or <code>NULL</code>
     */
    public Repo getRepo(String org, String repo) {
        return dsl.
                select(REPO.fields()).from(REPO).join(ORG)
                .on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetchOne()
                .into(Repo.class);
    }

    public List<Repo> getApps(String repo) {
        return dsl.
                select().from
                (APP.join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID)))
                .where(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(Repo.class);
    }

    /**
     *
     * @param app
     * @return matching repo or <code>NULL</code>
     */
    public App getApp(String app) {
        return dsl.
                select().from(APP)
                .where(APP.APP_NAME.eq(app))
                .fetchOne()
                .into(App.class);
    }

    public List<Repo> getBranches(String repo) {
        return dsl.
                select().from
                (BRANCH.join(REPO)
                        .on(BRANCH.REPO_FK.eq(REPO.REPO_ID)))
                .where(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(Repo.class);
    }

    /**
     *
     * @param branch the branch to find
     * @return the matching branch or <code>NULL</code>
     */
    public Branch getBranch(String branch) {
        return dsl.
                select().from(BRANCH)
                .where(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(Branch.class);
    }

    public AppBranch getAppBranch(String app, String branch) {
        return dsl.
                select().from(APP_BRANCH)
                .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                .join(BRANCH).on(APP_BRANCH.APP_FK.eq(BRANCH.BRANCH_ID))
                .where(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(AppBranch.class);
    }



}