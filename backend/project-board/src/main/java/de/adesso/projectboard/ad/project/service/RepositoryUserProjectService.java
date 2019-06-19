package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.persistence.specification.StatusSpecification;
import de.adesso.projectboard.base.search.HibernateSearchService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.PageableUserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RepositoryUserProjectService implements PageableUserProjectService {

    public static final Set<String> LOB_INDEPENDENT_STATUS = Set.of("eskaliert", "escalated");

    public static final Set<String> LOB_DEPENDENT_STATUS = Set.of("open", "offen");

    private final ProjectRepository projectRepo;

    private final UserService userService;

    private final HibernateSearchService hibernateSearchService;

    @Autowired
    public RepositoryUserProjectService(ProjectRepository projectRepo,
                                        UserService userService,
                                        HibernateSearchService hibernateSearchService) {
        this.projectRepo = projectRepo;
        this.userService = userService;
        this.hibernateSearchService = hibernateSearchService;
    }

    @Override
    public List<Project> getProjectsForUser(User user, Sort sort) {
        var userLob = userService.getUserData(user).getLob();
        return projectRepo.findAll(new StatusSpecification(LOB_INDEPENDENT_STATUS, LOB_DEPENDENT_STATUS, userLob), sort);
    }

    @Override
    public List<Project> searchProjectsForUser(User user, String query, Sort sort) {
        return hibernateSearchService.searchProjects(query, Set.of());
    }

    @Override
    public Page<Project> getProjectsForUserPaginated(User user, Pageable pageable) {
        var userLob = userService.getUserData(user).getLob();
        return projectRepo.findAll(new StatusSpecification(LOB_INDEPENDENT_STATUS, LOB_DEPENDENT_STATUS, userLob), pageable);
    }

    @Override
    public Page<Project> searchProjectsForUserPaginated(String query, User user, Pageable pageable) {
        return hibernateSearchService.searchProjects(query, Set.of(), pageable);
    }

}
