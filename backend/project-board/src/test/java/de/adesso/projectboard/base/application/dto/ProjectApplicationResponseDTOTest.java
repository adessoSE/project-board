package de.adesso.projectboard.base.application.dto;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProjectApplicationResponseDTOTest {

    @Test
    public void fromApplication() {
        SuperUser superUser = new SuperUser("test-user-1");
        User user = new User("test-user-2", superUser);

        Project project = new Project();
        project.setId("Key");

        LocalDateTime applicationTime = LocalDateTime.now().minus(2L, ChronoUnit.DAYS);

        ProjectApplication application = new ProjectApplication(project, "Testcomment", user);
        application.setId(2L);
        application.setApplicationDate(applicationTime);

        ProjectApplicationResponseDTO dto = ProjectApplicationResponseDTO.fromApplication(application);

        assertEquals("Testcomment", dto.getComment());
        assertEquals(applicationTime, dto.getDate());
        assertEquals("Key", dto.getProject().getId());
        assertEquals(2L, dto.getId());
        assertNotNull(dto.getUser());
    }


}