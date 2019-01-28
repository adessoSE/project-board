package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.projection.exception.MultipleDefaultProjectionsException;
import de.adesso.projectboard.base.projection.exception.MultipleSimilarlyNamedProjectionsException;
import de.adesso.projectboard.base.projection.util.AnnotationUtilsWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Service} to scan for projection interfaces annotated with a {@link NamedProjection}
 * annotation. Provides methods to retrieve projections by name and/or target class.
 */
@Service
@Slf4j
public class ProjectionService implements InitializingBean {

    private final String BASE_PACKAGE = "de/adesso/projectboard/base";

    private final AnnotatedTypeScanner typeScanner;

    private final AnnotationUtilsWrapper annotationUtilsWrapper;

    /**
     * Maps a projection name and target class to a projection.
     */
    final Map<Pair<String, Class<?>>, Class<?>> projectionClassMap;

    /**
     * Maps a target class to its default projection.
     */
    final Map<Class<?>, Class<?>> defaultProjectionClassMap;

    @Autowired
    public ProjectionService(AnnotatedTypeScanner typeScanner, AnnotationUtilsWrapper annotationUtilsWrapper) {
        this.typeScanner = typeScanner;
        this.annotationUtilsWrapper = annotationUtilsWrapper;

        this.projectionClassMap = new HashMap<>();
        this.defaultProjectionClassMap = new HashMap<>();
    }

    /**
     *
     * @param projectionName
     *          The name of the projection, not null.
     *
     * @param projectionTarget
     *          The target of the projection, not null.
     *
     * @return
     *          The projection matching the given {@code projectionName}
     *          and {@code projectionTarget} or the default projection for the given {@code projectionTarget}
     *          when no matching projection is present.
     *
     * @see #getDefault(Class)
     */
    public Class<?> getByNameOrDefault(@NonNull String projectionName, @NonNull Class<?> projectionTarget) {
        return projectionClassMap.getOrDefault(Pair.of(projectionName, projectionTarget), getDefault(projectionTarget));
    }

    /**
     *
     * @param projectionTarget
     *          The target class of the projection, not null.
     *
     * @return
     *          The default projection for the given {@code projectionTarget},
     *          may be null.
     */
    public Class<?> getDefault(Class<?> projectionTarget) {
        return defaultProjectionClassMap.get(projectionTarget);
    }

    /**
     * Call the {@link #addProjectionInterface(NamedProjection, Class)} for each class
     * returned by {@link #getAnnotatedInterfaces(String)}.
     *
     * @param basePackage
     *          The base package to search annotated interfaces in, not null.
     */
    void addProjectionInterfaces(String basePackage) throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        for(var annotatedInterface : getAnnotatedInterfaces(basePackage)) {
            var annotation = annotationUtilsWrapper.findAnnotation(annotatedInterface, NamedProjection.class);
            addProjectionInterface(annotation, annotatedInterface);
        }
    }

    /**
     *
     * @param basePackage
     *          The base package to search annotated interfaces in, not null.
     *
     * @return
     *          All interface class instances annotated with {@link NamedProjection}.
     */
    Set<Class<?>> getAnnotatedInterfaces(String basePackage) {
        return typeScanner.findTypes(basePackage).stream()
                .filter(Class::isInterface)
                .collect(Collectors.toSet());
    }

    /**
     *
     * @param annotation
     *          The annotation of the annotated interface, not null.
     *
     * @param annotatedInterface
     *          The class instance of the annotated interface, not null.
     *
     * @throws MultipleSimilarlyNamedProjectionsException
     *              When at least two {@link NamedProjection} annotations on
     *              different interfaces are present that have the same {@code name}
     *              and {@code target} values.
     *
     * @throws MultipleDefaultProjectionsException
     *              When at least two {@link NamedProjection} annotations on
     *              different interfaces are present that have the same {@code target}
     *              value and are marked as the default projection for that type.
     */
    void addProjectionInterface(NamedProjection annotation, Class<?> annotatedInterface) throws MultipleSimilarlyNamedProjectionsException, MultipleDefaultProjectionsException {
        var projectionName = getProjectionName(annotation, annotatedInterface);
        var projectionTarget = annotation.target();
        var defaultProjection = annotation.defaultProjection();
        Pair<String, Class<?>> nameTargetPair = Pair.of(projectionName, projectionTarget);

        if(projectionClassMap.containsKey(nameTargetPair)) {
            throw new MultipleSimilarlyNamedProjectionsException(projectionName, projectionTarget);
        }
        projectionClassMap.put(nameTargetPair, annotatedInterface);

        log.debug(String.format("Added projection for target '%s' with name '%s'!",
                projectionTarget.toString(), projectionName));

        if(defaultProjection) {
            if(defaultProjectionClassMap.containsKey(projectionTarget)) {
                throw new MultipleDefaultProjectionsException(projectionTarget);
            }
            defaultProjectionClassMap.put(projectionTarget, annotatedInterface);

            log.debug(String.format("Added default projection for target '%s' with name '%s'!",
                    projectionTarget.toString(), projectionName));
        }
    }

    /**
     *
     * @param annotation
     *          The annotation of the annotated interface, not null.
     *
     * @param projectionInterface
     *          The class instance of the annotated interface, not null.
     *
     * @return
     *          The {@code name} value of the given {@code annotation} or the lower case simple
     *          class name in case it is empty.
     */
    String getProjectionName(NamedProjection annotation, Class<?> projectionInterface) {
        var annotationName = annotation.name();

        if(annotationName.isEmpty()) {
            return projectionInterface.getSimpleName().toLowerCase();
        }

        return annotationName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        addProjectionInterfaces(BASE_PACKAGE);
    }

}
