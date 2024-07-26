package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
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
public abstract class AbstractPersistService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected DSLContext dsl;

    protected final ObjectMapper mapper = SharedObjectMappers.simpleObjectMapper;

    private AbstractPersistService() {
        throw new RuntimeException("needs dsl in constructor");
    }

    @Autowired
    public AbstractPersistService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public DSLContext getDsl() {
        return dsl;
    }
}