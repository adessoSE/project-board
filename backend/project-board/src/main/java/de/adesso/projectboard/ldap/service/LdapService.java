package de.adesso.projectboard.ldap.service;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.ldap.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ldap.service.util.DnStructureMapper;
import de.adesso.projectboard.ldap.service.util.UserDataMapper;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     *          is equal to the given {@code userId} and the AD entry's {@code accountExpires} attribute
     *          is in the future is <b>not empty</b>.
     *
     * @see LdapConfigurationProperties#getLdapBase()
     * @see LdapConfigurationProperties#getUserIdAttribute()
     */
    public boolean userExists(String userId) {
        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes(idAttribute)
                .where("objectClass").is("person")
                .and("accountExpires").gte(get100NanosSince1601())
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
                .attributes(idAttribute)
                .where("objectClass").is("person")
                .and("directReports").isPresent()
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
    public List<UserData> getUserData(List<User> users) throws IllegalArgumentException {
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
                .attributes(idAttribute, "name", "givenName", "userPrincipalName", "mail", "division", "objectClass", "thumbnailPhoto")
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
     *          {@link LdapConfigurationProperties#getUserIdAttribute() user ID attribute} values
     *          of the users.
     *
     * @see DnStructureMapper
     */
    public StringStructure getIdStructure(User user) {
        LdapQuery query = query()
                .base(base)
                .attributes("manager", "distinguishedName")
                .where("distinguishedName").isPresent()
                .and(idAttribute).is(user.getId());

        List<StringStructure> userResultList = ldapTemplate.search(query, new DnStructureMapper(user));

        validateQueryResult(userResultList, 1);
        StringStructure dnStructure = userResultList.get(0);

        // get the staff member IDs
        String userDN = dnStructure.getUser();
        Set<String> staffMemberIDs = getStaffMemberIDsByManagerDN(userDN);

        // get the manager ID
        String managerDN = dnStructure.getManager();
        String managerID = getUserIdByDN(managerDN);

        return new StringStructure(user, user.getId(), managerID, staffMemberIDs);
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
     * @see #getUserIdByDN(String)
     */
    public String getManagerId(User user) {
        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes("manager")
                .where("objectClass").is("person")
                .and(idAttribute).isPresent()
                .and(idAttribute).is(user.getId());

        List<String> resultList = ldapTemplate.search(query, (AttributesMapper<String>) attributes -> {
            return (String) attributes.get("manager").get();
        });

        validateQueryResult(resultList, 1);

        return getUserIdByDN(resultList.get(0));
    }

    /**
     *
     * @param managerDN
     *          The <i>distinguished name</i> of the manager to
     *          search the staff members for.
     *
     * @return
     *          A {@link Set} of the values of the
     *          {@link LdapConfigurationProperties#getUserIdAttribute() configured ID attribute}
     *          of all users where the {@code manager} attribute is equal to the given {@code managerDN}.
     *
     * @see LdapConfigurationProperties#getUserIdAttribute()
     */
    Set<String> getStaffMemberIDsByManagerDN(String managerDN) {
        if(Objects.requireNonNull(managerDN).isEmpty()) {
            throw new IllegalArgumentException("DN can't be empty!");
        }

        LdapQuery query = query()
                .base(this.base)
                .attributes(idAttribute)
                .where("manager").is(managerDN)
                .and("accountExpires").gte(get100NanosSince1601())
                .and(idAttribute).isPresent();

        List<String> staffMemberIDs = ldapTemplate.search(query, (AttributesMapper<String>) attributes -> {
            return (String) attributes.get(idAttribute).get();
        });

        return new HashSet<>(staffMemberIDs);
    }

    /**
     *
     * @param DN
     *          The <i>distinguished name</i> to get the ID of the
     *          user by.
     *
     * @return
     *          The value of the {@link LdapConfigurationProperties#getUserIdAttribute() configured ID attribute}
     *          of the user where the <i>distinguished name</i> attribute is equal to the given {@code DN}.
     *
     * @throws IllegalArgumentException
     *          When a empty {@code DN} was passed.
     *
     * @see LdapConfigurationProperties#getUserIdAttribute()
     */
    String getUserIdByDN(String DN) throws IllegalArgumentException {
        if(Objects.requireNonNull(DN).isEmpty()) {
            throw new IllegalArgumentException("DN can't be empty!");
        }

        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes(idAttribute)
                .where("distinguishedName").is(DN)
                .and(idAttribute).isPresent();

        // get the query result and get the ID attribute
        List<String> resultList = ldapTemplate.search(query, (AttributesMapper<String>) attributes -> {
            return (String) attributes.get(idAttribute).get();
        });

        validateQueryResult(resultList, 1);

        return resultList.get(0);
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

    /**
     *
     * @return
     *          The count of passed {@link 100ns} since {@code 01.01.1601}
     *          of the current time. Required to search
     *          for dates.
     */
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
