package com.ericdriggs.reportcard.model;

//import com.ericdriggs.reportcard.gen.db.tables.Context;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Context;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Repo;
import com.ericdriggs.reportcard.gen.db.tables.pojos.Sha;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;
import java.util.Locale;

public class Comparators {

    public static final Comparator<Org> ORG_CASE_INSENSITIVE_ORDER
            = new Comparators.OrgCaseInsensitiveComparator();

    public static final Comparator<Repo> REPO_CASE_INSENSITIVE_ORDER
            = new Comparators.RepoCaseInsensitiveComparator();

    public static final Comparator<Branch> BRANCH_CASE_INSENSITIVE_ORDER
            = new Comparators.BranchCaseInsensitiveComparator();

    public static final Comparator<Sha> SHA
            = new Comparators.ShaComparator();

    public static final Comparator<Context> CONTEXT_CASE_INSENSITIVE_ORDER
            = new Comparators.ContextCaseInsensitiveComparator();

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

    private static class ContextCaseInsensitiveComparator
            implements Comparator<Context>, java.io.Serializable {
        private static final long serialVersionUID = 9214266396218473015L;

        public int compare(Context val1, Context val2) {
            return compareContext(val1, val2);
        }
    }

    public static int compareOrg(Org val1, Org val2) {
        return  compareLowerNullSafe(val1.getOrgName(), val2.getOrgName());
    }

    public static int compareRepo(Repo val1, Repo val2) {
        return chainCompare(
                Integer.compare(val1.getOrgFk(), val2.getOrgFk()),
                compareLowerNullSafe(val1.getRepoName(), val2.getRepoName())
        );
    }

    public static int compareBranch(Branch val1, Branch val2) {
        return chainCompare(
                Integer.compare(val1.getRepoFk(), val2.getRepoFk()),
                compareLowerNullSafe(val1.getBranchName(), val2.getBranchName())
        );
    }

    public static int compareSha(Sha val1, Sha val2) {
        return chainCompare(
                Integer.compare(val1.getBranchFk(), val2.getBranchFk()),
                ObjectUtils.compare(val1.getSha(), val2.getSha())
        );
    }

    public static int compareContext(Context val1, Context val2) {
        return chainCompare(
                Long.compare(val1.getShaFk(), val2.getShaFk()),
                compareLowerNullSafe(val1.getHost(), val2.getHost()),
                compareLowerNullSafe(val1.getApplication(), val2.getApplication()),
                compareLowerNullSafe(val1.getPipeline(), val2.getPipeline())
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

    public static int compareLowerNullSafe( String s1, String s2){
        if  (s1 == null || s2 == null){
            return ObjectUtils.compare(s1, s2);
        }
        return s1.toLowerCase().compareTo(s2.toLowerCase());

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
