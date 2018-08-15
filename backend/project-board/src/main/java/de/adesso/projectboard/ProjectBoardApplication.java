package de.adesso.projectboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class ProjectBoardApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ProjectBoardApplication.class, args);
	}

}
