package io.github.ericdriggs.reportcard.aws.comparatorr;

import software.amazon.awssdk.services.s3.model.CommonPrefix;

import java.util.Comparator;

public class CommonPrefixDescendingComparator implements Comparator<CommonPrefix> {

    public static CommonPrefixDescendingComparator INSTANCE = new CommonPrefixDescendingComparator();

    @Override
    public int compare(CommonPrefix val1, CommonPrefix val2) {
        //swap order for descending
        return CommonPrefixComparator.doCompare(val2, val1);
    }
}
