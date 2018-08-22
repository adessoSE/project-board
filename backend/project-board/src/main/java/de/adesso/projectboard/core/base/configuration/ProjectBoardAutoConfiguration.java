package de.adesso.projectboard.core.base.configuration;

import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import de.adesso.projectboard.core.base.rest.ProjectApplicationHandler;
import de.adesso.projectboard.core.mail.MailService;
import de.adesso.projectboard.core.reader.JiraProjectReader;
import de.adesso.projectboard.core.reader.JiraProjectReaderConfigurationProperties;
import de.adesso.projectboard.core.rest.JiraProjectApplicationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectBoardAutoConfiguration {

    @Bean(name = "projectReaderBean")
    @ConditionalOnMissingBean(AbstractProjectReader.class)
    @Autowired
    public JiraProjectReader restReader(RestTemplateBuilder builder, JiraProjectReaderConfigurationProperties properties) {
        return new JiraProjectReader(builder, properties);
    }

    @Bean
    @ConditionalOnMissingBean(ProjectApplicationHandler.class)
    @Autowired
    public JiraProjectApplicationHandler applicationHandler(ProjectRepository repository, MailService mailService) {
        return new JiraProjectApplicationHandler(repository, mailService);
    }

}
