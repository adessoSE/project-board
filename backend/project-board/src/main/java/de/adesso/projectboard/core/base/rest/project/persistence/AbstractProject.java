package de.adesso.projectboard.core.base.rest.project.persistence;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Abstract base class for project entities with a long as the id.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class AbstractProject {

    @Id
    private long id;

}
