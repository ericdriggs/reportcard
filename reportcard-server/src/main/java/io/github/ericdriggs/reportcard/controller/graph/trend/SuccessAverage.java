package io.github.ericdriggs.reportcard.controller.graph.trend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class SuccessAverage {
    private int maxCount;
    private int totalCount;
    private int successCount;

    @JsonIgnore
    final static BigDecimal oneHundredPercent = new BigDecimal(100);

    public SuccessAverage(int maxCount) {
        this.maxCount = maxCount;
        this.successCount = 0;
        this.totalCount = 0;
    }

    public BigDecimal successPercent() {
        return successPercent(successCount, totalCount, 2);
    }

    public void incrementSuccessCount() {
        successCount++;
    }

    public void incrementTotalCount() {
        totalCount++;
    }

    static BigDecimal successPercent(int successCount, int totalCount, int scale) {
        return new BigDecimal(successCount).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).setScale(scale, RoundingMode.HALF_UP);
    }

    @JsonIgnore
    public static boolean isSuccess(BigDecimal bigDecimal) {
        return oneHundredPercent.compareTo(bigDecimal) >= 0;
    }
}
