package de.adesso.projectboard.core.base.rest;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.scanner.RestProjectAttributeScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/")
public class ProjectBoardRestController {

    private final RestProjectAttributeScanner scanner;

    private final ProjectRepository repository;

    @Autowired
    public ProjectBoardRestController(RestProjectAttributeScanner scanner, ProjectRepository repository) {
        this.scanner = scanner;
        this.repository = repository;
    }

    @GetMapping("/{projectId}")
    public AbstractProject getById(@PathVariable long projectId) {
        return repository.findById(projectId).get();
    }

    @GetMapping("/")
    public Iterable<AbstractProject> getAll() {
        return repository.findAll();
    }

}
