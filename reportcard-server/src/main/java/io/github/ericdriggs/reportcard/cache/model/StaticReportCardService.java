package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.ReportCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticReportCardService {

    public static ReportCardService INSTANCE;

    @Autowired
    public void setReportCardService(ReportCardService reportCardService) {
        //TODO: sleep if null?
        StaticReportCardService.INSTANCE = reportCardService;
    }
}
