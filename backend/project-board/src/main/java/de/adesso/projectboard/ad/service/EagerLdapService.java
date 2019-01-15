package de.adesso.projectboard.ad.service;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ad.service.mapper.LdapUserNodeMapper;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class EagerLdapService {

    private static final String[] USER_NODE_ATTRIBUTES = new String[] {"name", "givenName", "userPrincipalName", "manager", "sn", "department", "division", "directReports", "distinguishedName"};

    private final HierarchyTreeNodeRepository hierarchyTreeNodeRepo;

    private final LdapTemplate ldapTemplate;

    private final String base;

    private final String idAttribute;

    @Autowired
    public EagerLdapService(LdapTemplate ldapTemplate, HierarchyTreeNodeRepository hierarchyTreeNodeRepo, LdapConfigurationProperties ldapProperties) {
        this.ldapTemplate = ldapTemplate;
        this.hierarchyTreeNodeRepo = hierarchyTreeNodeRepo;

        this.base = ldapProperties.getLdapBase();
        this.idAttribute = ldapProperties.getUserIdAttribute();
    }

    Collection<LdapUserNode> getAllUserNodes() {
        String[] adAttributes = Arrays.append(USER_NODE_ATTRIBUTES, idAttribute);

        LdapQuery query = query()
                .base(base)
                .attributes(adAttributes)
                .where("accountExpires").gte(get100NanosSince1601())
                .and("employeeId").isPresent()
                .and("manager").isPresent();

        var unfilteredNodes = ldapTemplate.search(query, new LdapUserNodeMapper(idAttribute));
        return filterNodes(unfilteredNodes);
    }

    public void updateUserHierarchy() {
        var ldapUserNodes = getAllUserNodes();

        var ownManagers = ldapUserNodes.stream()
                .filter(ldapUserNode -> ldapUserNode.getDn().equals(ldapUserNode.getManagerDn()))
                .collect(Collectors.toList());

        hierarchyTreeNodeRepo.saveAll(buildHierarchyNodes(ownManagers, ldapUserNodes));
    }

    public void updateUserData() {
        var ldapUserNodes = getAllUserNodes();

        System.out.println("test");
    }

    /**
     * Removes all nodes from the given {@code nodes} collection that have
     * a manager DN referencing a node that is not contained
     * inside the given {@code nodes} collection.
     * <br/>
     * Also removes all distinguished names from the direct reports
     * when the distinguished name references a node that is not present
     * in the given {@code nodes} or the distinguished name is equal
     * to the node's distinguished name.
     *
     * @param nodes
     *          The nodes to filter, not null.
     *
     * @return
     *          A filtered collection of nodes.
     */
    Collection<LdapUserNode> filterNodes(Collection<LdapUserNode> nodes) {
        Objects.requireNonNull(nodes);

        var allDistinguishedNames = nodes.stream()
                .map(LdapUserNode::getDn)
                .collect(Collectors.toList());

        return nodes.stream()
                .filter(node -> allDistinguishedNames.contains(node.getManagerDn()))
                .map(node -> {
                    var directReports = node.getDirectReportsDn();

                    var filteredDirectReports = directReports.stream()
                            .filter(directReport -> !node.getDn().equals(directReport))
                            .filter(allDistinguishedNames::contains)
                            .collect(Collectors.toList());

                    return node.setDirectReportsDn(filteredDirectReports);
                }).collect(Collectors.toList());
    }

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

    HierarchyTreeNode buildHierarchyNode(LdapUserNode rootNode, List<LdapUserNode> childNodes) {
        var dnHierarchyNodeMap = new HashMap<String, HierarchyTreeNode>();

        var rootHierarchyNode = new HierarchyTreeNode(new User(rootNode.getId()));
        dnHierarchyNodeMap.put(rootNode.getDn(), rootHierarchyNode);

        childNodes.forEach(childNode -> {
            // TODO: use user service to create a new user if not present
            var user = new User(childNode.getId());

            var childHierarchyNode = new HierarchyTreeNode(user);
            dnHierarchyNodeMap.put(childNode.getDn(), childHierarchyNode);

            dnHierarchyNodeMap.get(childNode.getManagerDn()).addDirectStaffMember(childHierarchyNode);
        });

        return rootHierarchyNode;
    }

    List<LdapUserNode> getChildNodesInLevelOrder(LdapUserNode node, Map<String, LdapUserNode> dnNodeMap) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(dnNodeMap);

        var childNodesInLevelOrder = new ArrayList<LdapUserNode>();
        var childDnDeque = new LinkedList<>(node.getDirectReportsDn());

        while(!childDnDeque.isEmpty()) {
            var childDn = childDnDeque.removeFirst();
            var childNode = dnNodeMap.get(childDn);

            childDnDeque.addAll(childNode.getDirectReportsDn());

            childNodesInLevelOrder.add(childNode);
        }

        return childNodesInLevelOrder;
    }

    String get100NanosSince1601() {
        // the offset (in 100ns) between 01.01.1601 and 01.01.1970
        BigInteger offSetTo1970 = new BigInteger("116444736000000000");

        // the passed time in ms between 01.01.1970 and now
        String millisSince1970 = Long.toString(System.currentTimeMillis());

        // multiply by 10.000 (-> append 4 zeros) to get to 100ns interval
        BigInteger current100NanosSince1970 = new BigInteger(millisSince1970 + "0000");

        // add the offset and return it as a string
        return current100NanosSince1970.add(offSetTo1970).toString();
    }

}
