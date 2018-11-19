package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInfoRepository;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class UserPersistenceTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    ProjectRepository projectRepo;

    @Autowired
    AccessInfoRepository infoRepo;

    @Autowired
    ProjectApplicationRepository applicationRepo;

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Projects.sql",
    })
    public void testSave() {
        User user = new User("User1");

        assertTrue(true);
    }

}