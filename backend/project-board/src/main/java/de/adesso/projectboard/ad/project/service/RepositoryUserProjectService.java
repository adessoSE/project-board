package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.persistence.specification.StatusSpecification;
import de.adesso.projectboard.base.search.HibernateSearchService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.PageableUserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class RepositoryUserProjectService implements PageableUserProjectService {

    private final ProjectRepository projectRepo;

    private final UserService userService;

    private final HibernateSearchService managerSearchService;

    private final HibernateSearchService staffSearchService;

    private final Set<String> lobDependentStatus;

    private final Set<String> lobIndependentStatus;

    private final Set<String> managerStatus;

    @Autowired
    public RepositoryUserProjectService(ProjectRepository projectRepo,
                                        UserService userService,
                                        @Qualifier("managerSearchService") HibernateSearchService managerSearchService,
                                        @Qualifier("staffSearchService") HibernateSearchService staffSearchService,
                                        ProjectBoardConfigurationProperties properties) {
        this.projectRepo = projectRepo;
        this.userService = userService;
        this.managerSearchService = managerSearchService;
        this.staffSearchService = staffSearchService;

        this.lobDependentStatus = new HashSet<>(properties.getLobDependentStatus());
        this.lobIndependentStatus = new HashSet<>(properties.getLobIndependentStatus());
        this.managerStatus = Stream.concat(lobDependentStatus.stream(), lobIndependentStatus.stream())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Project> getProjectsForUser(User user, Sort sort) {
        return projectRepo.findAll(getProjectSpecificationForUser(user), sort);
    }

    @Override
    public List<Project> searchProjectsForUser(User user, String query, Sort sort) {
        if(userService.userIsManager(user)) {
            return managerSearchService.searchProjects(query, null);
        } else {
            var userLob = userService.getUserData(user).getLob();
            return staffSearchService.searchProjects(query, userLob);
        }
    }

    @Override
    public Page<Project> getProjectsForUserPaginated(User user, Pageable pageable) {
        return projectRepo.findAll(getProjectSpecificationForUser(user), pageable);
    }

    @Override
    public Page<Project> searchProjectsForUserPaginated(String query, User user, Pageable pageable) {
        if(userService.userIsManager(user)) {
            return managerSearchService.searchProjects(query, pageable,null);
        } else {
            var userLob = userService.getUserData(user).getLob();
            return staffSearchService.searchProjects(query, pageable, userLob);
        }
    }

    private Specification<Project> getProjectSpecificationForUser(User user) {
        if(userService.userIsManager(user)) {
            return new StatusSpecification(managerStatus, Set.of(), null);
        } else {
            var userLob = userService.getUserData(user).getLob();
            return new StatusSpecification(lobIndependentStatus, lobDependentStatus, userLob);
        }
    }

}
