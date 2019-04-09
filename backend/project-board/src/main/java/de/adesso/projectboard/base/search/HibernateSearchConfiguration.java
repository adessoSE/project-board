package de.adesso.projectboard.base.search;

import org.springframework.beans.factory.annotation.Autowired;
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
    public HibernateSimpleQueryUtils simpleQueryEnhancer() {
        return new HibernateSimpleQueryUtils();
    }

    @Autowired
    @Bean
    public HibernateSearchService hibernateSearchService(HibernateSimpleQueryUtils hibernateSimpleQueryUtils) {
        var searchService = new HibernateSearchService(hibernateSimpleQueryUtils);
        searchService.initialize(entityManager);

        return searchService;
    }

}
