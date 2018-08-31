package de.adesso.projectboard.core.rest;

import de.adesso.projectboard.core.rest.security.persistence.UserAccessInfo;
import de.adesso.projectboard.core.rest.security.persistence.UserAccessInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@PreAuthorize("hasRole('admin')")
@RestController
@RequestMapping("/projects/access")
public class UserAccessController {

    private final UserAccessInfoRepository accessInfoRepo;

    @Autowired
    public UserAccessController(UserAccessInfoRepository accessInfoRepo) {
        this.accessInfoRepo = accessInfoRepo;
    }

    @GetMapping("/")
    public Iterable<UserAccessInfo> getAll() {
        return accessInfoRepo.findAll();
    }

    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public UserAccessInfo create(@Valid @RequestBody UserAccessInfo info) {
        return accessInfoRepo.save(info);
    }

}
