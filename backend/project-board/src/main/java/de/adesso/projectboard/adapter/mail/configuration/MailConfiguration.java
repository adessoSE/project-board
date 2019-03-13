package de.adesso.projectboard.adapter.mail.configuration;

import de.adesso.projectboard.adapter.mail.MailSenderAdapter;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.handler.MailProjectApplicationEventHandler;
import de.adesso.projectboard.adapter.mail.handler.MailUserAccessEventHandler;
import de.adesso.projectboard.adapter.mail.persistence.MessageRepository;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.user.service.UserService;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
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

    @Bean
    public VelocityEngine velocityEngine() {
        // set the classpath resource loader as the default velocity resource loader
        var properties = new Properties();
        properties.put(RuntimeConstants.INPUT_ENCODING, StandardCharsets.UTF_8.name());
        properties.put(RuntimeConstants.ENCODING_DEFAULT, StandardCharsets.UTF_8.name());
        properties.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        properties.put("classpath.resource.loader.description", "Velocity Classpath Resource Loader");

        var velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        return velocityEngine;
    }

    @Autowired
    @Bean
    public VelocityMailTemplateService velocityTemplateService(VelocityEngine velocityEngine) {
        return new VelocityMailTemplateService(velocityEngine);
    }

    @Autowired
    @Bean
    public MailSenderAdapter mailSenderAdapter(MessageRepository messageRepository, JavaMailSender javaMailSender,
                                               UserService userService, Clock clock) {
        return new MailSenderAdapter(messageRepository, javaMailSender, userService, clock);
    }

    @Autowired
    @Bean
    public ProjectApplicationEventHandler mailProjectApplicationHandler(MailSenderAdapter mailSenderAdapter, UserService userService, VelocityMailTemplateService velocityMailTemplateService,
                                                                        MailConfigurationProperties mailConfigProperties) {
        return new MailProjectApplicationEventHandler(mailSenderAdapter, userService, velocityMailTemplateService, mailConfigProperties);
    }

    @Autowired
    @Bean
    public UserAccessEventHandler mailUserAccessEventHandler(MailSenderAdapter mailSenderAdapter, UserService userService, VelocityMailTemplateService velocityMailTemplateService,
                                                             ProjectBoardConfigurationProperties configurationProperties) {
        return new MailUserAccessEventHandler(mailSenderAdapter, userService, velocityMailTemplateService, configurationProperties);
    }

}
