package de.adesso.projectboard.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-integration-test.properties")
public class IntegrationTestConfiguration {

}
