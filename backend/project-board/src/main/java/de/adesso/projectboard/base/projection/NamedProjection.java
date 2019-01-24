package de.adesso.projectboard.base.projection;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedProjection {

    /**
     *
     * @return
     *          The name of the projection.
     */
    String name() default "";

    /**
     *
     * @return
     *          The target of the projection.
     */
    ProjectionTarget target();

    /**
     *
     * @return
     *          {@code true}, iff the projection is the
     *          default projection of the target.
     */
    boolean defaultProjection() default false;

}
