package de.adesso.projectboard.core.base.rest.user.dto;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserResponseDTOTest {

    @Test
    public void fromUser() {
        JiraProject jiraProject = new JiraProject();
        jiraProject.setId(1L);
        jiraProject.setKey("Testkey");

        User user = new User("user");
        user.addApplication(new ProjectApplication(jiraProject, "Testcomment", user));
        user.addBookmark(jiraProject);

        UserResponseDTO dto = UserResponseDTO.fromUser(user);

        assertEquals(1L, dto.getApplications().getCount());
        assertEquals(1L, dto.getBookmarks().getCount());
        assertEquals("user", dto.getId());
    }

}