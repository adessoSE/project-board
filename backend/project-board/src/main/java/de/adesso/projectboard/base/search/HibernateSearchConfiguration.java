package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class HibernateSearchConfiguration {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Autowired
    @Bean
    public HibernateSearchService staffSearchService(ProjectBoardConfigurationProperties properties) {
        var lobDependentStatus = new HashSet<>(properties.getLobDependentStatus());
        var lobIndependentStatus = new HashSet<>(properties.getLobIndependentStatus());

        var searchService = new HibernateSearchService(lobDependentStatus, lobIndependentStatus);
        searchService.indexExistingEntities(entityManager);

        return searchService;
    }

    @Autowired
    @Bean
    public HibernateSearchService managerSearchService(ProjectBoardConfigurationProperties properties) {
        var lobIndependentStatus = Stream.concat(
                properties.getLobIndependentStatus().stream(),
                properties.getLobIndependentStatus().stream()
        ).collect(Collectors.toSet());

        return new HibernateSearchService(Set.of(), lobIndependentStatus);
    }

}
