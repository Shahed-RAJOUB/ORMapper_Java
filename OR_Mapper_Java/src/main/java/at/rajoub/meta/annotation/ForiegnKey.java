package at.rajoub.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForiegnKey {
    /** Field name. */
    String fieldName() default "";

    /** Column name. */
    String columnName() default "";

    /** Column type. */
    Class columnType() default Void.class;

    /** Nullable flag. */
    boolean nullable() default false;
}
