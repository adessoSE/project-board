package de.adesso.projectboard.ad.user;

import de.adesso.projectboard.base.exceptions.HierarchyNotFoundException;
import de.adesso.projectboard.base.exceptions.UserDataNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Profile("adesso-ad")
@Service
@Transactional
public class RepositoryUserService implements UserService {

    private final UserRepository userRepo;

    private final UserDataRepository dataRepo;

    private final HierarchyTreeNodeRepository hierarchyTreeNodeRepo;

    public RepositoryUserService(UserRepository userRepo,
                                 UserDataRepository dataRepo,
                                 HierarchyTreeNodeRepository hierarchyTreeNodeRepo) {
        this.userRepo = userRepo;
        this.dataRepo = dataRepo;
        this.hierarchyTreeNodeRepo = hierarchyTreeNodeRepo;
    }

    @Override
    public boolean userExists(String userId) {
        return userRepo.existsById(userId);
    }

    @Override
    public boolean userIsManager(User user) {
        return hierarchyTreeNodeRepo.existsByUserAndManagingUserTrue(user);
    }

    @Override
    public HierarchyTreeNode getHierarchyForUser(User user) {
        return hierarchyTreeNodeRepo.findByUser(user)
                .orElseThrow(() -> new HierarchyNotFoundException(user.getId()));
    }

    @Override
    public UserData getUserData(User user) {
        return dataRepo.findByUser(user)
                .orElseThrow(() -> new UserDataNotFoundException(user.getId()));
    }

    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean userHasStaffMember(User user, User staffMember) {
        var staffMemberHierarchy = getHierarchyForUser(staffMember);
        return hierarchyTreeNodeRepo.existsByUserAndStaffContaining(user, staffMemberHierarchy);
    }

    @Override
    public User getManagerOfUser(User user) {
        var hierarchy = getHierarchyForUser(user);
        if(hierarchy.isRoot()) {
            return hierarchy.getUser();
        }

        return hierarchy
                .getManager()
                .getUser();
    }

    @Override
    public List<UserData> getStaffMemberUserDataOfUser(User user, Sort sort) {
        var directStaff = getStaffMembersOfUser(user);
        var directStaffData = dataRepo.findByUserIn(directStaff, sort);

        if(directStaffData.size() != directStaff.size()) {
            throw new IllegalStateException("Data for some staff members is not present!");
        }

        return directStaffData;
    }

    @Override
    public List<User> getStaffMembersOfUser(User user) {
        return getHierarchyForUser(user).stream()
                .map(HierarchyTreeNode::getUser)
                .collect(Collectors.toList());
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
        Objects.requireNonNull(users);

        var userManagerMap = hierarchyTreeNodeRepo.findByUserIn(users).stream()
                .collect(Collectors.toMap(
                        HierarchyTreeNode::getUser,
                        HierarchyTreeNode::isManagingUser
                ));

        if(userManagerMap.keySet().size() != users.size()) {
            throw new IllegalStateException("Hierarchy for some users not present!");
        }

        return userManagerMap;
    }

    public User getOrCreateUserById(String userId) {
        Objects.requireNonNull(userId);

        return userRepo.findById(userId)
                .orElseGet(() -> userRepo.save(new User(userId)));
    }

}
