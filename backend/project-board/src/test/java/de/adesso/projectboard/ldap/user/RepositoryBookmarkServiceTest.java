package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryBookmarkServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    RepositoryBookmarkService bookmarkService;

    @Mock
    User user;

    @Mock
    Project project;

    @Before
    public void setUp() {
        // set up repo mock
        when(userRepository.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            Object[] args = invocation.getArguments();

            return (User) args[0];
        });


        // set up entity mocks
        when(user.getId()).thenReturn("user");
    }


    @Test
    public void testAddBookmarkToUser() {
        bookmarkService.addBookmarkToUser(user, project);

        verify(user).addBookmark(project);
        verify(userRepository).save(user);
    }

    @Test
    public void testRemoveBookmarkOfUser_OK() {
        // set up repo mock
        when(userRepository.existsByIdAndBookmarksContaining("user", project)).thenReturn(true);

        bookmarkService.removeBookmarkOfUser(user, project);

        verify(user).removeBookmark(project);
        verify(userRepository).save(user);
    }

    @Test(expected = BookmarkNotFoundException.class)
    public void testRemoveBookmarkOfUser_NotBookmarked() {
        // set up repo mock
        when(userRepository.existsByIdAndBookmarksContaining("user", project)).thenReturn(false);

        bookmarkService.removeBookmarkOfUser(user, project);
    }

    @Test
    public void testGetBookmarksOfUser() {
        // set up entity mock
        when(user.getBookmarks()).thenReturn(Collections.singleton(project));

        List<Project> bookmarks = bookmarkService.getBookmarksOfUser(user);

        assertEquals(1, bookmarks.size());
        assertTrue(bookmarks.contains(project));
    }

    @Test
    public void testUserHasBookmark_HasBookmark() {
        // set up repo mock
        when(userRepository.existsByIdAndBookmarksContaining("user", project)).thenReturn(true);

        assertTrue(bookmarkService.userHasBookmark(user, project));
    }

    @Test
    public void testUserHasBookmark_HasNoBookmark() {
        // set up repo mock
        when(userRepository.existsByIdAndBookmarksContaining("user", project)).thenReturn(false);

        assertFalse(bookmarkService.userHasBookmark(user, project));
    }


}