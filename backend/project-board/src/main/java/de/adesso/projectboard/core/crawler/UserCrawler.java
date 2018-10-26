package de.adesso.projectboard.core.crawler;

import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.crawler.util.LdapUser;
import de.adesso.projectboard.core.crawler.util.LdapUserMapper;
import de.adesso.projectboard.core.crawler.util.tree.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Profile("adesso-ad")
@Service
public class UserCrawler {

    private final UserRepository userRepository;

    private final LdapTemplate ldapTemplate;

    @Autowired
    public UserCrawler(UserRepository userRepository, LdapTemplate ldapTemplate) {
        this.userRepository = userRepository;
        this.ldapTemplate = ldapTemplate;
    }

    // executed every day at 4am
    @Scheduled(cron = "0 4 * * * *")
    public void crawlUsers() {
        LdapQuery query = query()
                .base("OU=_Users,OU=adesso_Deutschland,DC=adesso,DC=local")
                .attributes("sAMAccountName", "name", "mail", "division", "manager", "objectClass")
                .where("sAMAccountName").isPresent()
                .and("name").isPresent()
                .and("mail").isPresent()
                .and("division").isPresent()
                .and("manager").isPresent()
                .and("objectClass").is("person")
                .and("division").is("LOB CROSS INDUSTRIES (CI)");

        List<LdapUser> result = ldapTemplate.search(query, new LdapUserMapper());

        Set<TreeNode<LdapUser>> initialNodeList = result.stream()
                .map(TreeNode::new)
                .collect(Collectors.toSet());

        HashSet<TreeNode<LdapUser>> treeNodes = buildTree(initialNodeList, new HashMap<>());

        System.out.println(result);
    }

    // TODO: only return a set of tree roots
    // TODO: prevent circular reference between nodes (itself as child not allowed) -> StackOverFlow in RootFirstTreeIterator
    private HashSet<TreeNode<LdapUser>> buildTree(Collection<TreeNode<LdapUser>> nodeSet, Map<String, TreeNode<LdapUser>> managerNodeMap) {
        Set<TreeNode<LdapUser>> newlyAddedNodes = new HashSet<>();

        nodeSet.forEach(node -> {
            String managerDN = node.getContent().getManager();

            if(managerNodeMap.containsKey(managerDN)) {
                // just get the node from the map when it's present
                node.setParent(managerNodeMap.get(managerDN));
            } else {
                LdapUser ldapUser = getUserByDN(managerDN);
                TreeNode<LdapUser> managerNode = new TreeNode<>(ldapUser);

                // Cache the result in the map to improve performance
                managerNodeMap.put(managerDN, managerNode);

                // add it to the newly added nodes
                newlyAddedNodes.add(managerNode);

                // set it as the parent
                node.setParent(managerNode);
            }
        });

        if(newlyAddedNodes.size() == 0) {
            return new HashSet<>(managerNodeMap.values());
        } else {
            return buildTree(newlyAddedNodes, managerNodeMap);
        }
    }

    /**
     *
     * @param dn
     *          The <i>distinguished name</i> of the user.
     *
     * @return
     *          The {@link LdapUser} representing the AD user.
     *
     * @throws IllegalArgumentException
     *          When no user was found or more than one user were found.
     */
    private LdapUser getUserByDN(String dn) throws IllegalArgumentException {
        LdapQuery query = createUserQueryByBase(dn);
        List<LdapUser> result = ldapTemplate.search(query, new LdapUserMapper());

        if(result.size() == 1) {
            return result.get(0);
        } else {
            throw new IllegalArgumentException("Result size was " + result.size());
        }
    }

    /**
     *
     * @param base
     *          The base of the {@link LdapQuery}.
     *
     * @return
     *          The {@link LdapQuery} to query the attributes <i>sAMAccountName,
     *          name, mail, division, manager</i> and <i>objectClass</i>, all guaranteed to
     *          be present.
     *
     * @throws IllegalArgumentException
     *          When the given {@code base} is {@code null} or
     *          {@link String#isEmpty() empty}.
     */
    private LdapQuery createUserQueryByBase(String base) throws IllegalArgumentException {
        if(base == null || base.isEmpty()) {
            throw new IllegalArgumentException("base can't be null or empty!");
        }

        return query()
                .base(base)
                .attributes("sAMAccountName", "name", "mail", "division", "manager", "objectClass")
                .where("sAMAccountName").isPresent()
                .and("name").isPresent()
                .and("mail").isPresent()
                .and("division").isPresent()
                .and("manager").isPresent()
                .and("objectClass").is("person");
    }

}
