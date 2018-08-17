package de.adesso.projectboard.core.base.rest;

import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.scanner.RestProjectAttributeScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectBoardRestController {

    private final RestProjectAttributeScanner scanner;

    private final ProjectRepository repository;

    @Autowired
    public ProjectBoardRestController(RestProjectAttributeScanner scanner, ProjectRepository repository) {
        this.scanner = scanner;
        this.repository = repository;
    }

    @GetMapping("/api/test")
    public String test() {
        return "Test";
    }

}
