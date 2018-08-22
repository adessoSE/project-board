package de.adesso.projectboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EntityScan
@EnableJpaRepositories
public class ProjectBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectBoardApplication.class, args);
	}

}
