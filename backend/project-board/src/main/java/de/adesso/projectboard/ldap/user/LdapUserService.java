package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.security.AuthenticationInfo;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructureRepository;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.base.util.Sorting;
import de.adesso.projectboard.ldap.service.LdapService;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link UserService} implementation that uses LDAP queries to retrieve
 * user data from a AD.
 *
 * @see LdapService
 * @see UserRepository
 * @see AuthenticationInfo
 */
@Profile("adesso-ad")
@Service
public class LdapUserService implements UserService {

    private final UserRepository userRepo;

    private final UserDataRepository dataRepo;

    private final OrganizationStructureRepository structureRepo;

    private final LdapService ldapService;

    private final AuthenticationInfo authInfo;

    public LdapUserService(UserRepository userRepo,
                           UserDataRepository dataRepo,
                           OrganizationStructureRepository structureRepo,
                           LdapService ldapService,
                           AuthenticationInfo authInfo) {
        this.userRepo = userRepo;
        this.dataRepo = dataRepo;
        this.structureRepo = structureRepo;
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
        return userRepo.existsById(userId) || ldapService.userExists(userId);
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The result of {@link LdapService#isManager(User)} with the
     *          corresponding {@link User} to the given {@code userId} as
     *          the argument when no {@link OrganizationStructure} instance
     *          is found for the user. {@code true} when one is present
     *          and the {@link OrganizationStructure#getStaffMembers() staff members}
     *          collection is empty.
     *
     * @see #getUserById(String)
     * @see LdapService#isManager(User)
     */
    @Override
    public boolean isManager(String userId) {
        User user = getUserById(userId);

        Optional<OrganizationStructure> structureOptional =
                structureRepo.findByUser(user);

        return structureOptional.map(organizationStructure -> {
                    return organizationStructure.getStaffMembers().isEmpty();
                })
                .orElseGet(() -> ldapService.isManager(user));
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The {@link OrganizationStructure} instance for the {@link User}
     *          with the given {@code userId}.
     *
     * @see #getUserById(String)
     * @see LdapService#getIdStructure(User)
     */
    @Override
    public OrganizationStructure getStructureForUser(String userId) throws UserNotFoundException {
        User user = getUserById(userId);

        // return the structure saved in the repo if one is present
        // or get the latest structure from the AD
        return structureRepo.findByUser(user).orElseGet(() -> {
            StringStructure idStructure = ldapService.getIdStructure(user);

            // get the corresponding User instances
            User manager = getUserById(idStructure.getManager());
            Set<User> staffMembers = idStructure.getStaffMembers()
                    .stream()
                    .map(this::getUserById)
                    .collect(Collectors.toSet());

            // return the persisted instance
            return structureRepo.save(new OrganizationStructure(user, manager, staffMembers));
        });
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The {@link UserData} instance for the user.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId}
     *          was found.
     *
     * @see #getUserById(String)
     * @see LdapService#getUserData(User)
     */
    @Override
    public UserData getUserData(String userId) throws UserNotFoundException {
        User user = getUserById(userId);

        Optional<UserData> dataOptional = dataRepo.findByUser(user);

        // return the persisted instance if it is present
        // or retrieve the data and return it after persisting it
        if(dataOptional.isPresent()) {
            return dataOptional.get();
        } else {
            UserData data = ldapService.getUserData(getUserById(userId));

            return dataRepo.save(data);
        }
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
        Optional<User> userOptional = userRepo.findById(userId);

        return userOptional.orElseGet(() -> {
           if(ldapService.userExists(userId)) {
               return userRepo.save(new User(userId));
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
     *
     * @see #getStructureForUser(String)
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
    public List<UserData> getStaffMemberDataOfUser(String userId, Sorting sorting) throws UserNotFoundException {
        OrganizationStructure structureForUser = getStructureForUser(userId);

        // assure that data of all users is present in the repo
        structureForUser
                .getStaffMembers()
                .forEach(user -> getUserData(user.getId()));

        return dataRepo.findAllByUser(structureForUser.getStaffMembers(), sorting.toSort());
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
        return userRepo.save(user);
    }

    @Override
    public void delete(User user) {
        // TODO: implement delete() in LdapUserService
    }

    @Override
    public void deleteUserById(String userId) throws UserNotFoundException {
        // intentionally left blank
    }

}