package de.adesso.projectboard.base.search;

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

    @Bean
    public HibernateSearchService staffSearchService() {
        var searchService = new HibernateSearchService(Set.of("offen", "open"), Set.of("eskaliert", "escalated"));
        searchService.indexExistingEntities(entityManager);

        return searchService;
    }

    @Bean
    public HibernateSearchService managerSearchService() {
        return new HibernateSearchService(Set.of(), Set.of("offen", "open", "eskaliert", "escalated"));
    }

}
