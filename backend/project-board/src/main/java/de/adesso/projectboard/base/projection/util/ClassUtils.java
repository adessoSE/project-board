package de.adesso.projectboard.base.projection.util;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class ClassUtils {

    /**
     *
     * @param className
     *          The fully qualified name of the class to get the class instance
     *          for, not null.
     *
     * @return
     *          The class instance of the class with the given
     *          {@code className}.
     *
     * @throws ClassNotFoundException
     *          When no class with the given name was found.
     *
     * @see Class#forName(String)
     */
    public Class<?> getClassForName(@NonNull String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    /**
     *
     * @param annotatedClass
     *          The class to get the given {@code annotation} from, not null.
     *
     * @param annotation
     *          The class instance of the annotation to get, not null.
     *
     * @param <T>
     *          The type of the annotation.
     *
     * @return
     *          The annotation or {@code null} if it is not present.
     *
     * @see Class#getAnnotation(Class)
     */
    public <T extends Annotation> T getAnnotation(@NonNull Class<?> annotatedClass, @NonNull Class<T> annotation) {
        return annotatedClass.getAnnotation(annotation);
    }

}
