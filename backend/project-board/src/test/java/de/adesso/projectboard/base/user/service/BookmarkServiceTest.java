package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectServiceImpl;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.ldap.user.BookmarkServiceImpl;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private LdapUserService userService;

    @Mock
    private ProjectServiceImpl projectService;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private User testUser;

    private Project testProject;

    @Before
    public void setUp() {
        // test data setup
        this.testUser = new SuperUser("testUser");
        this.testUser.setFullName("Test", "User");
        this.testUser.setLob("LOB Test");
        this.testUser.setEmail("test-testUser@test.com");

        this.testProject = new Project()
                .setId("STF-1")
                .setTitle("Title");

        // mock setup for methods used often
        when(userRepo.save(testUser)).thenReturn(testUser);
        when(userRepo.existsByIdAndBookmarksContaining(anyString(), any(Project.class))).thenReturn(false);

        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq(testUser.getId()))).thenReturn(testUser);

        when(projectService.getProjectById(anyString())).thenThrow(ProjectNotFoundException.class);
        when(projectService.getProjectById(eq(testProject.getId()))).thenReturn(testProject);

        when(projectService.projectExists(anyString())).thenReturn(false);
        when(projectService.projectExists(eq(testProject.getId()))).thenReturn(true);
    }

    @Test
    public void testAddBookmarkToUser_OK() {
        assertEquals(0L, testUser.getBookmarks().size());

        // call the tested method
        Project returnedProject = bookmarkService.addBookmarkToUser(testUser.getId(), testProject.getId());

        verify(userRepo).save(testUser);
        assertEquals(testProject, returnedProject);
        assertEquals(1L, testUser.getBookmarks().size());
        assertTrue(testUser.getBookmarks().contains(testProject));
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddBookmarkToUser_UserNotExists() {
        bookmarkService.addBookmarkToUser("non-existent-user", testProject.getId());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testAddBookmarkToUser_ProjectNotExists() {
        bookmarkService.addBookmarkToUser(testUser.getId(), "non-existent-project");
    }

    @Test
    public void testRemoveBookmarkFromUser_OK() {
        testUser.addBookmark(testProject);
        assertEquals(1L, testUser.getBookmarks().size());
        assertTrue(testUser.getBookmarks().contains(testProject));

        // mock setup
        when(userRepo.existsByIdAndBookmarksContaining(eq("testUser"), eq(testProject))).thenReturn(true);

        // call the tested method
        bookmarkService.removeBookmarkFromUser(testUser.getId(), testProject.getId());

        verify(userRepo).save(testUser);
        assertEquals(0L, testUser.getBookmarks().size());
    }

    @Test(expected = BookmarkNotFoundException.class)
    public void testRemoveBookmarkFromUser_BookmarkNotFound() {
        assertFalse(testUser.getBookmarks().contains(testProject));

        bookmarkService.removeBookmarkFromUser(testUser.getId(), testProject.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void testRemoveBookmarkFromUser_UserNotExists() {
        bookmarkService.removeBookmarkFromUser("non-existent-user", testProject.getId());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testRemoveBookmarkFromUser_ProjectNotExists() {
        bookmarkService.removeBookmarkFromUser(testUser.getId(), "non-existent-project");
    }

    @Test
    public void testGetBookmarksOfUser() {
        assertEquals(0L, bookmarkService.getBookmarksOfUser(testUser.getId()).size());

        testUser.addBookmark(testProject);
        assertEquals(1L, testUser.getBookmarks().size());
        assertTrue(testUser.getBookmarks().contains(testProject));

        Set<Project> bookmarks = bookmarkService.getBookmarksOfUser(testUser.getId());
        assertEquals(1L, bookmarks.size());
        assertTrue(bookmarks.contains(testProject));
    }

    @Test
    public void testUserHasBookmark_OK() {
        assertFalse(testUser.getBookmarks().contains(testProject));
        assertFalse(bookmarkService.userHasBookmark(testUser.getId(), testProject.getId()));

        testUser.addBookmark(testProject);
        assertEquals(1L, testUser.getBookmarks().size());
        assertTrue(testUser.getBookmarks().contains(testProject));

        // mock setup
        when(userRepo.existsByIdAndBookmarksContaining(eq("testUser"), eq(testProject))).thenReturn(true);

        verify(userRepo).existsByIdAndBookmarksContaining(testUser.getId(), testProject);
        assertTrue(bookmarkService.userHasBookmark(testUser.getId(), testProject.getId()));
    }

    @Test
    public void testUserHasBookmark_UserNotExists() {
        testUserHasBookmark_OK();
    }

    @Test
    public void testUserHasBookmark_ProjectNotExists() {
        assertFalse(bookmarkService.userHasBookmark(testUser.getId(), "non-existent-project"));
    }

}