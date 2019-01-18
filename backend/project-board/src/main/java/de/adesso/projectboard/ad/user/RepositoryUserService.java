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
import lombok.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    public boolean userExists(@NonNull String userId) {
        return userRepo.existsById(userId);
    }

    @Override
    public boolean userIsManager(@NonNull User user) {
        return hierarchyTreeNodeRepo.existsByUserAndManagingUserTrue(user);
    }

    @Override
    public HierarchyTreeNode getHierarchyForUser(@NonNull User user) {
        return hierarchyTreeNodeRepo.findByUser(user)
                .orElseThrow(() -> new HierarchyNotFoundException(user.getId()));
    }

    @Override
    public UserData getUserData(@NonNull User user) {
        return dataRepo.findByUser(user)
                .orElseThrow(() -> new UserDataNotFoundException(user.getId()));
    }

    @Override
    public User getUserById(@NonNull String userId) throws UserNotFoundException {
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean userHasStaffMember(@NonNull User user, @NonNull User staffMember) {
        var staffMemberHierarchy = getHierarchyForUser(staffMember);
        return hierarchyTreeNodeRepo.existsByUserAndStaffContaining(user, staffMemberHierarchy);
    }

    @Override
    public User getManagerOfUser(@NonNull User user) {
        var hierarchy = getHierarchyForUser(user);
        if(hierarchy.isRoot()) {
            return hierarchy.getUser();
        }

        return hierarchy
                .getManager()
                .getUser();
    }

    @Override
    public List<UserData> getStaffMemberUserDataOfUser(@NonNull User user, @NonNull Sort sort) {
        var directStaff = getStaffMembersOfUser(user);
        var directStaffData = dataRepo.findByUserIn(directStaff, sort);

        if(directStaffData.size() != directStaff.size()) {
            throw new IllegalStateException("Data for some staff members is not present!");
        }

        return directStaffData;
    }

    @Override
    public List<User> getStaffMembersOfUser(@NonNull User user) {
        return getHierarchyForUser(user).stream()
                .map(HierarchyTreeNode::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public User save(@NonNull User user) {
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
    public Map<User, Boolean> usersAreManagers(@NonNull Set<User> users) {
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

    public User getOrCreateUserById(@NonNull String userId) {
        return userRepo.findById(userId)
                .orElseGet(() -> userRepo.save(new User(userId)));
    }

}
