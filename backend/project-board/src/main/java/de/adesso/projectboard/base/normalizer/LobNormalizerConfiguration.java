package de.adesso.projectboard.base.normalizer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(LobNormalizerProperties.class)
public class LobNormalizerConfiguration {

    private final LobNormalizerProperties lobNormalizerProperties;

    //TODO:
    // add additional tests for RootTermDistanceCalculator
    // adapt HibernateSearchService
    // does the Lazy annotation work on bean methods with type Optional?
    @Bean
    @Lazy
    public ProjectLobNormalizer projectLobNormalizer(@Qualifier("lobRootTermDistanceCalculators") Set<RootTermDistanceCalculator> distanceCalculators) {
        return new ProjectLobNormalizer(distanceCalculators);
    }

    @Bean
    @Lazy
    public UserDataLobNormalizer userDataLobNormalizer(@Qualifier("lobRootTermDistanceCalculators") Set<RootTermDistanceCalculator> distanceCalculators) {
        return new UserDataLobNormalizer(distanceCalculators);
    }

    @Bean
    @Lazy
    public Set<RootTermDistanceCalculator> lobRootTermDistanceCalculators() {
       return lobNormalizerProperties.getNormalizerPairs()
                .parallelStream()
                .map(pair -> {
                    var rootName = pair.getRootName();
                    var derivedNames = new HashSet<>(pair.getDerivedNames());

                    return new RootTermDistanceCalculator(rootName, derivedNames);
                })
                .collect(Collectors.toSet());
    }

}
