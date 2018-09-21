package de.adesso.projectboard.core.base.rest.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoResponseDTO;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
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

    private UserAccessInfoResponseDTO accessInfo;

    private CollectionLink applications;

    private CollectionLink bookmarks;

    private CollectionLink staff;

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

        // create new bookmarks link to break circular reference to bookmarks
        // user -> bookmark -> user -> bookmark -> ....
        CollectionLink bookmarksLink = new CollectionLink();
        bookmarksLink.setCount(user.getBookmarks().size());
        bookmarksLink.setPath(String.format("/users/%s/bookmarks", user.getId()));

        // create a new access info dto to break circular reference
        // user -> accessInfo -> user
        UserAccessInfoResponseDTO infoDTO;
        if(user.hasAccess()) {
            UserAccessInfo activeInfo = user.getAccessObject();
            infoDTO = UserAccessInfoResponseDTO.fromAccessInfo(activeInfo);
        } else {
            infoDTO = UserAccessInfoResponseDTO.noAccess();
        }

        // create new UserResponseDTO object to return
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setLob(user.getLob());
        userDTO.setApplications(applicationsLink);
        userDTO.setBookmarks(bookmarksLink);
        userDTO.setAccessInfo(infoDTO);

        // create new staff link when it is necessary
        if(user instanceof SuperUser) {
            CollectionLink staffLink = new CollectionLink();
            staffLink.setCount(((SuperUser) user).getStaffMembers().size());
            staffLink.setPath(String.format("/users/%s/staff", user.getId()));

            userDTO.setStaff(staffLink);
        }

        return userDTO;
    }

    @Data
    public static class CollectionLink implements Serializable {

        private long count;

        private String path;

    }

}
