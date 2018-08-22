package de.adesso.projectboard.core.base.rest;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.scanner.RestProjectAttributeScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;

@RestController
@RequestMapping("/projects/")
public class ProjectBoardRestController {

    private final RestProjectAttributeScanner scanner;

    private final ProjectRepository projectRepository;

    private final ProjectBoardConfigurationProperties properties;

    private final EntityManager entityManager;

    @Autowired
    public ProjectBoardRestController(RestProjectAttributeScanner scanner,
                                      ProjectRepository projectRepository,
                                      ProjectBoardConfigurationProperties properties,
                                      EntityManager entityManager) {
        this.scanner = scanner;
        this.projectRepository = projectRepository;
        this.properties = properties;
        this.entityManager = entityManager;
    }

    @GetMapping("/{projectId}")
    public AbstractProject getById(@PathVariable long projectId) {
        Optional<AbstractProject> projectOptional = projectRepository.findById(projectId);

        if(projectOptional.isPresent()) {
            return projectOptional.get();
        } else {
            throw new ProjectNotFoundException();
        }
    }

    @GetMapping("/")
    public Iterable<? extends AbstractProject> getAll() {
        return projectRepository.findAll();
    }

    @GetMapping("/search/")
    @SuppressWarnings("unchecked")
    public Iterable<? extends AbstractProject> search(@RequestParam Map<String,String> requestParams) {
        Map<String, String> fieldParamValueMap = new LinkedHashMap<>();

        for(Map.Entry<String, String> entry : requestParams.entrySet()) {
            if(scanner.canQuery(entry.getKey())) {
                fieldParamValueMap.put(scanner.getFieldNameByQueryName(entry.getKey()), entry.getValue());
            }
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(properties.getProjectClass());
        Root root = query.from(properties.getProjectClass());

        List<Predicate> predicates = new ArrayList<>();

        for(Map.Entry<String, String> queryEntry : fieldParamValueMap.entrySet()) {
            Expression<String> actual = builder.lower(root.get(queryEntry.getKey()));
            String pattern = '%' + queryEntry.getValue().toLowerCase() + '%';

            predicates.add(builder.like(actual, pattern));
        }

        Predicate[] predicatesArr = new Predicate[predicates.size()];
        query.where(predicates.toArray(predicatesArr));

        return entityManager.createQuery(query.select(root)).getResultList();
    }

}
