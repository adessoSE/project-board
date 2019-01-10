package de.adesso.projectboard.base.user.dto;

import de.adesso.projectboard.base.access.dto.AccessInfoResponseDTO;
import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory to create DTOs for {@link User}s.
 */
@Component
@Transactional
public class UserDtoFactory {

    private final UserService userService;

    private final UserAccessService userAccessService;

    @Autowired
    public UserDtoFactory(UserService userService, UserAccessService userAccessService) {
        this.userService = userService;
        this.userAccessService = userAccessService;
    }

    /**
     * Method to create a DTO for a single User. Please use the
     * {@link #createDtos(Collection, boolean)} method when creating
     * DTOs for multiple users instead of calling the method for
     * each user.
     *
     * @param user
     *          The {@link User} to create a DTO for.
     *
     * @param includePicture
     *          Whether the picture should be included.
     *
     * @return
     *          The created {@link UserResponseDTO DTO}.
     */
    public UserResponseDTO createDto(User user, boolean includePicture) {
        UserData userData = userService.getUserData(user);
        boolean isManager = userService.userIsManager(user);

        return getDto(userData, isManager, includePicture);
    }

    /**
     * Method to create a DTO for a single User. Please use the
     * {@link #createDtos(Collection, boolean)}  method when creating
     * DTOs for multiple users instead of calling the method for
     * each user.
     *
     * @param userData
     *          The {@link UserData} instance to create a DTO from.
     *
     * @param includePicture
     *          Whether the picture should be included.
     *
     * @return
     *          The created {@link UserResponseDTO DTO}.
     */
    public UserResponseDTO createDto(UserData userData, boolean includePicture) {
        boolean isManager = userService.userIsManager(userData.getUser());

        return getDto(userData, isManager, includePicture);
    }

    /**
     * Method to create DTOs for multiple Users at once.
     *
     * @param userData
     *          The {@link UserData} instances to create DTOs from
     *
     * @param includePicture
     *          Whether the picture should be included.
     *
     * @return
     *          A {@link Set} of {@link UserResponseDTO DTOs}.
     */
    public Set<UserResponseDTO> createDtos(Collection<UserData> userData, boolean includePicture) {
        Set<User> users = userData.parallelStream()
                .map(UserData::getUser)
                .collect(Collectors.toSet());

        Map<User, Boolean> userManagerMap = userService.usersAreManagers(users);

        return userData.parallelStream()
                .map(data -> {
                    boolean isManager = userManagerMap.get(data.getUser());

                    return getDto(data, isManager, includePicture);
                })
                .collect(Collectors.toSet());
    }

    UserResponseDTO getDto(UserData userData, boolean isManager, boolean includePicture) {
        User user = userData.getUser();

        AccessInfoResponseDTO infoDTO = new AccessInfoResponseDTO();
        if(userAccessService.userHasActiveAccessInfo(user)) {
            AccessInfo latestInfo = user.getLatestAccessInfo()
                    .orElseThrow(() -> new IllegalStateException("No info instance present!"));

            infoDTO.setHasAccess(true)
                    .setAccessStart(latestInfo.getAccessStart())
                    .setAccessEnd(latestInfo.getAccessEnd());
        }

        String pictureBase64 = null;
        if(includePicture) {
            pictureBase64 = getBase64Picture(userData);
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
                .setLob(userData.getLob())
                .setPicture(pictureBase64);
    }

    String getBase64Picture(UserData userData) {
        String pictureBase64 = null;
        if(!Objects.isNull(userData.getPicture())) {
            pictureBase64 = Base64.getEncoder().encodeToString(userData.getPicture());
        }

        return pictureBase64;
    }

}
