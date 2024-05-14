package io.github.ericdriggs.reportcard.model.graph.condition;

import org.jooq.impl.DSL;
import org.jooq.Condition;
import org.jooq.impl.TableImpl;

@SuppressWarnings("rawtypes")
public class TableConditionMap extends java.util.HashMap<TableImpl, Condition> {

    @Override
    public Condition get(Object key) {
        return getCondition((TableImpl) key);
    }

    public Condition getCondition(TableImpl key) {
        Condition ret = super.get(key);
        if (ret != null) {
            return ret;
        }
        return DSL.condition("true");
    }

}
