package de.adesso.projectboard.base.user.dto;

import de.adesso.projectboard.base.access.dto.AccessInfoResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
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

    private String lob;

    private boolean isBoss;

    private AccessInfoResponseDTO accessInfo;

    private CollectionLink applications;

    private CollectionLink bookmarks;

    private CollectionLink projects;

    public static UserResponseDTO fromUserData(UserData userData, boolean isBoss) {
        User user = userData.getUser();

        CollectionLink applications = new CollectionLink(user.getApplications().size());
        CollectionLink bookmarks = new CollectionLink(user.getBookmarks().size());
        CollectionLink projects = new CollectionLink(user.getOwnedProjects().size());

        AccessInfoResponseDTO accessDTO;
        if(user.getLatestAccessInfo() != null) {
            accessDTO = AccessInfoResponseDTO.fromAccessInfo(user.getLatestAccessInfo());
        } else {
            accessDTO = AccessInfoResponseDTO.noAccess();
        }

        return new UserResponseDTO()
                .setId(user.getId())
                .setFirstName(userData.getFirstName())
                .setLastName(userData.getLastName())
                .setEmail(userData.getEmail())
                .setLob(userData.getLob())
                .setBoss(isBoss)
                .setAccessInfo(accessDTO)
                .setApplications(applications)
                .setBookmarks(bookmarks)
                .setProjects(projects);
    }

    @Data
    public static class CollectionLink implements Serializable {

        private long count;

        public CollectionLink(long count) {
            this.count = count;
        }

    }

}
