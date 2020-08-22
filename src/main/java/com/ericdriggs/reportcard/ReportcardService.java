package com.ericdriggs.reportcard;

import java.util.List;

import com.ericdriggs.reportcard.db.Tables;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportcardService {

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