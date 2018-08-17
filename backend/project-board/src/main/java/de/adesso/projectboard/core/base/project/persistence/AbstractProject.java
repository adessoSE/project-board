package de.adesso.projectboard.core.base.project.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract base class for project entities with a long as the id.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractProject {

    @Id
    private long id;

}
