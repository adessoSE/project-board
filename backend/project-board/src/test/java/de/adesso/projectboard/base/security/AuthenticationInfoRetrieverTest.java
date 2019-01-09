package de.adesso.projectboard.base.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class AuthenticationInfoRetrieverTest {

    private AuthenticationInfoRetriever authInfoRetriever;

    @Before
    public void setUp() {
        this.authInfoRetriever = () -> "";
    }

    @WithMockUser(roles = "admin")
    @Test
    public void hasAdminRoleReturnsTrueWhenAdminRoleIsPresent() {
        // given
        // method annotation

        // when
        boolean actualHasAdminRole = authInfoRetriever.hasAdminRole();

        // then
        assertThat(actualHasAdminRole).isTrue();
    }

    @WithMockUser(roles = "user")
    @Test
    public void hasAdminRoleReturnsFalseWhenAdminRoleIsNotPresent() {
        // given
        // method annotation

        // when
        boolean actualHasAdminRole = authInfoRetriever.hasAdminRole();

        // then
        assertThat(actualHasAdminRole).isFalse();
    }

}