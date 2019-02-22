package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.persistence.specification.StatusSpecification;
import de.adesso.projectboard.base.search.HibernateSearchService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
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

    private static final Set<String> ALL_STATUS = Set.of("open", "offen", "eskaliert", "escalated");

    private final UserService userService;

    private final ProjectRepository projectRepo;

    private final UserRepository userRepo;

    private final RepositoryProjectService projectService;

    private final HibernateSearchService hibernateSearchService;

    @Autowired
    public RepositoryUserProjectService(UserService userService,
                                        ProjectRepository projectRepo,
                                        UserRepository userRepo,
                                        RepositoryProjectService projectService,
                                        HibernateSearchService hibernateSearchService) {
        this.userService = userService;
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.projectService = projectService;
        this.hibernateSearchService = hibernateSearchService;
    }

    @Override
    public List<Project> getProjectsForUser(User user, Sort sort) {
        return projectRepo.findAll(new StatusSpecification(ALL_STATUS), sort);
    }

    @Override
    public List<Project> searchProjectsForUser(User user, String query, Sort sort) {
        return hibernateSearchService.searchProjects(query, ALL_STATUS);
    }

    @Override
    public boolean userOwnsProject(User user, Project project) {
        return userRepo.existsByIdAndOwnedProjectsContaining(user.getId(), project);
    }

    @Override
    @Transactional
    public Project createProjectForUser(Project project, User user) {
        Project createdProject = projectService.createProject(project);

        user.addOwnedProject(createdProject);
        userService.save(user);

        return createdProject;
    }

    @Override
    @Transactional
    public Project addProjectToUser(User user, Project project) {
        user.addOwnedProject(project);
        userService.save(user);

        return project;
    }

    @Override
    public Page<Project> getProjectsForUserPaginated(User user, Pageable pageable) {
        return projectRepo.findAll(new StatusSpecification(ALL_STATUS), pageable);
    }

    @Override
    public Page<Project> searchProjectsForUserPaginated(String query, User user, Pageable pageable) {
        return hibernateSearchService.searchProjects(query, ALL_STATUS, pageable);
    }

}
