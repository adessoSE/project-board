package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.project.service.ProjectService;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class BookmarkServiceIntegrationTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectRepository projRepo;

    @Autowired
    private BookmarkService bookmarkService;

    @MockBean
    private UserService userService;

    @MockBean
    private ProjectService projectService;

    @Before
    public void setUp() {
        // DB setup
        User user = new SuperUser("user");
        user.setFullName("Test", "User");
        user.setLob("LOB Test");
        user.setEmail("test-user@test.com");

        Project project = Project.builder()
                .id("STF-1")
                .title("Title")
                .build();

        projRepo.save(project);
        userRepo.save(user);

        // mock setup
        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq("user"))).thenReturn(userRepo.findById("user").get());

        when(projectService.getProjectById(anyString())).thenThrow(ProjectNotFoundException.class);
        when(projectService.getProjectById(eq("STF-1"))).thenReturn(projRepo.findById("STF-1").get());

        when(projectService.projectExists(anyString())).thenReturn(false);
        when(projectService.projectExists(eq("STF-1"))).thenReturn(true);
    }

    @Test
    public void testAddBookmarkToUser() {
        User user = userRepo.findById("user").get();
        assertEquals(0L, user.getBookmarks().size());

        bookmarkService.addBookmarkToUser("user", "STF-1");

        user = userRepo.findById("user").get();
        List<Project> bookmarks = new ArrayList<>(user.getBookmarks());

        assertEquals(1L, user.getBookmarks().size());
        Project bookmark = bookmarks.get(0);
        assertEquals("STF-1", bookmark.getId());
    }

    @Test
    public void testRemoveBookmarkFromUser_OK() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        user.addBookmark(project);
        user = userRepo.save(user);
        assertEquals(1L, user.getBookmarks().size());
        assertTrue(user.getBookmarks().contains(project));

        bookmarkService.removeBookmarkFromUser(user.getId(), project.getId());

        user = userRepo.findById("user").get();
        assertEquals(0L, user.getBookmarks().size());
    }

    @Test(expected = BookmarkNotFoundException.class)
    public void testRemoveBookmarkFromUser_BookmarkNotFound() {
        bookmarkService.removeBookmarkFromUser("user", "STF-1");
    }

    @Test
    public void testGetBookmarksOfUser() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        assertEquals(0L, bookmarkService.getBookmarksOfUser("user").size());

        user.addBookmark(project);
        userRepo.save(user);

        Set<Project> bookmarks = bookmarkService.getBookmarksOfUser("user");
        assertEquals(1L, bookmarks.size());
        assertTrue(bookmarks.contains(project));
    }

    @Test
    public void testUserHasBookmark() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        assertFalse(bookmarkService.userHasBookmark(user.getId(), project.getId()));

        user.addBookmark(project);
        userRepo.save(user);

        assertTrue(bookmarkService.userHasBookmark(user.getId(), project.getId()));
    }

}