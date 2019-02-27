package de.adesso.projectboard.adapter.mail.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TimeAwareMessageTest {

    @Mock
    private User userMock;

    private LocalDateTime relevancyDate;

    private TimeAwareMessage timeAwareMessage;

    @Before
    public void setUp() {
        this.relevancyDate = LocalDateTime.now();
        this.timeAwareMessage = new TimeAwareMessage(userMock, userMock, "Subject", "Text", relevancyDate);
    }

    @Test
    public void isStillRelevantReturnsTrueWhenRelevancyDateIsAfterOtherDate() {
        // given
        var otherDate = relevancyDate.minus(1L, ChronoUnit.HOURS);

        // when / then
        compareExpectedWithActualResultOfIsStillRelevant(otherDate, true);
    }

    @Test
    public void isStillRelevantReturnsTrueWhenRelevancyDateIsEqualToOtherDate() {
        // given
        var otherDate = relevancyDate;

        // when / then
        compareExpectedWithActualResultOfIsStillRelevant(otherDate, true);
    }

    @Test
    public void isStillRelevantReturnsFalseWhenRelevancyDateIsBeforeOtherDate() {
        // given
        var otherDate = relevancyDate.plus(2L, ChronoUnit.MINUTES);

        // when / then
        compareExpectedWithActualResultOfIsStillRelevant(otherDate, false);
    }

    private void compareExpectedWithActualResultOfIsStillRelevant(LocalDateTime otherDate, boolean expectedResult) {
        // when
        var actualResult = timeAwareMessage.isStillRelevant(otherDate);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

}