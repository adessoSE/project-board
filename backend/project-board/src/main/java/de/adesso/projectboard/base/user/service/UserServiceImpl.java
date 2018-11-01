package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.security.AuthenticationInfo;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure;
import de.adesso.projectboard.base.util.Sort;
import de.adesso.projectboard.user.service.LdapService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} to to provide functionality to manage {@link User}s.
 *
 * @see UserRepository
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final LdapService ldapService;

    private final AuthenticationInfo authInfo;

    public UserServiceImpl(UserRepository userRepository,
                           LdapService ldapService,
                           AuthenticationInfo authInfo) {
        this.userRepository = userRepository;
        this.ldapService = ldapService;
        this.authInfo = authInfo;
    }

    /**
     *
     * @return
     *          The result of {@link #getUserById(String)} with the ID returned
     *          by {@link AuthenticationInfo#getUserId()} as the argument.
     */
    @Override
    public User getAuthenticatedUser() throws UserNotFoundException {
        return getUserById(authInfo.getUserId());
    }

    /**
     *
     * @return
     *          The result of {@link AuthenticationInfo#getUserId()}.
     */
    @Override
    public String getAuthenticatedUserId() {
        return authInfo.getUserId();
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          {@code true}, iff {@link UserRepository#existsById(Object)} or
     *          {@link LdapService#userExists(String)} returns {@code true}.
     */
    @Override
    public boolean userExists(String userId) {
        return userRepository.existsById(userId) || ldapService.userExists(userId);
    }

    @Override
    public boolean isManager(String userId) {
        return false;
    }

    @Override
    public OrganizationStructure getStructureForUser(String userId) throws UserNotFoundException {
        return null;
    }

    @Override
    public UserData getUserData(String userId) throws UserNotFoundException {
        return null;
    }

    /**
     * Lazily initializes a {@link User} when there is no user with the
     * given {@code userId} present in the repository but a user exists
     * in the AD.
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The {@link User} with the corresponding {@code userId}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was
     *          found in the repository and there is no user with the {@code userId}
     *          present in the AD.
     *
     * @see LdapService#userExists(String)
     */
    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);

        return userOptional.orElseGet(() -> {
           if(ldapService.userExists(userId)) {
               return userRepository.save(new User(userId));
           }

           throw new UserNotFoundException();
        });
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to check.
     *
     * @param staffId
     *          The {@link User#id ID} of the staff member.
     *
     * @return
     *          {@code true}, iff the {@link #userExists(String) user exists}
     *          and the {@link #getStructureForUser(String) organizational structure}
     *          of the user contains a user with the given {@code staffId}.
     */
    @Override
    public boolean userHasStaffMember(String userId, String staffId) {
        if(userExists(userId)) {
            return getStructureForUser(userId)
                    .getStaffMembers()
                    .stream()
                    .anyMatch(user -> user.getId().equals(userId));
        }

        return false;
    }

    @Override
    public List<User> getStaffMembersOfUser(String userId, List<Sort> sorts) throws UserNotFoundException {
        List<User> staffMembers = getStructureForUser(userId).getStaffMembers();

        staffMembers.sort(Sort.toComparator(User.class, sorts));

        return staffMembers;
    }

    /**
     *
     * @param user
     *          The {@link User} to save.
     *
     * @return
     *          The result of {@link UserRepository#save(Object)}.
     */
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {

    }

}
