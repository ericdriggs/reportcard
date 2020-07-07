package com.ericdriggs.ragnarok;

import java.util.List;

import com.ericdriggs.ragnarok.db.Tables;
import com.ericdriggs.ragnarok.db.tables.pojos.Org;
import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RagnarokService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    DSLContext dsl;

    public List<Org> getOrgs() {
        return dsl
                .selectFrom(Tables.ORG)
                .fetch()
                .into(Org.class);
    }
}