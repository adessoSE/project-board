package de.adesso.projectboard.base.application.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

/**
 * Service interface to provide functionality to manage {@link ProjectApplication}s.
 */
public interface ApplicationService {

    /**
     *
     * @param user
     *          The {@link User}.
     *
     * @param project
     *          The {@link Project}.
     *
     * @return
     *          {@code true}, iff the user's {@link User#applications applications}
     *          contain an application that refers to the project.
     */
    boolean userHasAppliedForProject(User user, Project project);

    /**
     * Creates a new {@link ProjectApplication} and add it
     * to the user's {@link User#applications applications}.
     *
     * @param user
     *          The {@link User} to create an application for.
     *
     * @param projectId
     *          The ID of the {@link Project} the user applied for.
     *
     * @param comment
     *          A optional comment.
     *
     * @return
     *          The created {@link ProjectApplication}.
     *
     * @throws AlreadyAppliedException
     *          When the given {@code user} has already applied for the project.
     */
    ProjectApplication createApplicationForUser(User user, String projectId, String comment) throws AlreadyAppliedException;

    /**
     *
     * @param user
     *          The {@link User} to get the applications of.
     *
     * @param sort
     * @return
     *          The user's {@link ProjectApplication applications}.
     */
    List<ProjectApplication> getApplicationsOfUser(User user, Sort sort);

    /**
     *
     * @param users
     *          The {@link User}s to get the staff applications of.
     *
     * @param sort
     *          The sorting to apply.
     *
     * @return
     *          The {@link ProjectApplication applications} of the staff
     *          members of the user.
     */
    List<ProjectApplication> getApplicationsOfUsers(Collection<User> users, Sort sort);

}
