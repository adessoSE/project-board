package de.adesso.projectboard.base.user.dto;

import de.adesso.projectboard.base.access.dto.AccessInfoResponseDTO;
import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory to create DTOs for {@link User}s.
 */
@Component
public class UserDtoFactory {

    private final UserService userService;

    @Autowired
    public UserDtoFactory(UserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param user
     *          The {@link User} to create a DTO for.
     *
     * @return
     *          The created {@link UserResponseDTO DTO}.
     */
    public UserResponseDTO createDto(User user) {
        UserData userData = userService.getUserData(user.getId());
        boolean isManager = userService.userIsManager(user.getId());
        AccessInfo latestInfo = user.getLatestAccessInfo();

        AccessInfoResponseDTO infoDTO = new AccessInfoResponseDTO();
        if(latestInfo != null) {
            infoDTO.setHasAccess(latestInfo.isCurrentlyActive())
                    .setAccessStart(latestInfo.getAccessStart())
                    .setAccessEnd(latestInfo.getAccessEnd());
        }

        return new UserResponseDTO()
                .setAccessInfo(infoDTO)
                .setApplications(new UserResponseDTO.CollectionLink(user.getApplications().size()))
                .setBookmarks(new UserResponseDTO.CollectionLink(user.getBookmarks().size()))
                .setProjects(new UserResponseDTO.CollectionLink(user.getOwnedProjects().size()))
                .setId(user.getId())
                .setBoss(isManager)
                .setEmail(userData.getEmail())
                .setFirstName(userData.getFirstName())
                .setLastName(userData.getLastName())
                .setLob(userData.getLob());
    }

}
