package de.adesso.projectboard.core.base.rest.user.dto;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserResponseDTOTest {

    @Test
    public void fromUser() {
        Project project = new Project();
        project.setId("Key");

        SuperUser firstUser = new SuperUser("first-user");
        firstUser.setFullName("First", "User");
        firstUser.setEmail("first.user@example.com");
        firstUser.setLob("LOB Test");

        User secondUser = new User("second-user", firstUser);
        secondUser.setFullName("Second", "User");
        secondUser.setEmail("second.user@example.com");
        secondUser.setLob("LOB Test");

        firstUser.addApplication(new ProjectApplication(project, "Testcomment", firstUser));
        firstUser.addBookmark(project);
        firstUser.addCreatedProject(new Project());

        UserResponseDTO dto = UserResponseDTO.fromUser(firstUser);

        assertEquals(1L, dto.getApplications().getCount());
        assertEquals(1L, dto.getBookmarks().getCount());
        assertEquals(1L, dto.getProjects().getCount());
        assertEquals(2L, dto.getStaff().getCount());
        assertEquals("first-user", dto.getId());
        assertEquals("First", dto.getFirstName());
        assertEquals("User", dto.getLastName());
        assertEquals("first.user@example.com", dto.getEmail());
        assertEquals("LOB Test", dto.getLob());
        assertTrue(dto.isBoss());
        assertFalse(dto.getAccessInfo().isHasAccess());
    }

}