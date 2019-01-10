package de.adesso.projectboard.base.user.dto;

import de.adesso.projectboard.base.access.dto.AccessInfoResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The DTO of a {@link User} send back to the user.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDTO implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String lob;

    private AccessInfoResponseDTO accessInfo;

    private boolean isBoss;

    private String picture;

    private CollectionLink applications;

    private CollectionLink bookmarks;

    private CollectionLink projects;

    @Data
    public static class CollectionLink implements Serializable {

        private long count;

        public CollectionLink(long count) {
            this.count = count;
        }

    }

}
