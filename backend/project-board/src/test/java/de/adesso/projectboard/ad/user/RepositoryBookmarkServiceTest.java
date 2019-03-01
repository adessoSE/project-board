package de.adesso.projectboard.ad.user;

import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryBookmarkServiceTest {

    private final String USER_ID = "user";

    @Mock
    UserRepository userRepository;

    @Mock
    Project projectMock;

    @Mock
    User userMock;

    RepositoryBookmarkService bookmarkService;

    @Before
    public void setUp() {
        this.bookmarkService = new RepositoryBookmarkService(userRepository);
    }

    @Test
    public void addBookmarkToUserAddsBookmark() {
        // given

        // when
        Project actualProject = bookmarkService.addBookmarkToUser(userMock, projectMock);

        // then
        assertThat(actualProject).isEqualTo(projectMock);

        verify(userMock).addBookmark(projectMock);
        verify(userRepository).save(userMock);
    }

    @Test
    public void removeBookmarkOfUserRemovesBookmark() {
        // given
        given(userMock.getId()).willReturn(USER_ID);
        given(userRepository.existsByIdAndBookmarksContaining(USER_ID, projectMock)).willReturn(true);

        // when
        bookmarkService.removeBookmarkOfUser(userMock, projectMock);

        // then
        verify(userMock).removeBookmark(projectMock);
        verify(userRepository).save(userMock);
    }

    @Test
    public void removeBookmarkOfUserThrowsExceptionWhenNotBookmarked() {
        // given
        given(userMock.getId()).willReturn(USER_ID);
        given(userRepository.existsByIdAndBookmarksContaining(USER_ID, projectMock)).willReturn(false);

        // when
        assertThatThrownBy(() -> bookmarkService.removeBookmarkOfUser(userMock, projectMock))
                .isInstanceOf(BookmarkNotFoundException.class);
    }

    @Test
    public void getBookmarksOfUserReturnsAllBookmarks() {
        // given
        given(userMock.getBookmarks()).willReturn(Collections.singleton(projectMock));

        // when
        List<Project> actualBookmarks = bookmarkService.getBookmarksOfUser(userMock);

        // then
        assertThat(actualBookmarks).containsExactly(projectMock);
    }

    @Test
    public void userHasBookmarkReturnsTrueWhenBookmarkExists() {
        // given
        given(userMock.getId()).willReturn(USER_ID);
        given(userRepository.existsByIdAndBookmarksContaining(USER_ID, projectMock)).willReturn(true);

        // when
        boolean actualHasBookmarked = bookmarkService.userHasBookmark(userMock, projectMock);

        // then
        assertThat(actualHasBookmarked).isTrue();
    }

    @Test
    public void userHasBookmarkReturnsFalseWhenBookmarkDoesNotExists() {
        // given
        given(userMock.getId()).willReturn(USER_ID);
        given(userRepository.existsByIdAndBookmarksContaining(USER_ID, projectMock)).willReturn(false);

        // when
        boolean actualHasBookmarked = bookmarkService.userHasBookmark(userMock, projectMock);

        // then
        assertThat(actualHasBookmarked).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void removeAllBookmarksOfUser() {
        // given
        Set<Project> bookmarksMock = mock(Set.class);
        given(userMock.getBookmarks()).willReturn(bookmarksMock);

        // when
        bookmarkService.removeAllBookmarksOfUser(userMock);

        // then
        verify(bookmarksMock).clear();
        verify(userRepository).save(userMock);
    }
}