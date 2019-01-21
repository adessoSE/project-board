package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static de.adesso.projectboard.util.TestHelper.assertEqualsAndHashCodeEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProjectApplicationTest {

    @Mock
    private Project projectMock;

    @Mock
    private Project otherProjectMock;

    @Mock
    private User userMock;

    @Mock
    private User otherUserMock;

    @Test
    public void equalsReturnsTrueForSameInstance() {
        // given
        long applicationId = 1;
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        // when
        boolean actualEquals = application.equals(application);

        // then
        assertThat(actualEquals).isTrue();
    }

    @Test
    public void equalsReturnsTrueForBothIdsNullAndHashCodeEquals() {
        // given
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);

        var otherApplication = new ProjectApplication(projectMock, comment, userMock, date);

        // when
        boolean actualEquals = application.equals(otherApplication);

        int applicationHash = application.hashCode();
        int otherApplicationHash = otherApplication.hashCode();

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualEquals).isTrue();
        softly.assertThat(applicationHash).isEqualTo(otherApplicationHash);

        softly.assertAll();
    }

    @Test
    public void equalsReturnsTrueForBothProjectsNullAndHashCodeEquals() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(null, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(null, comment, userMock, date);
        otherApplication.id = applicationId;

        // when & then
        assertEqualsAndHashCodeEquals(application, otherApplication);
    }

    @Test
    public void equalsReturnsTrueForBothCommentsNullAndHashCodeEquals() {
        // given
        long applicationId = 1;
        var userId = "user";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, null, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, null, userMock, date);
        otherApplication.id = applicationId;

        // when & then
        assertEqualsAndHashCodeEquals(application, otherApplication);
    }

    @Test
    public void equalsReturnsTrueForBothDatesNullAndHashCodeEquals() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, null);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, comment, userMock, null);
        otherApplication.id = applicationId;

        // when & then
        assertEqualsAndHashCodeEquals(application, otherApplication);
    }

    @Test
    public void equalsReturnsTrueForBothUserIdsNullAndHashCodeEquals() {
        // given
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(null);

        var application = new ProjectApplication(projectMock, comment, userMock, date);

        var otherApplication = new ProjectApplication(projectMock, comment, userMock, date);

        // when & then
        assertEqualsAndHashCodeEquals(application, otherApplication);
    }

    @Test
    public void equalsReturnsTrueForSameFieldValuesAndHashCodeEquals() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, comment, userMock, date);
        otherApplication.id = applicationId;

        // when & then
        assertEqualsAndHashCodeEquals(application, otherApplication);
    }

    @Test
    public void equalsReturnsTrueForDifferentUserWithSameIdAndHashCodeEquals() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);
        given(otherUserMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, comment, otherUserMock, date);
        otherApplication.id = applicationId;

        // when & then
        assertEqualsAndHashCodeEquals(application, otherApplication);
    }

    @Test
    public void equalsReturnsFalseForDifferentType() {
        // given
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        var application = new ProjectApplication(projectMock, comment, userMock, date);

        // when
        boolean actualEquals = application.equals("Test");

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentId() {
        // given
        long applicationId = 1;
        long otherApplicationId = 2;
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, comment, userMock, date);
        otherApplication.id = otherApplicationId;

        // when
        boolean actualEquals = application.equals(otherApplication);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentProject() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(otherProjectMock, comment, userMock, date);
        otherApplication.id = applicationId;

        // when
        boolean actualEquals = application.equals(otherApplication);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentComment() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";
        var otherComment = "This is a different comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, otherComment, userMock, date);
        otherApplication.id = applicationId;

        // when
        boolean actualEquals = application.equals(otherApplication);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentDate() {
        // given
        long applicationId = 1;
        var userId = "user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();
        var otherDate = date.minus(1L, ChronoUnit.MINUTES);

        given(userMock.getId()).willReturn(userId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, comment, userMock, otherDate);
        otherApplication.id = applicationId;

        // when
        boolean actualEquals = application.equals(otherApplication);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentUserIds() {
        // given
        long applicationId = 1;
        var userId = "user";
        var otherUserId = "other-user";
        var comment = "This is a comment!";
        var date = LocalDateTime.now();

        given(userMock.getId()).willReturn(userId);
        given(otherUserMock.getId()).willReturn(otherUserId);

        var application = new ProjectApplication(projectMock, comment, userMock, date);
        application.id = applicationId;

        var otherApplication = new ProjectApplication(projectMock, comment, otherUserMock, date);
        otherApplication.id = applicationId;

        // when
        boolean actualEquals = application.equals(otherApplication);

        // then
        assertThat(actualEquals).isFalse();
    }

}