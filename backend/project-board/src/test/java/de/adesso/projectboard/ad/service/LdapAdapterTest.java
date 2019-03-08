package de.adesso.projectboard.ad.service;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.ldap.support.LdapUtils.emptyLdapName;

@RunWith(MockitoJUnitRunner.class)
public class LdapAdapterTest {

    private final String ID_ATTR = "sAMAccountName";

    @Mock
    private LdapTemplate ldapTemplateMock;

    @Mock
    private LdapConfigurationProperties ldapPropertiesMock;

    private Clock clock;

    private LdapAdapter ldapAdapter;

    @Before
    public void setUp() {
        given(ldapPropertiesMock.getUserIdAttribute()).willReturn(ID_ATTR);

        Instant instant = Instant.parse("2018-01-01T13:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.ldapAdapter = new LdapAdapter(ldapTemplateMock, ldapPropertiesMock, clock);
    }

    @Test
    public void getThumbnailPhotos() {
        // given


        // when

        // then
    }

    @Test
    public void buildIdCriteriaReturnsExpectedCriteriaWithMultipleIds() {
        // given
        var userIds = List.of("A", "B", "C");
        var expectedCriteria = LdapQueryBuilder.query()
                .where(ID_ATTR).is("A")
                .or(ID_ATTR).is("B")
                .or(ID_ATTR).is("C");

        // when / then
        compareActualWithExpectedCriteria(userIds, expectedCriteria);
    }

    @Test
    public void buildIdCriteriaReturnsExpectedCriteriaWithSingleId() {
        // given
        var userIds = List.of("A");
        var expectedCriteria = LdapQueryBuilder.query()
                .where(ID_ATTR).is("A");

        // when / then
        compareActualWithExpectedCriteria(userIds, expectedCriteria);
    }

    @Test
    public void getActiveDirectoryTimestamp() {
        // given
        var dateTime = LocalDateTime.of(2019, 1, 1, 13, 37);
        var expectedTimestamp = Long.toString(131_908_234_200_000_000L);

        // when
        var actualTimeStamp = ldapAdapter.getActiveDirectoryTimestamp(dateTime);

        // then
        assertThat(actualTimeStamp).isEqualTo(expectedTimestamp);
    }

    private void compareActualWithExpectedCriteria(List<String> userIds, ContainerCriteria expectedCriteria) {
        // when
        var actualCriteria = ldapAdapter.buildIdCriteria(userIds);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualCriteria.filter()).isEqualTo(expectedCriteria.filter());
        softly.assertThat(actualCriteria.base()).isEqualTo(emptyLdapName());
        softly.assertThat(actualCriteria.countLimit()).isNull();

        softly.assertAll();
    }

}
