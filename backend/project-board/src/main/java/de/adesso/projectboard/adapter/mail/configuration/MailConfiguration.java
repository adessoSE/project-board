package de.adesso.projectboard.adapter.mail.configuration;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.handler.MailProjectApplicationEventHandler;
import de.adesso.projectboard.adapter.mail.handler.MailUserAccessEventHandler;
import de.adesso.projectboard.adapter.mail.persistence.MessageRepository;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.user.service.UserService;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Properties;

@ConditionalOnResource(resources = "classpath:mail.properties")
@PropertySource("classpath:mail.properties")
@Configuration
public class MailConfiguration {

    @Bean("velocityEngine")
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
    @Bean("velocityMailTemplateService")
    @DependsOn("velocityEngine")
    public VelocityMailTemplateService velocityTemplateService(VelocityEngine velocityEngine) {
        return new VelocityMailTemplateService(velocityEngine);
    }

    @Autowired
    @Bean("mailSenderService")
    public MailSenderService mailSenderService(MessageRepository messageRepository, JavaMailSenderImpl javaMailSender,
                                               UserService userService, Clock clock) {
        return new MailSenderService(messageRepository, javaMailSender, userService, clock);
    }

    @Autowired
    @Bean
    @DependsOn({"mailSenderService", "velocityMailTemplateService"})
    public ProjectApplicationEventHandler mailProjectApplicationHandler(MailSenderService mailSenderService, UserService userService, VelocityMailTemplateService velocityMailTemplateService) {
        return new MailProjectApplicationEventHandler(mailSenderService, userService, velocityMailTemplateService);
    }

    @Autowired
    @Bean
    @DependsOn({"mailSenderService", "velocityMailTemplateService"})
    public UserAccessEventHandler mailUserAccessEventHandler(MailSenderService mailSenderService, UserService userService, VelocityMailTemplateService velocityMailTemplateService) {
        return new MailUserAccessEventHandler(mailSenderService, userService, velocityMailTemplateService);
    }

}
