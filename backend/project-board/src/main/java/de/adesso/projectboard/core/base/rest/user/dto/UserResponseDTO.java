package de.adesso.projectboard.core.base.rest.user.dto;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoResponseDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * The DTO of a {@link User} send back to the user.
 */
@Data
public class UserResponseDTO implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private UserAccessInfoResponseDTO accessInfo;

    private SetLink applications;

    private SetLink bookmarks;

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
        SetLink applicationsLink = new SetLink();
        applicationsLink.setCount(user.getApplications().size());
        applicationsLink.setPath(String.format("/users/%s/applications", user.getId()));

        // create new bookmarks link to break circular reference to bookmarks
        // user -> bookmark -> user -> bookmark -> ....
        SetLink bookmarksLink = new SetLink();
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
        userDTO.setApplications(applicationsLink);
        userDTO.setBookmarks(bookmarksLink);
        userDTO.setAccessInfo(infoDTO);

        return userDTO;
    }

    @Data
    public static class SetLink implements Serializable {

        private long count;

        private String path;

    }

}
