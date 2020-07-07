package com.ericdriggs.ragnarok;

        import java.util.List;
        import java.util.stream.Collectors;

        import com.ericdriggs.ragnarok.db.Tables;
        import com.ericdriggs.ragnarok.model.Org;
        import org.jooq.DSLContext;
        import org.modelmapper.ModelMapper;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
//        import com.ericdriggs.ragnarok.db.model.Tables;
//        import co.cantina.springjooq.domain.Book;

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
                .stream()
                .map(e -> mapper.map(e, Org.class))
                .collect(Collectors.toList());
    }
}