package de.adesso.projectboard.base.application.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectApplicationPersistenceTest {

    @Autowired
    private ProjectApplicationRepository applicationRepo;

    @Before
    public void setUp() {

    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:/de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:/de/adesso/projectboard/core/base/persistence/Applications.sql"
    })
    public void testSave() {
        Optional<ProjectApplication> applicationOptional = applicationRepo.findById(1L);
        assertTrue(applicationOptional.isPresent());

        ProjectApplication application = applicationOptional.get();

        assertEquals(LocalDateTime.of(2018, 1, 1, 13, 37), application.getApplicationDate());
        assertEquals("First application", application.getComment());
        assertNotNull(application.getUser());
        assertEquals("User1", application.getUser().getId());
        assertNotNull(application.getProject());
        assertEquals("STF-3", application.getProject().getId());
    }

}