package de.adesso.projectboard.ldap.access;

import de.adesso.projectboard.base.access.dto.UserAccessInfoRequestDTO;
import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link Service} to to provide functionality to manage {@link AccessInfo}.
 *
 * @see UserAccessController
 */
@Service
public class UserAccessServiceImpl {

    private final LdapUserService userService;

    @Autowired
    public UserAccessServiceImpl(LdapUserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to remove access from.
     *
     * @return
     *          The saved {@link User}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} is
     *          found.
     *
     */
    public User deleteAccessForUser(String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        user.removeAccess();

        return userService.save(user);
    }

    /**
     *
     * @param infoDTO
     *          The {@link UserAccessInfoRequestDTO} object.
     *
     * @param userId
     *          The id of the {@link User} to give access to.
     *
     * @return
     *          The updated {@link User}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    public User createAccessForUser(UserAccessInfoRequestDTO infoDTO, String userId) throws UserNotFoundException {
        User userToGiveAccess = userService.getUserById(userId);
        userToGiveAccess.giveAccessUntil(infoDTO.getAccessEnd());

        // save the updated user
        return userService.save(userToGiveAccess);
    }

}
