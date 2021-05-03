package com.ericdriggs.reportcard.model;

import com.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Repo;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Sha;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

public class Comparators {

    public static final Comparator<Org> ORG_CASE_INSENSITIVE_ORDER
            = new Comparators.OrgCaseInsensitiveComparator();

    public static final Comparator<Repo> REPO_CASE_INSENSITIVE_ORDER
            = new Comparators.RepoCaseInsensitiveComparator();

    public static final Comparator<Branch> BRANCH_CASE_INSENSITIVE_ORDER
            = new Comparators.BranchCaseInsensitiveComparator();

    public static final Comparator<Sha> SHA
            = new Comparators.ShaComparator();

    private static class OrgCaseInsensitiveComparator
            implements Comparator<Org>, java.io.Serializable {
        private static final long serialVersionUID = 7807917410507365390L;

        public int compare(Org val1, Org val2) {
            return compareOrg(val1, val2);
        }
    }

    private static class RepoCaseInsensitiveComparator
            implements Comparator<Repo>, java.io.Serializable {
        private static final long serialVersionUID = 1499664932611968428L;

        public int compare(Repo val1, Repo val2) {
            return compareRepo(val1, val2);
        }

    }

    private static class BranchCaseInsensitiveComparator
            implements Comparator<Branch>, java.io.Serializable {
        private static final long serialVersionUID = 6449752623071242729L;

        public int compare(Branch val1, Branch val2) {
            return compareBranch(val1, val2);
        }
    }

    private static class ShaComparator
            implements Comparator<Sha>, java.io.Serializable {
        private static final long serialVersionUID = 4527771070609844606L;
        public int compare(Sha val1, Sha val2) {
            return compareSha(val1, val2);
        }
    }



    public static int compareOrg(Org val1, Org val2) {
        return  ObjectUtils.compare(val1.getOrgName().toLowerCase(), val2.getOrgName().toLowerCase());
    }

    public static int compareRepo(Repo val1, Repo val2) {
        return chainCompare(
                Integer.compare(val1.getOrgFk(), val2.getOrgFk()),
                ObjectUtils.compare(val1.getRepoName().toLowerCase(), val2.getRepoName().toLowerCase())
        );
    }

    public static int compareBranch(Branch val1, Branch val2) {
        return chainCompare(
                Integer.compare(val1.getRepoFk(), val2.getRepoFk()),
                ObjectUtils.compare(val1.getBranchName().toLowerCase(), val2.getBranchName().toLowerCase())
        );
    }

    public static int compareSha(Sha val1, Sha val2) {
        return chainCompare(
                Integer.compare(val1.getBranchFk(), val2.getBranchFk()),
                ObjectUtils.compare(val1.getSha(), val2.getSha())
        );
    }

    public static int chainCompare(int... compares) {
        for (int compare : compares) {
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    public static String toLower(String string) {
        if (string == null) {
            return null;
        }
        else {
            return string.toLowerCase();
        }
    }
}
