package de.adesso.projectboard.base.projection;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * {@link ClassPathScanningCandidateComponentProvider} that includes interfaces.
 *
 * @param <T>
 *          The type of annotation to match.
 */
public abstract class InterfaceCandidateComponentProvider<T extends Annotation> extends ClassPathScanningCandidateComponentProvider {

    /**
     * Constructs a new instance. Excludes the default filters and adds a
     * new {@link AnnotationTypeFilter} to include interfaces annotated
     * with the annotation with the corresponding {@code annotationClass}.
     *
     * @param annotationClass
     *          The class of annotation to get the annotated interfaces
     *          for, not null.
     */
    public InterfaceCandidateComponentProvider(@NonNull Class<T> annotationClass) {
        super(false);

        addIncludeFilter(new AnnotationTypeFilter(annotationClass, false));
    }

    /**
     * Overrides the default behaviour of the {@link ClassPathScanningCandidateComponentProvider}
     * to include interfaces.
     *
     * @param beanDefinition
     *          The bean definition of the potential candidate component.
     *
     * @return
     *          {@code true}, iff the potential candidate component
     *          is an interface.
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface();
    }

}
