package de.adesso.projectboard.core.base.project.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractProject {

    @Id
    @GeneratedValue
    private long id;

}
