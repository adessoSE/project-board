package de.adesso.projectboard.ldap.service;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.ldap.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ldap.service.util.StructureMapper;
import de.adesso.projectboard.ldap.service.util.UserDataMapper;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Profile("adesso-ad")
@Service
public class LdapService {

    private final LdapTemplate ldapTemplate;

    private final String base;

    private final String idAttribute;

    @Autowired
    public LdapService(LdapTemplate ldapTemplate, LdapConfigurationProperties ldapProperties) {
        this.ldapTemplate = ldapTemplate;

        this.base = ldapProperties.getLdapBase();
        this.idAttribute = ldapProperties.getUserIdAttribute();
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          {@code true}, iff the LDAP query searching for
     *          a <i>person</i> at the configured {@link LdapConfigurationProperties#getLdapBase() base}
     *          where the configured {@link LdapConfigurationProperties#getUserIdAttribute() user ID attribute}
     *          is equal to the given {@code userId} is <b>not empty</b>.
     *
     * @see LdapConfigurationProperties#getLdapBase()
     * @see LdapConfigurationProperties#getUserIdAttribute()
     */
    public boolean userExists(String userId) {
        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes(idAttribute, "objectClass")
                .where("objectClass").is("person")
                .and(idAttribute).isPresent()
                .and(idAttribute).is(userId);

        List<String> resultList = ldapTemplate.search(query, (AttributesMapper<String>) attributes -> {
            return (String) attributes.get(idAttribute).get();
        });

        return !resultList.isEmpty();
    }

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          {@code true}, iff the LDAP query searching for
     *          a <i>person</i> at the configured {@link LdapConfigurationProperties#getLdapBase() base},
     *          where the configured {@link LdapConfigurationProperties#getUserIdAttribute() user ID attribute}
     *          is equal to the given {@code userId} and the <i>directReports</i> attribute is
     *          present, is <i>not empty</i>.
     */
    public boolean isManager(String userId) {
        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes(idAttribute, "objectClass", "directReports")
                .where("objectClass").is("person")
                .and("directReports").isPresent()
                .and(idAttribute).isPresent()
                .and(idAttribute).is(userId);

        return !ldapTemplate.search(query, (AttributesMapper<String>) attributes -> (String) attributes.get(idAttribute).get()).isEmpty();
    }

    /**
     *
     * @param users
     *          The {@link User}s to get the data for.
     *
     * @return
     *          The {@link UserData} instances for the given {@code users}.
     *
     * @throws IllegalStateException
     *          When the query results length the given {@code users} collection
     *          length.
     *
     * @throws IllegalArgumentException
     *          When a empty {@code users} collection was passed.
     */
    public List<UserData> getUserData(List<User> users) throws IllegalStateException {
        if(Objects.requireNonNull(users).isEmpty()) {
            throw new IllegalArgumentException("Users collection can not be empty!");
        }

        // get IDs
        List<String> userIds = users
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // build a sub criteria to search for every
        // user ID
        ContainerCriteria idCriteria = null;
        for(String userId : userIds) {
            if(idCriteria == null) {
                idCriteria = query()
                        .where(idAttribute)
                        .is(userId);
            } else {
                idCriteria.or(idAttribute).is(userId);
            }
        }

        // query for every user
        LdapQuery query = query()
                .base(base)
                .countLimit(users.size())
                .attributes(idAttribute, "name", "givenName", "userPrincipalName", "mail", "division", "objectClass")
                .where("name").isPresent()
                .and("givenName").isPresent()
                .and("division").isPresent()
                .and("objectClass").is("person")
                .and(idAttribute).isPresent()
                .and(idCriteria);

        List<UserData> userDataList = ldapTemplate.search(query, new UserDataMapper(users, idAttribute));

        validateQueryResult(userDataList, users.size());

        return userDataList;
    }

    /**
     *
     * @param user
     *          The {@link User}
     *
     * @return
     *          A {@link StringStructure} instance, where the {@link StringStructure#staffMembers staff members}
     *          and {@link StringStructure#manager manager} fields contain the configured
     *          {@link LdapConfigurationProperties#getUserIdAttribute() user ID attribute} value.
     *
     * @see StructureMapper
     */
    public StringStructure getIdStructure(User user) {
        LdapQuery query = query()
                .base(base)
                .attributes(idAttribute, "objectClass", "manager", "directReports")
                .where("objectClass").is("person")
                .and(idAttribute).isPresent()
                .and(idAttribute).is(user.getId());

        List<StringStructure> resultList = ldapTemplate.search(query, new StructureMapper(user));

        validateQueryResult(resultList, 1);

        // get the IDs of the user for the corresponding distinguished name
        StringStructure dnStructure = resultList.get(0);

        // get the staff member IDs
        Set<String> staffMemberDns = dnStructure.getStaffMembers();
        Set<String> staffMemberIDs = new HashSet<>(getUserIdsByDN(staffMemberDns));

        // get the manager ID separately
        String managerDN = dnStructure.getManager();
        String managerID = getUserIdsByDN(Collections.singleton(managerDN)).get(0);

        return new StringStructure(user, managerID, staffMemberIDs);
    }

    /**
     *
     * @param user
     *          The {@link User} to get the manager's {@link User#id ID}
     *          of.
     *
     * @return
     *          The manager's ID.
     *
     * @see #getUserIdsByDN(Collection)
     */
    public String getManagerId(User user) {
        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes(idAttribute, "objectClass", "manager")
                .where("objectClass").is("person")
                .and(idAttribute).isPresent()
                .and(idAttribute).is(user.getId());

        List<String> managerList = ldapTemplate.search(query, (AttributesMapper<String>) attributes -> {
            return (String) attributes.get("manager").get();
        });

        validateQueryResult(managerList, 1);

        return getUserIdsByDN(managerList).get(0);
    }

    /**
     *
     * @param dns
     *          The distinguished names of the users.
     *
     * @return
     *          A list of the configured users' {@link LdapConfigurationProperties#userIdAttribute id Attribute}
     *          for every given DN.
     */
    List<String> getUserIdsByDN(Collection<String> dns) {
        if(dns.isEmpty()) {
            return Collections.emptyList();
        }

        // build a criteria for all DNs
        ContainerCriteria dnCriteria = null;
        for(String dn : dns) {
            if(dnCriteria == null) {
                dnCriteria = query()
                        .where("distinguishedName")
                        .is(dn);
            } else {
                dnCriteria
                        .or("distinguishedName")
                        .is(dn);
            }
        }

        // build the main query
        LdapQuery query = query()
                .countLimit(dns.size())
                .base(base)
                .attributes(idAttribute)
                .where("distinguishedName").isPresent()
                .and(idAttribute).isPresent()
                .and(dnCriteria);

        // get the query result and get the id attribute
        List<String> userIds = ldapTemplate.search(query, (AttributesMapper<String>) attributes -> {
            return (String) attributes.get(idAttribute).get();
        });

        validateQueryResult(userIds, dns.size());

        return userIds;
    }

    /**
     *
     * @param resultList
     *          The result list to verify.
     *
     * @param expectedSize
     *          The expected result size.
     *
     * @param <T>
     *          The type of the list.
     *
     * @throws IllegalStateException
     *          When the given {@code resultList}'s {@link List#size()}
     *          differs from the given {@code expectedSize}.
     */
    <T> List<T> validateQueryResult(List<T> resultList, int expectedSize) throws IllegalStateException {
        if(Objects.requireNonNull(resultList).size() != expectedSize) {
            throw new IllegalStateException("Illegal result count: " + resultList.size());
        }

        return resultList;
    }

}
