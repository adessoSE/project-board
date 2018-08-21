package de.adesso.projectboard.core.base.rest.scanner;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface QueryName {

    /**
     *
     * @return
     *          The query name of the field.
     */
    String value();

}
