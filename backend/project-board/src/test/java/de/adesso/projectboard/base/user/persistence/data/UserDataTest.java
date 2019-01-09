package de.adesso.projectboard.base.user.persistence.data;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDataTest {

    @Test
    public void getFullName() {
        // given
        var firstName = "First";
        var lastName = "Last";
        var expectedFullName = String.format("%s %s", firstName, lastName);

        var userData = new UserData()
                .setFirstName(firstName)
                .setLastName(lastName);

        // when
        var actualLastName = userData.getFullName();

        // then
        assertThat(actualLastName).isEqualTo(expectedFullName);
    }

}