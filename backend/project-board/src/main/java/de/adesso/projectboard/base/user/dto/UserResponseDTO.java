package de.adesso.projectboard.base.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.adesso.projectboard.base.access.dto.UserAccessInfoResponseDTO;
import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.Data;

import java.io.Serializable;

/**
 * The DTO of a {@link User} send back to the user.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String lob;

    private boolean isBoss;

    private UserAccessInfoResponseDTO accessInfo;

    private CollectionLink applications;

    private CollectionLink bookmarks;

    private CollectionLink staff;

    private CollectionLink projects;

    /**
     *
     * @param user
     *          The {@link User} to convert to a DTO.
     *
     * @return
     *          The DTO.
     */
    public static UserResponseDTO fromUser(User user) {
        // create new applications link to break circular reference to applications
        // user -> application -> user -> application -> ....
        CollectionLink applicationsLink = new CollectionLink();
        applicationsLink.setCount(user.getApplications().size());
        applicationsLink.setPath(String.format("/users/%s/applications", user.getId()));

        // create new bookmarks link
        CollectionLink bookmarksLink = new CollectionLink();
        bookmarksLink.setCount(user.getBookmarks().size());
        bookmarksLink.setPath(String.format("/users/%s/bookmarks", user.getId()));

        // create a new access info dto to break circular reference
        // user -> accessInfoList -> user
        UserAccessInfoResponseDTO infoDTO;
        if(user.hasAccess()) {
            AccessInfo activeInfo = user.getLatestAccessInfo();
            infoDTO = UserAccessInfoResponseDTO.fromAccessInfo(activeInfo);
        } else {
            infoDTO = UserAccessInfoResponseDTO.noAccess();
        }

        // create new UserResponseDTO object to return
        UserResponseDTO userDTO = new UserResponseDTO()
            .setId(user.getId())
            .setFirstName(user.getFirstName())
            .setLastName(user.getLastName())
            .setEmail(user.getEmail())
            .setLob(user.getLob())
            .setApplications(applicationsLink)
            .setBookmarks(bookmarksLink)
            .setAccessInfo(infoDTO)
            .setBoss(user instanceof SuperUser);

        // create new staff/projects link when it is necessary
        if(user instanceof SuperUser) {
            SuperUser sUser = (SuperUser) user;

            CollectionLink staffLink = new CollectionLink();
            staffLink.setCount(sUser.getStaffMembers().size());
            staffLink.setPath(String.format("/users/%s/staff", sUser.getId()));
            userDTO.setStaff(staffLink);

            CollectionLink projectsLink = new CollectionLink();
            projectsLink.setCount(sUser.getCreatedProjects().size());
            projectsLink.setPath(String.format("/users/%s/projects", sUser.getId()));
            userDTO.setProjects(projectsLink);
        }

        return userDTO;
    }

    @Data
    public static class CollectionLink implements Serializable {

        private long count;

        private String path;

    }

}
