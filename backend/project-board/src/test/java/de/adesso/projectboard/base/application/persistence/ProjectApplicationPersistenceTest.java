package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class ProjectApplicationPersistenceTest {

    @Autowired
    ProjectApplicationRepository applicationRepo;

    @Autowired
    ProjectRepository projectRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql"
    })
    public void save() {
        // given
        LocalDateTime expectedDate = LocalDateTime.of(2018, 1, 1, 13, 37);
        Project expectedProject = projectRepo.findById("STF-1").orElseThrow(EntityNotFoundException::new);
        User expectedUser = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);
        String expectedComment = "Original Comment";

        ProjectApplication application = new ProjectApplication(expectedProject, expectedComment, expectedUser, expectedDate);
        application.setApplicationDate(expectedDate);

        // when
        ProjectApplication savedApplication = applicationRepo.save(application);
        ProjectApplication retrievedApplication = applicationRepo.findById(savedApplication.getId())
                .orElseThrow(EntityNotFoundException::new);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(retrievedApplication.getApplicationDate()).isEqualTo(expectedDate);
        softly.assertThat(retrievedApplication.getProject()).isEqualTo(expectedProject);
        softly.assertThat(retrievedApplication.getUser()).isEqualTo(expectedUser);
        softly.assertThat(retrievedApplication.getComment()).isEqualTo(expectedComment);

        softly.assertAll();
    }

}