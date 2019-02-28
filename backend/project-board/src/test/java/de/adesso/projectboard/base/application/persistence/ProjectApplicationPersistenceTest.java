package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class ProjectApplicationPersistenceTest {

    @Autowired
    private ProjectApplicationRepository applicationRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql"
    })
    public void save() {
        // given
        var expectedDate = LocalDateTime.of(2018, 1, 1, 13, 37);
        var expectedProject = projectRepo.findById("STF-1").orElseThrow();
        var expectedUser = userRepo.findById("User1").orElseThrow();
        var expectedComment = "Original Comment";

        var application = new ProjectApplication(expectedProject, expectedComment, expectedUser, expectedDate, false);
        application.setApplicationDate(expectedDate);

        // when
        var savedApplication = applicationRepo.save(application);
        var retrievedApplication = applicationRepo.findById(savedApplication.getId())
                .orElseThrow();

        // then
        var softly = new SoftAssertions();

        softly.assertThat(retrievedApplication.getApplicationDate()).isEqualTo(expectedDate);
        softly.assertThat(retrievedApplication.getProject()).isEqualTo(expectedProject);
        softly.assertThat(retrievedApplication.getUser()).isEqualTo(expectedUser);
        softly.assertThat(retrievedApplication.getComment()).isEqualTo(expectedComment);

        softly.assertAll();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql",
            "classpath:/de/adesso/projectboard/persistence/Applications.sql"
    })
    public void findAllByUserIn() {
        // given
        var firstUser = userRepo.findById("User1").orElseThrow();
        var secondUser = userRepo.findById("User2").orElseThrow();

        var expectedApplications = applicationRepo.findAllById(Set.of(1L , 2L, 3L));

        // when
        var actualApplications = applicationRepo.findAllByUserIn(Set.of(firstUser, secondUser), Sort.unsorted());

        // then
        assertThat(actualApplications).containsOnlyElementsOf(expectedApplications);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql",
            "classpath:/de/adesso/projectboard/persistence/Applications.sql"
    })
    public void findAllByProjectEquals() {
        // given

        // when

        // then

    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql",
            "classpath:/de/adesso/projectboard/persistence/Applications.sql"
    })
    public void existsByUserAndProjectReturnsTrueWhenApplicationWithGivenProjectAndUserExists() {
        // given
        var project = projectRepo.findById("STF-1").orElseThrow();
        var user = userRepo.findById("User1").orElseThrow();

        // when
        boolean actualExists = applicationRepo.existsByUserAndProject(user, project);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql",
            "classpath:/de/adesso/projectboard/persistence/Applications.sql"
    })
    public void existsByUserAndProjectReturnsFalseWhenApplicationWithGivenProjectAndUserDoesNotExist() {
        // given
        var project = projectRepo.findById("STF-1").orElseThrow();
        var user = userRepo.findById("User2").orElseThrow();

        // when
        boolean actualExists = applicationRepo.existsByUserAndProject(user, project);

        // then
        assertThat(actualExists).isFalse();
    }

}