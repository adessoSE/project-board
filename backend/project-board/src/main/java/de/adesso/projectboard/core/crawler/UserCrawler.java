package de.adesso.projectboard.core.crawler;

import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.crawler.configuration.CrawlerConfigurationProperties;
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

    private final CrawlerConfigurationProperties properties;

    @Autowired
    public UserCrawler(UserRepository userRepository,
                       LdapTemplate ldapTemplate,
                       CrawlerConfigurationProperties properties) {
        this.userRepository = userRepository;
        this.ldapTemplate = ldapTemplate;
        this.properties = properties;
    }

    // executed every day at 4am
    @Scheduled(cron = "0 4 * * * *")
    public void crawlUsers() {
        HashSet<TreeNode<LdapUser>> userTreeRoots = getUserTreeRoots();

        userTreeRoots.forEach(root -> {
            // maps dn -> SuperUser
            Map<String, SuperUser> dnSuperUserMap = new HashMap<>();

            root.forEach(node -> {
                if(node.isRoot()) {
                    // create a superuser instance for the tree
                    // root node
                    LdapUser rootUser = root.getContent();

                    SuperUser rootSuperUser = new SuperUser(rootUser.getSAMAccountName());
                    rootSuperUser.setFirstName(rootUser.getFirstName())
                            .setLastName(rootUser.getLastName())
                            .setEmail(rootUser.getMail())
                            .setLob(rootUser.getDivision());

                    // store in map
                    dnSuperUserMap.put(rootUser.getDistinguishedName(), rootSuperUser);

                    userRepository.save(rootSuperUser);
                } else {
                    LdapUser nodeUser = node.getContent();
                    String userId = nodeUser.getSAMAccountName();
                    String firstName = nodeUser.getFirstName();
                    String lastName = nodeUser.getLastName();
                    String email = nodeUser.getMail();
                    String lob = nodeUser.getDivision();

                    // map contains key because the child is returned
                    // after the parent (RootFirstTreeIterator)
                    SuperUser boss = dnSuperUserMap.get(nodeUser.getManagerDN());

                    User newUser;

                    if(node.isLeaf()) {
                        newUser = new User(userId, boss);
                    } else {
                        newUser = new SuperUser(userId, boss);

                        dnSuperUserMap.put(nodeUser.getDistinguishedName(), (SuperUser) newUser);
                    }

                    newUser.setFullName(firstName, lastName)
                            .setEmail(email)
                            .setLob(lob);

                    userRepository.save(newUser);
                }
            });
        });
    }

    /**
     *
     * @return
     *          The result of {@link #buildTree(Collection)} with
     *          {@link LdapUser}s retrieved from the AD.
     *
     * @see #buildTree(Collection)
     * @see #createUserQueryByBase(String)
     */
    private HashSet<TreeNode<LdapUser>> getUserTreeRoots() {
        LdapQuery query = createUserQueryByBase(properties.getCrawlBase());

        List<LdapUser> result = ldapTemplate.search(query, new LdapUserMapper());

        return buildTree(result);
    }

    /**
     *
     * @param users
     *          The initial {@link LdapUser}s to build the trees
     *          from.
     *
     * @return
     *          The result of {@link #buildTree(Collection, Map)}.
     *
     * @see #buildTree(Collection, Map)
     */
    private HashSet<TreeNode<LdapUser>> buildTree(Collection<LdapUser> users) {
        // generate a TreeNode for each user
        Set<TreeNode<LdapUser>> nodeSet = users.stream()
                .map(TreeNode::new)
                .collect(Collectors.toSet());

        // add each node to the map
        HashMap<String, TreeNode<LdapUser>> dnNodeMap = new HashMap<>();
        nodeSet.forEach(node -> {
            String key = node.getContent().getDistinguishedName();

            dnNodeMap.put(key, node);
        });

        return buildTree(nodeSet, dnNodeMap);
    }

    /**
     * This method builds
     *
     * @param nodeSet
     *          The set of {@link TreeNode}s to build the trees from.
     *
     * @param dnNodeMap
     *          The map to cache {@link TreeNode}s of already
     *          retrieved users.
     *
     * @return
     *          A set of {@link TreeNode} roots.
     *
     * @see #buildTree(Collection)
     */
    private HashSet<TreeNode<LdapUser>> buildTree(Collection<TreeNode<LdapUser>> nodeSet, Map<String, TreeNode<LdapUser>> dnNodeMap) {
        Set<TreeNode<LdapUser>> highestLevelNodes = new HashSet<>();

        nodeSet.forEach(node -> {
            String managerDN = node.getContent().getManagerDN();

            if(dnNodeMap.containsKey(managerDN)) {
                // get the node from the map when it's present
                TreeNode<LdapUser> managerNode = dnNodeMap.get(managerDN);

                // set the managerDN node as the parent when it's not equal
                // to the node (avoid circular reference)
                if(!node.equals(managerNode)) {
                    node.setParent(managerNode);

                    highestLevelNodes.add(managerNode);
                } else {
                    // when it's equal to the node then the user is it's
                    // own boss and the highest level is reached
                    highestLevelNodes.add(node);
                }
            } else {
                LdapUser ldapUser = getUserByDN(managerDN);
                TreeNode<LdapUser> managerNode = new TreeNode<>(ldapUser);

                // Cache the result in the map to improve performance
                dnNodeMap.put(managerDN, managerNode);

                // add it to the highest level nodes
                highestLevelNodes.add(managerNode);

                // set it as the parent
                node.setParent(managerNode);
            }
        });

        // stop if nothing changed
        if(highestLevelNodes.equals(nodeSet)) {
            return new HashSet<>(highestLevelNodes);
        } else {
            return buildTree(highestLevelNodes, dnNodeMap);
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
     *          name, mail, division, managerDN</i> and <i>objectClass</i>, all guaranteed to
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
                .attributes("sAMAccountName", "name", "givenName", "mail", "division", "manager", "objectClass", "distinguishedName")
                .where("sAMAccountName").isPresent()
                .and("name").isPresent()
                .and("givenName").isPresent()
                .and("mail").isPresent()
                .and("division").isPresent()
                .and("manager").isPresent()
                .and("distinguishedName").isPresent()
                .and("objectClass").is("person");
    }

}
