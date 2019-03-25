package de.adesso.projectboard.base.scheduled;

import java.lang.annotation.*;

/**
 * Annotation to annotate {@link ScheduledJob} implementations that are marked as spring beans.
 * Can be used to explicitly show that the implementation is automatically registered for
 * execution or disable automatic registration.
 *
 * @see ScheduledJobExecutor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AutoRegistered {

    /**
     *
     * @return
     *          {@code true}, iff the annotated {@link ScheduledJob}
     *          should be automatically registered.
     */
    boolean value() default true;

}
