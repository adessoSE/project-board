package de.adesso.projectboard.base.normalizer;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(LobNormalizerProperties.class)
public class LobNormalizerConfiguration {

    private final LobNormalizerProperties lobNormalizerProperties;

    //TODO:
    // refactor to separate method
    // Update tests for UserUpdater/ProjectUpdater
    // Add test for LobNormalizer
    // add additional tests for RootTermDistanceCalculator
    @Bean
    public ProjectLobNormalizer projectLobNormalizer() {
        var calculators = lobNormalizerProperties.getNormalizerPairs()
                .parallelStream()
                .map(pair -> new RootTermDistanceCalculator(pair.getRootName(), new HashSet<>(pair.getDerivedNames())))
                .collect(Collectors.toSet());

        return new ProjectLobNormalizer(calculators);
    }

    @Bean
    public UserDataLobNormalizer userDataLobNormalizer() {
        var calculators = lobNormalizerProperties.getNormalizerPairs()
                .parallelStream()
                .map(pair -> new RootTermDistanceCalculator(pair.getRootName(), new HashSet<>(pair.getDerivedNames())))
                .collect(Collectors.toSet());

        return new UserDataLobNormalizer(calculators);
    }

}
