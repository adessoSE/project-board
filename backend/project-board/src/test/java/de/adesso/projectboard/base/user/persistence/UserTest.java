package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Mock
    ProjectApplication applicationMock;

    @Mock
    AccessInfo accessInfoMock;

    @Test
    public void constructorIdSet() {
        // given
        String expectedUserId = "user";

        // when
        User user = new User(expectedUserId);

        // then
        assertThat(user.id).isEqualTo(expectedUserId);
    }

    @Test
    public void constructorCollectionsNotNull() {
        // given

        // when
        User user = new User("user");

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(user.bookmarks).isNotNull();
        softly.assertThat(user.accessInfoList).isNotNull();
        softly.assertThat(user.applications).isNotNull();
        softly.assertThat(user.ownedProjects).isNotNull();

        softly.assertAll();
    }

    @Test
    public void addBookmark() {
        // given
        Project project = mock(Project.class);
        User user = new User("user");

        // when
        user.addBookmark(project);

        // then
        assertThat(user.bookmarks).containsExactly(project);
    }


    @Test
    public void removeBookmark() {
        // given
        Project project = mock(Project.class);

        User user = new User("user");
        user.bookmarks.add(project);

        // when
        user.removeBookmark(project);

        // then
        assertThat(user.bookmarks).isEmpty();
    }

    @Test
    public void addApplicationBelongsToUser() {
        // given
        User user = new User("user");

        given(applicationMock.getUser()).willReturn(user);

        // when
        user.addApplication(applicationMock);

        // then
        assertThat(user.applications).containsExactly(applicationMock);
    }

    @Test
    public void addApplicationThrowsExceptionWhenNotBelongingToUser() {
        // given
        User user = new User("user");
        User wrongUser = new User("other-user");

        given(applicationMock.getUser()).willReturn(wrongUser);

        // when
        assertThatThrownBy(() -> user.addApplication(applicationMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The application belongs to another user!");
    }

    @Test
    public void removeApplication() {
        // given
        User user = new User("user");
        user.applications.add(applicationMock);

        // when
        user.removeApplication(applicationMock);

        // then
        assertThat(user.applications).isEmpty();
    }

    @Test
    public void addAccessInfoBelongsToUser() {
        // given
        User user = new User("user");

        given(accessInfoMock.getUser()).willReturn(user);

        // when
        user.addAccessInfo(accessInfoMock);

        // then
        assertThat(user.accessInfoList).containsExactly(accessInfoMock);
    }

    @Test
    public void addAccessInfoThrowsExceptionWhenNotBelongingToUser() {
        // given
        User user = new User("user");
        User otherUser = new User("other-user");

        given(accessInfoMock.getUser()).willReturn(otherUser);

        // when
        assertThatThrownBy(() -> user.addAccessInfo(accessInfoMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Info instance belongs to a different or no user!");
    }

    @Test
    public void removeAccessInfo() {
        // given
        User user = new User("user");
        user.accessInfoList.add(accessInfoMock);

        // when
        user.removeAccessInfo(accessInfoMock);

        // then
        assertThat(user.accessInfoList).isEmpty();
    }

    @Test
    public void getLatestAccessInfoReturnsElementCollectionContainsSingleElement() {
        // given
        User user = new User("user");
        user.accessInfoList.add(accessInfoMock);

        // when
        AccessInfo actualAccessInfo = user.getLatestAccessInfo();

        // then
        assertThat(actualAccessInfo).isEqualTo(accessInfoMock);
    }

    @Test
    public void getLatestAccessInfoReturnsLastAddedWhenCollectionContainsMultipleElements() {
        // given
        AccessInfo expectedAccessInfoMock = mock(AccessInfo.class);

        User user = new User("user");
        user.accessInfoList.addAll(Arrays.asList(accessInfoMock, expectedAccessInfoMock));

        // when
        AccessInfo actualAccessInfo = user.getLatestAccessInfo();

        // then
        assertThat(actualAccessInfo).isEqualTo(expectedAccessInfoMock);
    }

    @Test
    public void getLatestAccessInfoReturnsNullWhenCollectionContainsNoElement() {
        // given
        User user = new User("user");

        // when
        AccessInfo actualAccessInfo = user.getLatestAccessInfo();

        // then
        assertThat(actualAccessInfo).isNull();
    }

    @Test
    public void addOwnedProject() {
        // given
        Project project = mock(Project.class);

        User user = new User("user");

        // when
        user.addOwnedProject(project);

        // then
        assertThat(user.ownedProjects).containsExactly(project);
    }

    @Test
    public void removeOwnedProject() {
        // given
        Project project = mock(Project.class);

        User user = new User("user");
        user.ownedProjects.add(project);

        // when
        user.removeOwnedProject(project);

        // then
        assertThat(user.ownedProjects).isEmpty();
    }

    @Test
    public void equalsSameIdAndCollectionsEmpty() {
        // given
        User firstUser = new User("user");
        User secondUser = new User("user");

        // when
        boolean actualEquals = firstUser.equals(secondUser);

        // then
        assertThat(actualEquals).isTrue();
    }

}