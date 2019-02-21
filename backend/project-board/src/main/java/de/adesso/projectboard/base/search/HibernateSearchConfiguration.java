package de.adesso.projectboard.base.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Configuration
public class HibernateSearchConfiguration {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Bean
    public HibernateSearchService hibernateSearchService() {
        var searchService = new HibernateSearchService();
        searchService.initialize(entityManager);

        return searchService;
    }

}
