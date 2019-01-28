package de.adesso.projectboard.base.projection.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * Wrapper for Spring's {@link AnnotationUtils} class.
 */
@Component
public class AnnotationUtilsWrapper {

    /**
     *
     * @param annotatedClass
     *          The annotated class to get the annotation of, not null.
     *
     * @param annotation
     *          The class of the annotation.
     *
     * @param <A>
     *          The type of the annotation.
     *
     * @return
     *          The annotation or {@code null} in case it is not present.
     *
     * @see AnnotationUtils#findAnnotation(Class, Class)
     */
    public <A extends Annotation> A findAnnotation(Class<?> annotatedClass, Class<A> annotation) {
        return AnnotationUtils.findAnnotation(annotatedClass, annotation);
    }

}
