package de.adesso.projectboard.core.base.user.persistence;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmark;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class User {

    @Id
    private String username;

    @OneToMany
    List<ProjectBookmark> bookmarks;

    @OneToMany
    List<ProjectApplication> applications;

}
