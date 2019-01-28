package de.adesso.projectboard.base.projection;

import java.lang.annotation.*;

/**
 * Method parameter annotation to annotate controller handler method arguments
 * of type {@code Class<?>}. Annotating parameters of other type may lead to
 * runtime errors.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectionType {

    /**
     *
     * @return
     *          The target class of the projection.
     */
    Class<?> value();

}
