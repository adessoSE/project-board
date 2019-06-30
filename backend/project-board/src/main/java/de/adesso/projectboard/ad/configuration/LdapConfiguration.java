package de.adesso.projectboard.ad.configuration;

import de.adesso.projectboard.ad.access.RepositoryUserAccessService;
import de.adesso.projectboard.ad.service.LdapAdapter;
import de.adesso.projectboard.ad.updater.UserUpdateJob;
import de.adesso.projectboard.ad.updater.UserUpdater;
import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.normalizer.Normalizer;
import de.adesso.projectboard.base.search.HibernateSearchService;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(
        prefix = "projectboard.ldap",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Configuration
@EnableConfigurationProperties(LdapConfigurationProperties.class)
public class LdapConfiguration {

    @Autowired
    @Bean
    public LdapTemplate ldapTemplate(ContextSource source) {
        return new LdapTemplate(source);
    }

    @Autowired
    @Bean
    public LdapAdapter ldapAdapter(LdapTemplate ldapTemplate, LdapConfigurationProperties ldapConfigProperties, Clock clock) {
        return new LdapAdapter(ldapTemplate, ldapConfigProperties, clock);
    }

    @Autowired
    @Bean
    public RepositoryUserService repositoryUserService(UserRepository userRepository, UserDataRepository userDataRepository,
                                                       LdapAdapter ldapAdapter, HierarchyTreeNodeRepository hierarchyTreeNodeRepo,
                                                       HibernateSearchService hibernateSearchService) {
        return new RepositoryUserService(userRepository, userDataRepository, ldapAdapter, hierarchyTreeNodeRepo, hibernateSearchService);
    }

    @Autowired
    @Bean
    public RepositoryUserAccessService repositoryUserAccessService(RepositoryUserService repositoryUserService, AccessIntervalRepository accessIntervalRepo,
                                                                   UserAccessEventHandler userAccessEventHandler, Clock clock) {
        return new RepositoryUserAccessService(repositoryUserService, accessIntervalRepo, userAccessEventHandler, clock);
    }

    @Autowired
    @Bean
    public UserUpdater userUpdater(HierarchyTreeNodeRepository hierarchyTreeNodeRepo, RepositoryUserService repositoryUserService,
                                   UserDataRepository userDataRepo, LdapAdapter ldapAdapter, @Lazy Optional<List<Normalizer<UserData>>> normalizers) {
        return new UserUpdater(hierarchyTreeNodeRepo, repositoryUserService, userDataRepo, ldapAdapter, normalizers.orElse(List.of()));
    }

    @Autowired
    @Bean
    public UserUpdateJob userUpdateJob(UserUpdater userUpdater, LdapConfigurationProperties ldapConfigProperties, Clock clock) {
        return new UserUpdateJob(userUpdater, ldapConfigProperties, clock);
    }

}
