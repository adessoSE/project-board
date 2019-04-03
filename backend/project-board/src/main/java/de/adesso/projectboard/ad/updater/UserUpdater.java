package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.service.LdapAdapter;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
public class UserUpdater {

    private final HierarchyTreeNodeRepository hierarchyTreeNodeRepo;

    private final RepositoryUserService repoUserService;

    private final UserDataRepository userDataRepo;

    private final LdapAdapter ldapAdapter;

    @Autowired
    public UserUpdater(HierarchyTreeNodeRepository hierarchyTreeNodeRepo,
                       RepositoryUserService repoUserService,
                       UserDataRepository userDataRepo,
                       LdapAdapter ldapAdapter) {
        this.hierarchyTreeNodeRepo = hierarchyTreeNodeRepo;
        this.repoUserService = repoUserService;
        this.userDataRepo = userDataRepo;
        this.ldapAdapter = ldapAdapter;
    }

    public void updateHierarchyAndUserData() {
        var ldapUserNodes = ldapAdapter.getAllUserNodes();
        var filteredAndCleanedNodes = cleanDirectReports(filterNodesWithMissingManager(ldapUserNodes));

        updateHierarchy(filteredAndCleanedNodes);
        updateUserData(filteredAndCleanedNodes);
    }

    void updateHierarchy(@NonNull Collection<LdapUserNode> nodes) {
        var rootNodes = getRootNodes(nodes);
        var hierarchyTreeNodeRoots = buildHierarchyTrees(rootNodes, nodes);

        hierarchyTreeNodeRepo.deleteAll();
        hierarchyTreeNodeRepo.flush();
        hierarchyTreeNodeRepo.saveAll(hierarchyTreeNodeRoots);
    }

    void updateUserData(@NonNull Collection<LdapUserNode> nodes) {
        var userData = nodes.stream()
                .map(node -> {
                    var user = repoUserService.getOrCreateUserById(node.getId());

                    return new UserData(user, node.getGivenName(), node.getSurname(), node.getMail(), node.getDivision());
                })
                .collect(Collectors.toSet());

        userDataRepo.deleteAll();
        userDataRepo.flush();
        userDataRepo.saveAll(userData);
    }

    /**
     * Returns all nodes whose manager DN is equal to the node's DN.
     *
     * @param nodes
     *          The nodes to get the root nodes for, not null.
     *
     * @return
     *          The root nodes.
     */
    Set<LdapUserNode> getRootNodes(@NonNull Collection<LdapUserNode> nodes) {
        return nodes.stream()
                .filter(ldapUserNode -> ldapUserNode.getDn().equals(ldapUserNode.getManagerDn()))
                .collect(Collectors.toSet());
    }

    /**
     *
     * @param rootNodes
     *          The root nodes to create hierarchy trees for, not null.
     *
     * @param allNodes
     *          All nodes that are needed to build the hierarchy trees, not null.
     *
     * @return
     *          A collection of {@link HierarchyTreeNode}s representing the roots of the
     *          created trees, one for each node in the given {@code rootNodes}.
     */
    Collection<HierarchyTreeNode> buildHierarchyTrees(@NonNull Collection<LdapUserNode> rootNodes, @NonNull Collection<LdapUserNode> allNodes) {
        var dnNodeMap = allNodes.stream()
                .collect(Collectors.toMap(LdapUserNode::getDn, Function.identity()));

        return rootNodes.stream()
                .map(rootNode -> {
                    var childNodesInLevelOrder = getChildNodesInLevelOrder(rootNode, dnNodeMap);

                    return buildHierarchyNode(rootNode, childNodesInLevelOrder);
                })
                .collect(Collectors.toList());
    }

    /**
     * Builds a {@link HierarchyTreeNode} for a given {@code rootNode} and
     * it's {@code childNodes} in level order. The {@link HierarchyTreeNode#getUser() user}
     * of the returned hierarchy node is set to a user found with the same ID as
     * the given {@code rootNode}.
     *
     * @param rootNode
     *          The node to build a hierarchy node for, not null.
     *
     * @param childNodes
     *          The child nodes of the user <b>in level order</b>, not null.
     *
     * @return
     *          A {@code HierarchyTreeNode}.
     *
     * @see #getChildNodesInLevelOrder(LdapUserNode, Map)
     */
    HierarchyTreeNode buildHierarchyNode(@NonNull LdapUserNode rootNode, @NonNull List<LdapUserNode> childNodes) {
        var rootNodeUser = repoUserService.getOrCreateUserById(rootNode.getId());
        var rootHierarchyNode = new HierarchyTreeNode(rootNodeUser);

        var dnHierarchyNodeMap = new HashMap<String, HierarchyTreeNode>();
        dnHierarchyNodeMap.put(rootNode.getDn(), rootHierarchyNode);

        childNodes.forEach(childNode -> {
            var userId = childNode.getId();
            var user = repoUserService.getOrCreateUserById(userId);
            var childHierarchyNode = new HierarchyTreeNode(user);

            dnHierarchyNodeMap.put(childNode.getDn(), childHierarchyNode);

            var managerHierarchy = dnHierarchyNodeMap.get(childNode.getManagerDn());
            if(Objects.isNull(managerHierarchy)) {
                throw new IllegalArgumentException("Child nodes not in level order!");
            }
            managerHierarchy.addDirectStaffMember(childHierarchyNode);
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
    List<LdapUserNode> getChildNodesInLevelOrder(@NonNull LdapUserNode node, @NonNull Map<String, LdapUserNode> dnNodeMap) {
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

    /**
     * Removes sub-trees of nodes which's root node references a manager node
     * which is not contained in the given {@code nodes}.
     *
     * @param nodes
     *          The nodes to filter, not null.
     *
     * @return
     *          A filtered set of nodes.
     */
    Set<LdapUserNode> filterNodesWithMissingManager(@NonNull Collection<LdapUserNode> nodes) {
        var dnNodeMap = nodes.stream()
                .collect(Collectors.toMap(LdapUserNode::getDn, Function.identity()));
        var nodesToFilter = new HashSet<LdapUserNode>();

        nodes.forEach(node -> {
                    if(!nodesToFilter.contains(node)) {
                        var managerDn = node.getManagerDn();

                        if(!dnNodeMap.containsKey(managerDn)) {
                            nodesToFilter.add(node);

                            if(!node.getDirectReportsDn().isEmpty()) {
                                nodesToFilter.addAll(getChildNodesInLevelOrder(node, dnNodeMap));
                            }
                        }
                    }
                });

        var filteredNodes = new HashSet<>(nodes);
        filteredNodes.removeAll(nodesToFilter);
        return filteredNodes;
    }

    /**
     * Removes a direct report DN from the direct reports in case the DN is equal
     * to the nodes DN (<i>circular reference</i>) or no node with that DN exists
     * in the given {@code nodes}.
     *
     * @param nodes
     *          The nodes to clean up the direct reports of, not null.
     *
     * @return
     *          A set of all nodes.
     */
    Set<LdapUserNode> cleanDirectReports(@NonNull Collection<LdapUserNode> nodes) {
        var nodeDns = nodes.stream()
                .map(LdapUserNode::getDn)
                .collect(Collectors.toList());

        return nodes.stream()
                .map(node -> {
                    var directReports = node.getDirectReportsDn();

                    var filteredDirectReports = directReports.parallelStream()
                            .filter(directReport -> !node.getDn().equals(directReport) && nodeDns.contains(directReport))
                            .collect(Collectors.toList());

                    return node.setDirectReportsDn(filteredDirectReports);
                }).collect(Collectors.toSet());
    }

}
