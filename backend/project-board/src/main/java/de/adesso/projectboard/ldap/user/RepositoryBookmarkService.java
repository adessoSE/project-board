package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.project.service.RepositoryProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Service} to to provide functionality to manage {@link Project Project Bookmarks}.
 *
 * @see LdapUserService
 * @see RepositoryProjectService
 */
@Profile("adesso-ad")
@Service
public class RepositoryBookmarkService implements BookmarkService {

    private final UserRepository userRepo;

    @Autowired
    public RepositoryBookmarkService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Project addBookmarkToUser(User user, Project project) {
        // add the project and persist the entity
        user.addBookmark(project);
        userRepo.save(user);

        return project;
    }

    @Override
    public void removeBookmarkOfUser(User user, Project project) {
        if(userRepo.existsByIdAndBookmarksContaining(user.getId(), project)) {

            // remove the bookmark and update the entity
            user.removeBookmark(project);
            userRepo.save(user);

        } else {
            throw new BookmarkNotFoundException();
        }
    }

    @Override
    public List<Project> getBookmarksOfUser(User user) {
        return new ArrayList<>(user.getBookmarks());
    }

    @Override
    public boolean userHasBookmark(User user, Project project) {
        return userRepo.existsByIdAndBookmarksContaining(user.getId(), project);
    }

}
