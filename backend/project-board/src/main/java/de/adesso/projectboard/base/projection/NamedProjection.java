package de.adesso.projectboard.base.projection;

import java.lang.annotation.*;

/**
 * Class level annotation to mark projection interfaces <b>interfaces</b> to be registered by
 * the {@link ProjectionService}.
 */
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
     *          The target class of the projection.
     */
    Class<?> target();

    /**
     *
     * @return
     *          {@code true}, iff the projection is the
     *          default projection of the target.
     */
    boolean defaultProjection() default false;

}
