package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.persist.BrowseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StaticBrowseService {

    @Autowired
    public void setReportCardService(BrowseService browseService) {
        StaticBrowseService.INSTANCE = browseService;
    }

    private static BrowseService INSTANCE;

    public static BrowseService getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        synchronized (StaticBrowseService.class) {
            for (int i = 1; i <= 10; i++) {
                if (INSTANCE != null) {
                    return INSTANCE;
                }

                try {
                    log.info("waiting for StaticBrowseService to initialize. attempts: " + i);
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        throw new IllegalStateException("unable to initialize StaticBrowseService");
    }

}
