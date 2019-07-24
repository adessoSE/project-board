package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.Set;

@Configuration
public class HibernateSearchConfiguration {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Autowired
    @Bean
    public HibernateSearchService staffSearchService(ProjectBoardConfigurationProperties properties) {
        var lobDependentStatus = properties.getLobDependentStatus();
        var excludedStatus = properties.getStatusExcludedFromList();

        var searchService = new HibernateSearchService(lobDependentStatus, excludedStatus);
        searchService.indexExistingEntities(entityManager);

        return searchService;
    }

    @Autowired
    @Bean
    public HibernateSearchService managerSearchService(ProjectBoardConfigurationProperties properties) {
        var excludedStatus = properties.getStatusExcludedFromList();
        return new HibernateSearchService(Set.of(), excludedStatus);
    }

}
