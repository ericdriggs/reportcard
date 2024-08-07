package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

@Builder
@Jacksonized
@Value
public class StorageTypeUriLabel implements Comparable<StorageTypeUriLabel> {

    StorageType storageType;
    URI uri;
    String label;

    @Override
    public int compareTo(StorageTypeUriLabel that) {
        return CompareUtil.chainCompare(
                CompareUtil.compareURI(this.uri, that.uri),
                StringUtils.compare(this.label, that.label),
                ObjectUtils.compare(this.storageType, that.storageType)
        );
    }
}
