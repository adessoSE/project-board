package de.adesso.projectboard.core.rest;

import de.adesso.projectboard.core.rest.security.persistence.UserProjectsAccessInfo;
import de.adesso.projectboard.core.rest.security.persistence.UserProjectsAccessInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/projects/access")
public class UserProjectsAccessController {

    private final UserProjectsAccessInfoRepository accessInfoRepo;

    @Autowired
    public UserProjectsAccessController(UserProjectsAccessInfoRepository accessInfoRepo) {
        this.accessInfoRepo = accessInfoRepo;
    }

    @GetMapping("/")
    public Iterable<UserProjectsAccessInfo> getAll() {
        return accessInfoRepo.findAll();
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public UserProjectsAccessInfo create(@Valid @RequestBody UserProjectsAccessInfo info) {
        return accessInfoRepo.save(info);
    }

}
