package io.github.ericdriggs.reportcard.aws.comparatorr;

import org.apache.commons.lang3.ObjectUtils;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Comparator;

import static io.github.ericdriggs.reportcard.util.CompareUtil.chainCompare;

public class S3ObjectComparator implements Comparator<S3Object> {

    public static S3ObjectComparator INSTANCE = new S3ObjectComparator();

    @Override
    public int compare(S3Object val1, S3Object val2) {
        return doCompare(val1, val2);
    }

    public static int doCompare(S3Object val1, S3Object val2) {
        if (val1 == null | val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                ObjectUtils.compare(val1.key(), val2.key()),
                ObjectUtils.compare(val1.lastModified(), val2.lastModified()),
                ObjectUtils.compare(val1.eTag(), val2.eTag()),
                ObjectUtils.compare(val1.hashCode(), val2.hashCode()),
                ObjectUtils.compare(val1.size(), val2.size())
        );
    }
}
