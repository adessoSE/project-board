package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Before
    public void setUp() {

    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/persistence/Projects.sql"
    })
    public void testSave() {
        LocalDateTime date = LocalDateTime.of(2018, 1, 1, 13, 37);
        Project project = projectRepo.findById("STF-1").orElseThrow(EntityNotFoundException::new);
        User user = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);

        ProjectApplication application = new ProjectApplication(project, "Original Comment", user);
        application.setApplicationDate(date);

        assertEquals(date, application.getApplicationDate());
        assertEquals("Original Comment", application.getComment());
        assertNotNull(application.getUser());
        assertEquals("User1", application.getUser().getId());
        assertNotNull(application.getProject());
        assertEquals("STF-1", application.getProject().getId());
    }

}