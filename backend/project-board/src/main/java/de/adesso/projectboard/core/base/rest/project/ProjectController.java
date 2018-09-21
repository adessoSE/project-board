package de.adesso.projectboard.core.base.rest.project;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.scanner.RestProjectAttributeScanner;
import de.adesso.projectboard.core.base.rest.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final RestProjectAttributeScanner scanner;

    private final ProjectRepository projectRepository;

    private final ProjectBoardConfigurationProperties properties;

    private final EntityManager entityManager;

    private final UserService userService;

    @Autowired
    public ProjectController(RestProjectAttributeScanner scanner,
                             ProjectRepository projectRepository,
                             ProjectBoardConfigurationProperties properties,
                             EntityManager entityManager, UserService userService) {
        this.scanner = scanner;
        this.projectRepository = projectRepository;
        this.properties = properties;
        this.entityManager = entityManager;
        this.userService = userService;
    }


    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(value = "/{projectId}",
            produces = "application/json"
    )
    public AbstractProject getById(@PathVariable long projectId) {
        Optional<AbstractProject> projectOptional = projectRepository.findById(projectId);

        if(projectOptional.isPresent()) {
            return projectOptional.get();
        } else {
            throw new ProjectNotFoundException();
        }
    }


    @PreAuthorize("hasRole('admin')")
    @GetMapping(path = "/all",
            produces = "application/json"
    )
    public Iterable<? extends AbstractProject> getAll() {
        return projectRepository.findAll();
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(produces = "application/json")
    public Iterable<? extends AbstractProject> getAllForUser() {
        String userLob = userService.getCurrentUser().getLob();

        return StreamSupport.stream(projectRepository.findAll().spliterator(), false)
                .map(project -> (Project) project)
                .filter(jiraProject -> {

                    String projectLob = jiraProject.getLob();
                    boolean hasLob = projectLob != null;
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(jiraProject.getStatus());
                    boolean isOpen = "offen".equalsIgnoreCase(jiraProject.getStatus());

                    // exclude projects with a different status than "Offen" or "eskaliert"
                    if(isEscalated || (!hasLob && isOpen)) {
                        return true;
                    }

                    if(hasLob && isOpen) {
                        return userLob.equalsIgnoreCase(projectLob);
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(value = "/search",
            produces = "application/json"
    )
    @SuppressWarnings("unchecked")
    public Iterable<? extends AbstractProject> search(@RequestParam Map<String,String> requestParams) {
        // map the query params to the corresponding field name
        Map<String, String> fieldParamValueMap = new LinkedHashMap<>();

        for(Map.Entry<String, String> entry : requestParams.entrySet()) {
            if(scanner.canQuery(entry.getKey())) {
                fieldParamValueMap.put(scanner.getFieldNameByQueryName(entry.getKey()), entry.getValue());
            }
        }

        // TODO: SQL injection possible?

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(properties.getProjectClass());
        Root root = query.from(properties.getProjectClass());

        List<Predicate> predicates = new ArrayList<>();

        // create a predicate for each query param
        for(Map.Entry<String, String> queryEntry : fieldParamValueMap.entrySet()) {
            Expression<String> actual = builder.lower(root.get(queryEntry.getKey()));
            String pattern = '%' + queryEntry.getValue().toLowerCase() + '%';

            predicates.add(builder.like(actual, pattern));
        }

        // create a disjunction of all predicates
        Predicate[] predicatesArr = new Predicate[predicates.size()];
        query.where(builder.or(predicates.toArray(predicatesArr)));

        return entityManager.createQuery(query.select(root)).getResultList();
    }

}
