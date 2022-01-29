package at.rajoub.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/*
 *This Annotation purpose is to annotate a ForeignKey in a table when it exists
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForeignKey {
    /** Joined to table name required */
    Class<?> joinedTo();
}
