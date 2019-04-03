package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
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
public class UserPersistenceTest {

    @Autowired
    private ProjectApplicationRepository applicationRepo;

    @Autowired
    private AccessIntervalRepository accessIntervalRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void save() {
        // given
        LocalDateTime applicationDate = LocalDateTime.of(2018, 10, 10, 8, 0);
        LocalDateTime intervalStartTime = LocalDateTime.of(2018, 1, 10, 8, 0);
        LocalDateTime intervalEndTime = LocalDateTime.of(2018, 2, 10, 8, 0);
        String expectedUserId = "user";

        Project project = projectRepo.findById("STF-1").orElseThrow(EntityNotFoundException::new);

        User user = new User(expectedUserId);
        user.addBookmark(project);

        new ProjectApplication(project, "Comment", user, applicationDate, false);
        new AccessInterval(user, intervalStartTime, intervalEndTime);

        // when
        userRepo.save(user);
        User retrievedUser = userRepo.findById(expectedUserId).orElseThrow(EntityNotFoundException::new);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(retrievedUser.id).isEqualTo(expectedUserId);
        softly.assertThat(applicationRepo.count()).isEqualTo(1);
        softly.assertThat(accessIntervalRepo.count()).isEqualTo(1);

        softly.assertAll();
    }

}
