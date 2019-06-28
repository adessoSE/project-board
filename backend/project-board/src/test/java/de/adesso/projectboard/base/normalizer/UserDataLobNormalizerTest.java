package de.adesso.projectboard.base.normalizer;

import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserDataLobNormalizerTest {

    private UserDataLobNormalizer userDataLobNormalizer;

    @Mock
    private UserData userDataMock;

    @Before
    public void setUp() {
        this.userDataLobNormalizer = new UserDataLobNormalizer(Set.of());
    }

    @Test
    public void getLobOfReturnsLobOfUserData() {
        // given
        var expectedLob = "LOB Test 1";

        given(userDataMock.getLob()).willReturn(expectedLob);

        // when
        var actualLob = userDataLobNormalizer.getFieldValue(userDataMock);

        // then
        assertThat(actualLob).isEqualTo(expectedLob);
    }

    @Test
    public void setNormalizedLobSetsLobAndReturnsUpdatedUserData() {
        // given
        var normalizedLob = "LOB Test 2";

        given(userDataMock.setLob(normalizedLob)).willReturn(userDataMock);

        // when
        var actualUserData = userDataLobNormalizer.setNormalizedFieldValue(userDataMock, normalizedLob);

        // then
        assertThat(actualUserData).isEqualTo(userDataMock);
    }

}
