package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.service.LdapService;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Profile("adesso-ad")
@Service
@Transactional
public class UserUpdater {

    private final HierarchyTreeNodeRepository hierarchyTreeNodeRepo;

    private final RepositoryUserService repoUserService;

    private final UserDataRepository userDataRepo;

    private final LdapService ldapService;

    @Autowired
    public UserUpdater(HierarchyTreeNodeRepository hierarchyTreeNodeRepo,
                       RepositoryUserService repoUserService,
                       UserDataRepository userDataRepo,
                       LdapService ldapService) {
        this.hierarchyTreeNodeRepo = hierarchyTreeNodeRepo;
        this.repoUserService = repoUserService;
        this.userDataRepo = userDataRepo;
        this.ldapService = ldapService;
    }

    public void updateHierarchyAndUserData() {
        var ldapUserNodes = ldapService.getAllUserNodes();

        updateHierarchy(ldapUserNodes);
        updateUserData(ldapUserNodes);
    }

    void updateHierarchy(Collection<LdapUserNode> nodes) {
        var rootManagers = Objects.requireNonNull(nodes).stream()
                .filter(ldapUserNode -> ldapUserNode.getDn().equals(ldapUserNode.getManagerDn()))
                .collect(Collectors.toList());

        hierarchyTreeNodeRepo.deleteAllInBatch();
        hierarchyTreeNodeRepo.saveAll(buildHierarchyNodes(rootManagers, nodes));
    }

    void updateUserData(Collection<LdapUserNode> nodes) {
        var userData = Objects.requireNonNull(nodes).stream()
                .map(node -> {
                    var user = repoUserService.getOrCreateUserById(node.getId());

                    return new UserData(user, node.getGivenName(), node.getSurname(), node.getMail(), node.getDepartment());
                })
                .collect(Collectors.toList());

        userDataRepo.deleteAllInBatch();
        userDataRepo.saveAll(userData);
    }



    /**
     *
     * @param rootNodes
     *          The root nodes to create hierarchy nodes for, not null.
     *
     * @param allNodes
     *          All nodes that are needed to build the hierarchy nodes, not null.
     *
     * @return
     *          A collection of {@link HierarchyTreeNode}, one for each node
     *          in the given {@code rootNodes}.
     */
    Collection<HierarchyTreeNode> buildHierarchyNodes(Collection<LdapUserNode> rootNodes, Collection<LdapUserNode> allNodes) {
        Objects.requireNonNull(rootNodes);
        Objects.requireNonNull(allNodes);

        var dnNodeMap = new HashMap<String, LdapUserNode>();
        allNodes.forEach(node -> dnNodeMap.put(node.getDn(), node));

        return rootNodes.stream()
                .map(rootNode -> {
                    var childNodesInLevelOrder = getChildNodesInLevelOrder(rootNode, dnNodeMap);

                    return buildHierarchyNode(rootNode, childNodesInLevelOrder);
                })
                .collect(Collectors.toList());
    }

    /**
     * Builds a {@link HierarchyTreeNode} for a given {@code rootNode} and
     * it's {@code childNodes} in level order. The {@link HierarchyTreeNode#user user}
     * of the returned hierarchy node is set to a user found with the same ID as
     * the given {@code rootNode}.
     *
     * @param rootNode
     *          The node to build a hierarchy node for, not null.
     *
     * @param childNodes
     *          The child nodes of the user in <b>level order</b>, not null.
     *
     * @return
     *          A {@link HierarchyTreeNode}.
     */
    HierarchyTreeNode buildHierarchyNode(LdapUserNode rootNode, List<LdapUserNode> childNodes) {
        var rootNodeUser = repoUserService.getOrCreateUserById(rootNode.getId());
        var rootHierarchyNode = new HierarchyTreeNode(rootNodeUser);

        var dnHierarchyNodeMap = new HashMap<String, HierarchyTreeNode>();
        dnHierarchyNodeMap.put(rootNode.getDn(), rootHierarchyNode);

        childNodes.forEach(childNode -> {
            var userId = childNode.getId();
            var user = repoUserService.getOrCreateUserById(userId);

            var childHierarchyNode = new HierarchyTreeNode(user);

            dnHierarchyNodeMap.put(childNode.getDn(), childHierarchyNode);
            dnHierarchyNodeMap.get(childNode.getManagerDn()).addDirectStaffMember(childHierarchyNode);
        });

        return rootHierarchyNode;
    }

    /**
     * Returns the child nodes of a given {@code node} in level order. When iterating
     * the returned list the parent node of a returned node will always be returned
     * first. The returned list does not contain the given {@code node} though.
     * <br>
     * Example:
     * <pre>
     *         A
     *       /  \
     *      B   C
     *      |
     *      D
     * </pre>
     * When node <i>A</i> is passed, the list contains the following elements in exactly
     * this order: <i>B, C, D</i>
     *
     * @param node
     *          The node to get the child nodes of, not null.
     *
     * @param dnNodeMap
     *          A map to map a node's DN to the node itself, not null.
     *
     * @return
     *          The child nodes in level order.
     */
    List<LdapUserNode> getChildNodesInLevelOrder(LdapUserNode node, Map<String, LdapUserNode> dnNodeMap) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(dnNodeMap);

        var childNodesInLevelOrder = new ArrayList<LdapUserNode>();
        var childDnDeque = new LinkedList<>(node.getDirectReportsDn());

        while(!childDnDeque.isEmpty()) {
            var childDn = childDnDeque.removeFirst();

            if(!dnNodeMap.containsKey(childDn)) {
                var message = String.format("Child node with DN '%s' not found!", childDn);

                throw new IllegalArgumentException(message);
            }

            var childNode = dnNodeMap.get(childDn);
            childDnDeque.addAll(childNode.getDirectReportsDn());
            childNodesInLevelOrder.add(childNode);
        }

        return childNodesInLevelOrder;
    }

}
