package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.projection.exception.MultipleDefaultProjectionsException;
import de.adesso.projectboard.base.projection.exception.MultipleSimilarlyNamedProjectionsException;
import de.adesso.projectboard.base.projection.util.ClassUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ProjectionService implements InitializingBean {

    private final String BASE_PACKAGE = "de/adesso/projectboard/base";

    private final NamedProjectionCandidateComponentProvider componentProvider;

    private final ClassUtils classUtils;

    final Map<Pair<String, ProjectionTarget>, Class<?>> projectionClassMap;

    final Map<ProjectionTarget, Class<?>> defaultProjectionClassMap;

    @Autowired
    public ProjectionService(NamedProjectionCandidateComponentProvider componentProvider, ClassUtils classUtils) {
        this.componentProvider = componentProvider;
        this.classUtils = classUtils;

        this.projectionClassMap = new HashMap<>();
        this.defaultProjectionClassMap = new HashMap<>();
    }

    /**
     *
     * @param projectionName
     *          The name of the projection, not null.
     *
     * @param target
     *          The target of the projection, not null.
     *
     * @return
     *          The projection matching the given {@code projectionName}
     *          and {@code target} or the default projection for the given {@code target}
     *          when no matching projection is present.
     *
     * @see #getDefault(ProjectionTarget)
     */
    public Class<?> getByNameOrDefault(@NonNull String projectionName, @NonNull ProjectionTarget target) {
        return projectionClassMap.getOrDefault(Pair.of(projectionName, target), getDefault(target));
    }

    /**
     *
     * @param target
     *          The target of the projection, not null.
     *
     * @return
     *          The default projection for the given {@code target}.
     */
    public Class<?> getDefault(ProjectionTarget target) {
        return defaultProjectionClassMap.get(target);
    }

    /**
     * Calls the {@link #addProjectionInterface(NamedProjection, Class)} method for each
     * pair returned by {@link #getAnnotatedInterfaces(String)}.
     *
     * @param basePackage
     *          The base package to search annotated interfaces in, not null.
     */
    void addProjectionInterfaces(String basePackage) throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        for(var annotationInterfacePair : getAnnotatedInterfaces(basePackage)) {
            var annotation = annotationInterfacePair.getFirst();
            var annotatedInterface = annotationInterfacePair.getSecond();

            addProjectionInterface(annotation, annotatedInterface);
        }
    }

    /**
     *
     * @param basePackage
     *          The base package to search annotated interfaces in, not null.
     *
     * @return
     *          All annotated class instances and their corresponding
     *          annotation.
     */
    Set<Pair<NamedProjection, Class<?>>> getAnnotatedInterfaces(String basePackage) {
        var annotationClassPairs = new HashSet<Pair<NamedProjection, Class<?>>>();

        for(var beanDefinition : componentProvider.findCandidateComponents(basePackage)) {
            try {
                var annotatedClass = classUtils.getClassForName(beanDefinition.getBeanClassName());
                var annotation = classUtils.getAnnotation(annotatedClass, NamedProjection.class);

                annotationClassPairs.add(Pair.of(annotation, annotatedClass));
            } catch (ClassNotFoundException e) {
                log.debug("Annotated class not found!", e);
            }
        }

        return annotationClassPairs;
    }

    /**
     *
     * @param annotation
     *          The annotation of the annotated interface, not null.
     *
     * @param projectionInterface
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
    void addProjectionInterface(NamedProjection annotation, Class<?> projectionInterface) throws MultipleSimilarlyNamedProjectionsException, MultipleDefaultProjectionsException {
        var projectionName = getProjectionName(annotation, projectionInterface);
        var projectionTarget = annotation.target();
        var defaultProjection = annotation.defaultProjection();
        var nameTargetPair = Pair.of(projectionName, projectionTarget);

        if(projectionClassMap.containsKey(nameTargetPair)) {
            throw new MultipleSimilarlyNamedProjectionsException(projectionName, projectionTarget);
        }
        projectionClassMap.put(nameTargetPair, projectionInterface);

        log.debug(String.format("Added projection for target '%s' with name '%s'!",
                projectionTarget.toString(), projectionName));

        if(defaultProjection) {
            if(defaultProjectionClassMap.containsKey(projectionTarget)) {
                throw new MultipleDefaultProjectionsException(projectionTarget);
            }
            defaultProjectionClassMap.put(projectionTarget, projectionInterface);

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
