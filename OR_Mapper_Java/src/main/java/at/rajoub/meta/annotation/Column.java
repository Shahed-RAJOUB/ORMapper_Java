package at.rajoub.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 *This Annotation purpose is to annotate any type of the columns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    /**
     * Column name is required to map properly to the actual column in the table.
     */
    String columnName() default "";
}
