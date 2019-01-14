package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.ldap.service.EagerLdapService;
import de.adesso.projectboard.ldap.service.LdapService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link UserService} implementation that uses LDAP queries to retrieve
 * user data from a AD.
 *
 * @see LdapService
 * @see UserRepository
 * @see AuthenticationInfoRetriever
 */
@Profile("adesso-ad")
@Service
@Transactional
public class LdapUserService implements UserService {

    private final UserRepository userRepo;

    private final UserDataRepository dataRepo;

    private final EagerLdapService ldapService;

    private final HierarchyTreeNodeRepository hierarchyTreeNodeRepo;

    public LdapUserService(UserRepository userRepo,
                           UserDataRepository dataRepo,
                           EagerLdapService ldapService,
                           HierarchyTreeNodeRepository hierarchyTreeNodeRepo) {
        this.userRepo = userRepo;
        this.dataRepo = dataRepo;
        this.hierarchyTreeNodeRepo = hierarchyTreeNodeRepo;
        this.ldapService = ldapService;
    }

    @Override
    public boolean userExists(String userId) {
        return true;
    }

    @Override
    public boolean userIsManager(User user) {
        return true;
    }

    @Override
    public HierarchyTreeNode getHierarchyForUser(User user) {
        return null;
    }

    @Override
    public UserData getUserData(User user) {
        return null;
    }

    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        ldapService.updateUserHierarchy();

        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean userHasStaffMember(User user, User staffMember) {
        var staffMemberHierarchy = getHierarchyForUser(staffMember);
        return hierarchyTreeNodeRepo.existsByUserAndStaffContaining(user, staffMemberHierarchy);
    }

    @Override
    public User getManagerOfUser(User user) {
        // TODO
        return null;
    }

    @Override
    public List<User> getStaffMemberUserDataOfUser(User user, Sort sort) {
        return null;
    }

    @Override
    public List<UserData> getStaffMembersOfUser(User user) {
        return null;
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public void delete(User user) {
        // intentionally left blank
    }

    @Override
    public void deleteUserById(String userId) {
        // intentionally left blank
    }

    @Override
    public Map<User, Boolean> usersAreManagers(Set<User> users) {
        // TODO
        return new HashMap<>();
    }

    /**
     *
     * @param user
     *          The {@link User} to validate.
     *
     * @return
     *          {@code true}, iff {@link LdapService#userExists(String)}
     *          with the {@link User#id ID} of the given {@code user}
     *          returns {@code true}.
     *
     * @throws UserNotFoundException
     *          When {@link LdapService#userExists(String)}
     *          with the {@link User#id ID} of the given {@code user}
     *          returns {@code false}.
     */
    @Override
    public User validateExistence(User user) throws UserNotFoundException {
        return user;
    }

}
