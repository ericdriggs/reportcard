package io.github.ericdriggs.reportcard;

import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * abstract db service class with connection to DB through DSLContext
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public abstract class AbstractReportCardService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected DSLContext dsl;

    private AbstractReportCardService() {
        throw new RuntimeException("needs dsl in constructor");
    }

    @Autowired
    public AbstractReportCardService(DSLContext dsl) {
        this.dsl = dsl;
    }

}