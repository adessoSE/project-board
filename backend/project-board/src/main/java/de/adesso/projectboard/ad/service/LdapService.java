package de.adesso.projectboard.ad.service;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ad.service.mapper.LdapUserNodeMapper;
import de.adesso.projectboard.ad.service.mapper.ThumbnailPhotoMapper;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import org.bouncycastle.util.Arrays;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapService {

    private static final String[] USER_NODE_ATTRIBUTES = new String[] {"name", "givenName", "mail", "manager", "sn", "department", "division", "directReports", "distinguishedName"};

    private final LdapTemplate ldapTemplate;

    private final String base;

    private final String idAttribute;

    public LdapService(LdapTemplate ldapTemplate, LdapConfigurationProperties properties) {
        this.ldapTemplate = ldapTemplate;

        this.base = properties.getLdapBase();
        this.idAttribute = properties.getUserIdAttribute();
    }

    /**
     * Creates a LDAP query searching for users whose account is not
     * expired and contains an <i>employeeId</i> and <i>manager</i>
     * attribute. The query result is filtered via {@link #filterNodes(Collection)}.
     *
     * @return
     *          All user nodes matching query after filtering.
     */
    public Collection<LdapUserNode> getAllUserNodes() {
        String[] adAttributes = Arrays.append(USER_NODE_ATTRIBUTES, idAttribute);

        var query = query()
                .base(base)
                .attributes(adAttributes)
                .where("accountExpires").gte(getCurrentTimeInADFormat())
                .and("employeeId").isPresent()
                .and("manager").isPresent()
                .and("mail").isPresent();

        var unfilteredNodes = ldapTemplate.search(query, new LdapUserNodeMapper(idAttribute));
        return filterNodes(unfilteredNodes);
    }

    /**
     * Retrieves the thumbnail photo for a given list of {@code userId}. The returned map
     * may not contain every user ID.
     *
     * @param userIds
     *          The user IDs to get the thumbnail photos for, not null.
     *
     * @return
     *          A map that maps a user ID to a thumbnail photo.
     */
    public Map<String, byte[]> getThumbnailPhotos(List<String> userIds) {
        Objects.requireNonNull(userIds);

        if(userIds.isEmpty()) {
            return new HashMap<>();
        }

        var query = query()
                .base(base)
                .attributes(idAttribute, "thumbnailPhoto")
                .countLimit(userIds.size())
                .where(idAttribute).isPresent()
                .and(buildIdSubQuery(userIds));

        var mapEntries = ldapTemplate.search(query, new ThumbnailPhotoMapper(idAttribute));

        return mapEntries.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Removes all nodes from the given {@code nodes} that have
     * a manager DN referencing a node that is not contained
     * inside the given {@code nodes} collection.
     * <br/>
     * Also removes all distinguished names from the direct reports
     * when the distinguished name references a node that is not present
     * in the given {@code nodes} or the distinguished name is equal
     * to the node's own distinguished name.
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

    /**
     * Builds a sub-query to query multiple user IDs at once.
     *
     * @param userIds
     *          The IDs to build the sub-query from, not null and not empty.
     *
     * @return
     *          The sub-query.
     */
    ContainerCriteria buildIdSubQuery(List<String> userIds) {
        Objects.requireNonNull(userIds);

        if(userIds.isEmpty()) {
            throw new IllegalArgumentException("No user ID found!");
        }

        var subQuery = query()
                .where(idAttribute).is(userIds.get(0));
        for(int i = 1; i < userIds.size(); i++) {
            subQuery
                    .or(idAttribute).is(userIds.get(i));
        }

        return subQuery;
    }

    /**
     *
     * @return
     *          The time passed since 01.01.1601 in 100 nanosecond
     *          intervals.
     */
    String getCurrentTimeInADFormat() {
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
