package de.adesso.projectboard.core.configuration;

import de.adesso.projectboard.core.base.project.AbstractProjectRepository;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectBoardAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AbstractProjectRepository.class)
    public AbstractProjectRepository projectRepository() {
        return null;
    }

    @Bean
    @ConditionalOnMissingBean(AbstractProjectReader.class)
    public AbstractProjectReader restReader() {
        return null;
    }

}
