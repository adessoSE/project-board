package de.adesso.projectboard.adapter.mail.configuration;

import de.adesso.projectboard.adapter.mail.MailSenderAdapter;
import de.adesso.projectboard.adapter.mail.handler.MailProjectApplicationReceivedEventHandler;
import de.adesso.projectboard.adapter.mail.handler.MailUserAccessEventHandler;
import de.adesso.projectboard.adapter.mail.persistence.MessageRepository;
import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.application.handler.ProjectApplicationReceivedEventHandler;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.Clock;
import java.util.Properties;

@ConditionalOnProperty(
        prefix = "projectboard.mail",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Configuration
@EnableConfigurationProperties(MailConfigurationProperties.class)
class MailConfiguration {

    @Autowired
    @Bean
    public JavaMailSender javaMailSenderImpl(MailConfigurationProperties mailConfigurationProperties) {
        var mailProperties = new Properties();
        mailProperties.putAll(mailConfigurationProperties.getProperties());

        var mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(mailConfigurationProperties.getUsername());
        mailSender.setPassword(mailConfigurationProperties.getPassword());
        mailSender.setPort(mailConfigurationProperties.getPort());
        mailSender.setHost(mailConfigurationProperties.getHost());
        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    @Autowired
    @Bean
    public MailSenderAdapter mailSenderAdapter(MessageRepository messageRepository, JavaMailSender javaMailSender,
                                               UserService userService, Clock clock, MailConfigurationProperties properties) {
        return new MailSenderAdapter(messageRepository, javaMailSender, userService, properties, clock);
    }

    @Autowired
    @Bean
    public ProjectApplicationReceivedEventHandler mailProjectApplicationHandler(MailSenderAdapter mailSenderAdapter, UserService userService, VelocityTemplateService velocityMailTemplateService,
                                                                                MailConfigurationProperties mailConfigProperties) {
        return new MailProjectApplicationReceivedEventHandler(mailSenderAdapter, userService, velocityMailTemplateService, mailConfigProperties);
    }

    @Autowired
    @Bean
    public UserAccessEventHandler mailUserAccessEventHandler(MailSenderAdapter mailSenderAdapter, UserService userService, VelocityTemplateService velocityMailTemplateService,
                                                             ProjectBoardConfigurationProperties configurationProperties) {
        return new MailUserAccessEventHandler(mailSenderAdapter, userService, velocityMailTemplateService, configurationProperties);
    }

}
