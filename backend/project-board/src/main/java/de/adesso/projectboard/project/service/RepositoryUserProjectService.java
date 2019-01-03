package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
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

@Service
@Transactional(readOnly = true)
public class RepositoryUserProjectService implements PageableUserProjectService {

    private final UserService userService;

    private final ProjectRepository projectRepo;

    private final UserRepository userRepo;

    private final RepositoryProjectService projectService;

    @Autowired
    public RepositoryUserProjectService(UserService userService,
                                        ProjectRepository projectRepo,
                                        UserRepository userRepo,
                                        RepositoryProjectService projectService) {
        this.userService = userService;
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.projectService = projectService;
    }

    @Override
    public List<Project> getProjectsForUser(User user, Sort sort) {
        return projectRepo.findAllByStatusEscalatedOrOpen(sort);
    }

    @Override
    public List<Project> searchProjectsForUser(User user, String keyword, Sort sort) {
        return projectRepo.findAllByStatusEscalatedOrOpenAndKeyword(keyword, sort);
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
        return projectRepo.findAllByStatusEscalatedOrOpenPageable(pageable);
    }

    @Override
    public Page<Project> searchProjectsForUserPaginated(String keyword, User user, Pageable pageable) {
        return projectRepo.findAllByStatusEscalatedOrOpenAndKeywordPageable(keyword, pageable);
    }

}
