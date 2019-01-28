package de.adesso.projectboard.base.project.rest;

import org.springframework.http.ResponseEntity;

public abstract class BaseProjectController {

    public abstract ResponseEntity<?> getById(String projectId);

}
