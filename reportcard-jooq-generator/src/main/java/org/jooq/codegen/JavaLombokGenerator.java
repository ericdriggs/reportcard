package org.jooq.codegen;

import org.jooq.meta.Definition;
import org.jooq.meta.TypedElementDefinition;

/**
 * Uses lombok.Generated as a class annotation instead of a javax.annotation.(processing?.)Generated
 * <ol>
 *     <li>javax.annotation.(processing?.)Generated only has <code>SOURCE</code> retention,
 *         while lombok.Generated <code>CLASS</code> retention. </li>
 *     <li>lombok.Generated is ignored by jacoco when calculating code coverage</li>
 * </ol>
 *
 * Note: lombok is not a dependency of jooq, so if you use this generator,
 * it is YOUR responsibility to ensure your project supports the
 * lombok.Generated annotation
 *
 * @see <a href="https://github.com/jOOQ/jOOQ/issues/8617">https://github.com/jOOQ/jOOQ/issues/8617</a>
 * @see <a href="https://github.com/jacoco/jacoco/pull/731">https://github.com/jacoco/jacoco/pull/731</a>
 */
@SuppressWarnings("unused")
public class JavaLombokGenerator extends JavaGenerator {
    @Override
    protected void printClassAnnotations(JavaWriter out, Definition definition, GeneratorStrategy.Mode mode) {
        if (generateGeneratedAnnotation()) {
            String generated = "lombok.Generated";
            out.println("@%s", out.ref(generated));
        }

        if (mode.equals(GeneratorStrategy.Mode.POJO)) {
            out.println("@lombok.AllArgsConstructor");
            out.println("@lombok.Data");
            out.println("@lombok.experimental.SuperBuilder(toBuilder = true)");
            out.println("@lombok.NoArgsConstructor");
        }

        //if (!scala) //TODO: uncomment when scala variable is protected instead of private
        out.println("@%s({ \"all\", \"unchecked\", \"rawtypes\" })", out.ref("java.lang.SuppressWarnings"));

    }

    @Override
    protected void generatePojoGetter(TypedElementDefinition<?> column, int index, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }

    @Override
    protected void generatePojoSetter(TypedElementDefinition<?> column, int index, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }


    @Override
    protected void generatePojoCopyConstructor(Definition tableOrUDT, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }

    @Override
    protected void generatePojoMultiConstructor(Definition tableOrUDT, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }

    @Override
    protected void generatePojoDefaultConstructor(Definition tableOrUDT, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }

    @Override
    protected void generatePojoToString(Definition tableOrUDT, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }

    @Override
    public boolean generatePojosEqualsAndHashCode() {
        return false;
    }

    @Override
    protected void generatePojoEqualsAndHashCode(Definition tableOrUDT, JavaWriter out) {
        //No-op. Lombok handles boiler plate
    }





}
