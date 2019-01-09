package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructureRepository;
import de.adesso.projectboard.base.user.persistence.structure.tree.TreeNode;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.ldap.service.LdapService;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    private final LdapService ldapService;

    private final OrganizationStructureRepository structureRepo;

    public LdapUserService(UserRepository userRepo,
                           UserDataRepository dataRepo,
                           LdapService ldapService,
                           OrganizationStructureRepository structureRepo) {
        this.userRepo = userRepo;
        this.dataRepo = dataRepo;
        this.structureRepo = structureRepo;
        this.ldapService = ldapService;
    }


    /**
     * {@inheritDoc}
     *
     * @see UserRepository#existsById(Object)
     */
    @Override
    public boolean userExists(String userId) {
        return userRepo.existsById(userId) || ldapService.userExists(userId);
    }

    /**
     * Returns {@code true} iff {@link AuthenticationInfoRetriever#hasAdminRole()} returns {@code true}
     * the persisted {@link OrganizationStructure}'s
     * {@link OrganizationStructure#getStaffMembers() staff members collection}
     * is <b>not empty</b> or the result of {@link LdapService#isManager(String)}
     * if none is present.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean userIsManager(User user) {
        if(structureRepo.existsByUser(user)) {
            return structureRepo.existsByUserAndManagingUser(user, true);
        } else {
            return ldapService.isManager(user.getId());
        }
    }

    /**
     * Returns the persisted {@link OrganizationStructure} instance
     * for the {@code user} or a new instance based on the
     * returned {@link StringStructure} by {@link LdapService#getIdStructure(User)}
     * after persisting it if none is present.
     *
     * {@inheritDoc}
     *
     * @see LdapService#getIdStructure(User)
     */
    @Override
    public OrganizationStructure getStructureForUser(User user) {
        // return the structure saved in the repo if one is present
        // or get the latest structure from the AD
        return structureRepo.findByUser(user).orElseGet(() -> {
            // make sure the given instance actually exists
            // otherwise the resulting LDAP query is empty
            validateExistence(user);

            StringStructure idStructure = ldapService.getIdStructure(user);

            // get the corresponding manager instance
            String managerId = idStructure.getManager();
            User manager = userRepo.findById(managerId).orElseGet(() ->
                userRepo.save(new User(managerId))
            );

            // get the corresponding staff member instances
            Set<User> staffMembers = idStructure.getStaffMembers()
                    .stream()
                    .map(userId ->
                        userRepo.findById(userId)
                                .orElseGet(() -> userRepo.save(new User(userId)))
                    )
                    .collect(Collectors.toSet());

            // a user is a manager when he has at least one staff member
            OrganizationStructure structure = new OrganizationStructure(user, manager, staffMembers, !staffMembers.isEmpty());

            // return the newly created, persisted instance
            return structureRepo.save(structure);
        });
    }

    /**
     * Returns the persisted {@link UserData} instance for the {@code user} iff
     * one is present. Returns the returned instance of {@link LdapService#getUserData(List)}
     * after persisting it.
     *
     * {@inheritDoc}
     *
     * @see LdapService#getUserData(List)
     */
    @Override
    public UserData getUserData(User user) {
        Optional<UserData> dataOptional = dataRepo.findByUser(user);

        // return the persisted instance if it is present
        // or retrieve the data and return it after persisting it
        return dataOptional.orElseGet(() -> {
            validateExistence(user);

            UserData data = ldapService.getUserData(Collections.singletonList(user)).get(0);

            return dataRepo.save(data);
        });
    }

    /**
     * Lazily initializes a {@link User} when there is no user with the
     * given {@code userId} present in the repository but a user exists
     * in the AD.
     * <p>
     * {@inheritDoc}
     *
     * @see LdapService#userExists(String)
     */
    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepo.findById(userId);

        return userOptional.orElseGet(() -> {
            if (ldapService.userExists(userId)) {
                return userRepo.save(new User(userId));
            }

            throw new UserNotFoundException();
        });
    }

    /**
     * {@inheritDoc}
     *
     * @see OrganizationStructureRepository#existsByUserAndStaffMembersContaining(User, User)
     */
    @Override
    public boolean userHasStaffMember(User user, User staffMember) {
        // direct staff member
        if(structureRepo.existsByUserAndStaffMembersContaining(user, staffMember)) {
            return true;
        }

        // get the struct of the user to make sure it is present
        // in the DB
        getStructureForUser(user);

        // walk up the user<->manager path for efficiency reasons
        // the user is a manager
        var currentManager = getStructureForUser(staffMember).getManager();
        var currentManagerStructure = getStructureForUser(currentManager);

        while(!currentManager.equals(user) && !currentManagerStructure.getManager().equals(currentManager)) {
            if(structureRepo.existsByUserAndStaffMembersContaining(user, currentManager)) {
                return true;
            }

            currentManager = currentManagerStructure.getManager();
            currentManagerStructure = getStructureForUser(currentManager);
        }

        return false;
    }

    /**
     * Returns the referenced manager of the persisted {@link OrganizationStructure}
     * for the {@link User} with the given {@code userId} in case it is present and
     * returns the manager with the ID of the structure returned by
     * {@link LdapService#getIdStructure(User)}.
     *
     * {@inheritDoc}
     */
    @Override
    public User getManagerOfUser(User user) {
        Optional<OrganizationStructure> structureOptional = structureRepo.findByUser(user);

        if (structureOptional.isPresent()) {
            return structureOptional.get().getManager();
        } else {
            validateExistence(user);

            String managerId = ldapService.getManagerId(user);

            return getUserById(managerId);
        }
    }

    @Override
    public TreeNode<UserData> getStaffMemberUserDataOfUser(User user, Sort sort) {
        var staffTreeNode = getStaffMembersOfUser(user);

        return new TreeNode<>(getUserData(user));
    }

    @Override
    public TreeNode<User> getStaffMembersOfUser(User user) {
        var rootNode = new TreeNode<User>(user);

        var structure = getStructureForUser(user);

        structure.getStaffMembers().stream()
                .map(this::getStaffMembersOfUser)
                .forEach(rootNode::addChild);

        return rootNode;
    }

    /**
     * {@inheritDoc}
     *
     * @see UserRepository#save(Object)
     */
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
        Map<User, Boolean> userManagerMap = new HashMap<>();

        if(Objects.requireNonNull(users).isEmpty()) {
            return userManagerMap;
        }

        // get the cached OrganizationStructure instance for every user
        // ! does NOT have to contain every instance !
        List<OrganizationStructure> existingStructures = structureRepo.findAllByUserIn(users);

        // add it to the map for every existing instance
        existingStructures.forEach(structure -> userManagerMap.put(structure.getUser(), structure.isManagingUser()));

        // remove all users that have a persisted structure
        List<User> usersWithStructs = users.stream()
                .filter(user -> existingStructures.stream()
                        .anyMatch(struct -> struct.getUser().equals(user))
                )
                .collect(Collectors.toList());
        users.removeAll(usersWithStructs);

        // call the ldap service method for every user that has no
        // persisted instance and add it to the map
        Map<User, Boolean> ldapMap = users.parallelStream()
                .collect(Collectors.toMap(user -> user, user -> ldapService.isManager(user.getId())));
        userManagerMap.putAll(ldapMap);

        return userManagerMap;
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
        String userId = Objects.requireNonNull(user).getId();

        if(!ldapService.userExists(userId)) {
            throw new UserNotFoundException("No user with the given ID exists in the AD!");
        }

        return user;
    }

}
