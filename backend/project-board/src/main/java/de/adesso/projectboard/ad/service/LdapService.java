package de.adesso.projectboard.ad.service;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ad.service.mapper.LdapUserNodeMapper;
import de.adesso.projectboard.ad.service.mapper.ThumbnailPhotoMapper;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapService {

    private static final String[] USER_NODE_ATTRIBUTES = new String[] {"name", "givenName", "mail", "manager", "sn", "department", "division", "directReports", "distinguishedName"};

    private static final long EPOCH_OFFSET = 116_444_736_000_000_000L;

    private final LdapTemplate ldapTemplate;

    private final Clock clock;

    private final String base;

    private final String idAttribute;

    @Autowired
    public LdapService(LdapTemplate ldapTemplate, LdapConfigurationProperties properties, Clock clock) {
        this.ldapTemplate = ldapTemplate;
        this.clock = clock;

        this.base = properties.getLdapBase();
        this.idAttribute = properties.getUserIdAttribute();
    }

    /**
     * Creates a LDAP query searching for users whose account is not
     * expired and contains an <i>employeeId, mail</i> and <i>manager</i>
     * attribute.
     * <p>
     *      <b>Note:</b> A node's manager DN attribute or direct reports DN may refer to a node
     *      which is not present!
     * </p>
     *
     * @return
     *          All user nodes matching the query.
     */
    public Collection<LdapUserNode> getAllUserNodes() {
        String[] adAttributes = Arrays.append(USER_NODE_ATTRIBUTES, idAttribute);

        var adTimestamp = getActiveDirectoryTimestamp(LocalDateTime.now(clock));
        var query = query()
                .base(base)
                .attributes(adAttributes)
                .where("accountExpires").gte(adTimestamp)
                .and("employeeId").isPresent()
                .and("manager").isPresent()
                .and("mail").isPresent();

        return ldapTemplate.search(query, new LdapUserNodeMapper(idAttribute));
    }

    /**
     * Retrieves the thumbnail photo for a given list of {@code userId}. The returned map
     * may not contain every user ID in case no user was found with the given ID or no
     * thumbnail photo is present.
     *
     * @param userIds
     *          The user IDs to get the thumbnail photos for, not null.
     *
     * @return
     *          A map that maps a user ID to a thumbnail photo.
     */
    public Map<String, byte[]> getThumbnailPhotos(@NonNull Collection<String> userIds) {
        if(userIds.isEmpty()) {
            return new HashMap<>();
        }

        var query = query()
                .base(base)
                .attributes(idAttribute, "thumbnailPhoto")
                .countLimit(userIds.size())
                .where(idAttribute).isPresent()
                .and("thumbnailPhoto").isPresent()
                .and(buildIdCriteria(userIds));

        var mapEntries = ldapTemplate.search(query, new ThumbnailPhotoMapper(idAttribute));

        return mapEntries.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Builds a sub-query to query multiple user IDs at once.
     *
     * @param userIds
     *          The IDs to build the sub-query from, not null and not empty.
     *
     * @return
     *          A sub-query to query multiple IDs at once.
     */
    ContainerCriteria buildIdCriteria(@NotNull Collection<String> userIds) {
        Assert.notEmpty(userIds, "List must contain at least one User ID!");
        var userIdList = new ArrayList<>(userIds);

        var subQuery = query()
                .where(idAttribute).is(userIdList.get(0));
        userIdList.subList(1, userIds.size())
                .forEach(userId -> subQuery.or(idAttribute).is(userId));

        return subQuery;
    }

    /**
     *
     * @param dateTime
     *          The {@code LocalDateTime} to create the AD timestamp for, not null.
     *
     * @return
     *          The time passed between 01.01.1601 and the given {@code dateTime}
     *          (UTC) in 100 nanosecond intervals.
     */
    String getActiveDirectoryTimestamp(@NonNull LocalDateTime dateTime) {
        var epochSec = dateTime.toEpochSecond(ZoneOffset.UTC);
        var epochNanos = epochSec * 1_000L * 10_000L;

        return Long.toString(epochNanos + EPOCH_OFFSET);
    }

}
