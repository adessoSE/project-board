package de.adesso.projectboard.base.normalizer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "projectboard.lob-normalizer")
public class LobNormalizerProperties {

    private List<NormalizerPair> normalizerPairs = new ArrayList<>();

    @Data
    public static class NormalizerPair {

        private String rootName;

        private List<String> derivedNames = new ArrayList<>();

    }

}
