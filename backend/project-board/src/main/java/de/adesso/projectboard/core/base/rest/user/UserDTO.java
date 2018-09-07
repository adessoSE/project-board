package de.adesso.projectboard.core.base.rest.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO {

    private String id;

    private PropertyLink applications;

    private PropertyLink bookmarks;

    @Data
    public static class PropertyLink implements Serializable {

        private long count;

        private String path;

    }

}
