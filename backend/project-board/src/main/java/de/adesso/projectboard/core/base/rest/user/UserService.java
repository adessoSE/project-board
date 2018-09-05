package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmark;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmarkRepository;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Wrapper for the {@link UserRepository} to manage {@link User}s.
 *
 * @see UserRepository
 * @see AuthenticationInfo
 */
@Service
public class UserService {

    private final UserRepository userRepo;

    private final ProjectApplicationRepository applicationRepo;

    private final ProjectBookmarkRepository bookmarkRepo;

    private final AuthenticationInfo authInfo;

    @Autowired
    public UserService(UserRepository userRepo,
                       ProjectApplicationRepository applicationRepo,
                       ProjectBookmarkRepository bookmarkRepo,
                       AuthenticationInfo authInfo) {
        this.userRepo = userRepo;
        this.applicationRepo = applicationRepo;
        this.bookmarkRepo = bookmarkRepo;
        this.authInfo = authInfo;
    }

    /**
     * Returns the {@link User} object of the currently authenticated
     * user. Creates a new (persisted) {@link User} object when no
     * {@link User} object exists for the current user.
     *
     * @return
     *          The {@link User} object of the currently
     *          authenticated user.
     *
     * @see AuthenticationInfo#getUserId()
     */
    public User getCurrentUser() {
        Optional<User> optionalUser = userRepo.findById(authInfo.getUserId());

        return optionalUser.orElseGet(() -> userRepo.save(new User(authInfo.getUserId())));
    }

    /**
     *
     * @return
     *          The user ID of the current user returned by
     *          {@link AuthenticationInfo#getUserId()}.
     *
     * @see #getCurrentUser()
     */
    public String getCurrentUserId() {
        return authInfo.getUserId();
    }

    /**
     *
     * @param userId
     *          The id of the user.
     *
     * @return
     *          <i>true</i>, if the user with the given
     *          id exists, <i>false</i> otherwise.
     */
    public boolean userExists(String userId) {
        return userRepo.existsById(userId);
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to check for the {@link ProjectBookmark}.
     *
     * @param bookmarkId
     *          The id of the {@link ProjectBookmark}.
     *
     * @return
     *          <i>true</i>, when a {@link ProjectBookmark} with the the given
     *          {@code bookmarkId} exists and the users {@link User#getBookmarks() bookmarks}
     *          contain the bookmark.
     *
     * @see ProjectBookmarkRepository#findById(Object)
     */
    public boolean userHasBookmark(String userId, long bookmarkId) {
        Optional<ProjectBookmark> bookmarkOptional = bookmarkRepo.findById(bookmarkId);

        return bookmarkOptional.filter(projectBookmark -> userRepo.existsByIdAndBookmarksContaining(userId, projectBookmark)).isPresent();
    }

    /**
     *
     * @param userId
     *          The id of the user to retrieve.
     *
     * @return
     *          A user {@link Optional}.
     *
     * @see UserRepository#findById(Object)
     */
    public Optional<User> getUserById(String userId) {
        return userRepo.findById(userId);
    }

    /**
     * Adds a {@link ProjectBookmark} to a user and persists the updated
     * user.
     *
     * <p>
     *     Note: the given bookmark gets persisted if it is not persisted
     *     yet.
     * </p>
     *
     * @param user
     *          The {@link User} to add the {@link ProjectBookmark} to.
     *
     * @param bookmark
     *          The {@link ProjectBookmark} to add to the {@link User}.
     *
     * @see User#addBookmark(ProjectBookmark)
     */
    public ProjectBookmark addBookmarkToUser(User user, ProjectBookmark bookmark) {
        ProjectBookmark savedBookmark = bookmarkRepo.save(bookmark);

        user.addBookmark(bookmark);
        userRepo.save(user);

        return savedBookmark;
    }

    /**
     * Adds a {@link ProjectApplication} to a user and persists the updated
     * user.
     *
     * <p>
     *     Note: the given application gets persisted if it is not persisted
     *     yet.
     * </p>
     *
     * @param user
     *          The {@link User} to add the {@link ProjectApplication} to.
     *
     * @param application
     *          The {@link ProjectApplication} to add to the user.
     *
     * @see User#addApplication(ProjectApplication)
     */
    public ProjectApplication addApplicationToUser(User user, ProjectApplication application) {
        ProjectApplication savedApplication = applicationRepo.save(application);

        user.addApplication(application);
        userRepo.save(user);

        return savedApplication;
    }

    /**
     * Removes the {@code bookmark} from the {@code user} {@link User#getBookmarks() bookmarks}
     * and removes it from the database.
     *
     * @param user
     *          The {@link User} to remove the {@link ProjectBookmark} from.
     *
     * @param bookmark
     *          The {@link ProjectBookmark bookmark} to remove.
     *
     * @see User#removeBookmark(ProjectBookmark)
     */
    public void removeBookmarkFromUser(User user, ProjectBookmark bookmark) {
        if(user.removeBookmark(bookmark)) {
            bookmarkRepo.delete(bookmark);
        }
    }

}
