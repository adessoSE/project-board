package de.adesso.projectboard.core.base.rest.user.application.dto;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class ProjectApplicationResponseDTOTest {

    @Test
    public void fromApplication() {
        User user = new User("user");

        JiraProject project = new JiraProject();
        project.setId(1L);

        LocalDateTime applicationTime = LocalDateTime.now().minus(2L, ChronoUnit.DAYS);

        ProjectApplication application = new ProjectApplication(project, "Testcomment", user);
        application.setId(2L);
        application.setApplicationDate(applicationTime);

        ProjectApplicationResponseDTO dto = ProjectApplicationResponseDTO.fromApplication(application);

        assertEquals("Testcomment", dto.getComment());
        assertEquals(applicationTime, dto.getDate());
        assertEquals(1L, dto.getProject().getId());
        assertEquals(2L, dto.getId());
        assertNotNull(dto.getUser());
    }


}