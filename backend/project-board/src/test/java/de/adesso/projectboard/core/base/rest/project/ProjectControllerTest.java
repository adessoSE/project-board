package de.adesso.projectboard.core.base.rest.project;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectController projectController;

    @Before
    public void setUp() {
        when(projectRepository.findAll()).thenReturn(getProjects());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    public void testGetAllForUser_SuperUser() {
        when(userService.getCurrentUser()).thenReturn(getTestSuperUser("LOB Test"));

        List<Project> allForUser = projectController.getAllForUser();

        boolean allEscalatedOrOpen =
                allForUser.stream()
                .allMatch(project -> {
                    boolean isOpen = "offen".equalsIgnoreCase(project.getStatus());
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());

                    return isOpen || isEscalated;
                });
        assertTrue(allEscalatedOrOpen);

        assertEquals(6L, allForUser.size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    public void testGetAllForUser_User() {
        when(userService.getCurrentUser()).thenReturn(getTestUser("LOB Test"));

        List<Project> allForUser = projectController.getAllForUser();

        boolean allEscalatedOrFromSameLob = allForUser.stream()
                .allMatch(project -> {
                    boolean isOpen = "offen".equalsIgnoreCase(project.getStatus());
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
                    boolean sameLobAsUser = "LOB Test".equalsIgnoreCase(project.getLob());
                    boolean noLob = project.getLob() == null;

                    // escalated || isOpen <-> (sameLob || noLob)
                    // equivalence because implication is not enough
                    // when the status is neither "eskaliert" nor "offen"
                    return isEscalated || (isOpen && (sameLobAsUser || noLob) || (!isOpen && !(sameLobAsUser || noLob)));
                });

        assertTrue(allEscalatedOrFromSameLob);

        assertEquals(5L, allForUser.size());
    }

    private Iterable<Project> getProjects() {
        Project firstProject = Project.builder()
                .id("STD-1")
                .status("Offen")
                .lob("LOB Test")
                .build();

        Project secondProject = Project.builder()
                .id("STD-2")
                .status("eskaliert")
                .lob("LOB Test")
                .build();

        Project thirdProject = Project.builder()
                .id("STD-3")
                .status("Abgeschlossen")
                .lob("LOB Test")
                .build();

        Project fourthProject = Project.builder()
                .id("STD-4")
                .status("Offen")
                .lob("LOB Prod")
                .build();

        Project fifthProject = Project.builder()
                .id("STD-5")
                .status("eskaliert")
                .lob("LOB Prod")
                .build();

        Project sixthProject = Project.builder()
                .id("STD-6")
                .status("Offen")
                .lob(null)
                .build();

        Project seventhProject = Project.builder()
                .id("STD-7")
                .status("eskaliert")
                .lob(null)
                .build();

        Project eighthProject = Project.builder()
                .id("STD-8")
                .status("Abgeschlossen")
                .lob(null)
                .build();

        Project ninthProject = Project.builder()
                .id("STD-8")
                .status("Something weird")
                .lob(null)
                .build();

        return Arrays.asList(firstProject, secondProject, thirdProject,
                fourthProject, fifthProject, sixthProject,
                seventhProject, eighthProject, ninthProject);
    }

    private SuperUser getTestSuperUser(String lob) {
        SuperUser superUser = new SuperUser("user-1");
        superUser.setLob(lob);

        return superUser;
    }

    private User getTestUser(String lob) {
        User user = new User("user-2", getTestSuperUser(lob));
        user.setLob(lob);

        return user;
    }

}