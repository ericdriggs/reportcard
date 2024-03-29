
package org.jooq.codegen;

import org.jooq.meta.Definition;

@SuppressWarnings("unused")
public class SuffixGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        if (mode.equals(Mode.POJO)) {
            //Reduce naming ambiguity
            return super.getJavaClassName(definition, mode) + "Pojo";
        } else if (mode.equals(Mode.DEFAULT)) {
            return super.getJavaClassName(definition, mode) + "Table";
        }
        return super.getJavaClassName(definition, mode);
    }
}
