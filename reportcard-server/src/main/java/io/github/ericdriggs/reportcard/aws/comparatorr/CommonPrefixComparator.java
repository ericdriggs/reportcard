package io.github.ericdriggs.reportcard.aws.comparatorr;

import org.apache.commons.lang3.ObjectUtils;
import software.amazon.awssdk.services.s3.model.CommonPrefix;

import java.util.Comparator;

import static io.github.ericdriggs.reportcard.util.CompareUtil.chainCompare;

public class CommonPrefixComparator implements Comparator<CommonPrefix> {

    public static CommonPrefixComparator INSTANCE = new CommonPrefixComparator();

    @Override
    public int compare(CommonPrefix val1, CommonPrefix val2) {
        return doCompare(val1, val2);
    }

    public static int doCompare(CommonPrefix val1, CommonPrefix val2) {
        if (val1 == null | val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                ObjectUtils.compare(val1.prefix(), val2.prefix())
        );
    }
}
