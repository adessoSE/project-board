package de.adesso.projectboard.ldap.service.mapper;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserDataMapperTest {

    private final String ID_ATTR = "sAMAccountName";

    private final String EMAIL_ATTR = "mail";

    private final String PRINCIPAL_NAME_ATTR = "userPrincipalName";

    private final String PICTURE_ATTR = "thumbnailPhoto";

    private final String FULL_NAME_ATTR = "name";

    private final String GIVEN_NAME_ATTR = "givenName";

    private final String DIVISION_ATTR = "division";

    @Mock
    private Attributes attributesMock;

    @Mock
    private Attribute userIdAttributeMock;

    @Mock
    private Attribute fullNameAttributeMock;

    @Mock
    private Attribute firstNameAttributeMock;

    @Mock
    private Attribute divisionAttributeMock;

    @Mock
    private Attribute emailAttributeMock;

    @Mock
    private Attribute pictureAttributeMock;

    @Mock
    private Attribute userPrincipalNameAttributeMock;

    @Mock
    private User userMock;

    private UserDataMapper userDataMapper;

    @Before
    public void setUp() {
        this.userDataMapper = new UserDataMapper(Collections.singletonList(userMock), ID_ATTR);
    }

    @Test
    public void mapFromAttributesReturnsUserDataWithAllAttributesWhenPresent() throws NamingException {
        // given
        var expectedUserId = "user-id";
        var expectedFirstName = "Test";
        var expectedLastName = "Person";
        var expectedLob = "LoB Test";
        var expectedEmail = "test@email.com";
        var expectedPicture = new byte[] {2, 68, 43};
        var expectedUserData = new UserData(userMock, expectedFirstName, expectedLastName, expectedEmail, expectedLob, expectedPicture);
        var fullName = String.format("%s, %s", expectedLastName, expectedFirstName);

        setUpAttributesMockWithRequiredAttributes(expectedUserId, expectedFirstName, fullName, expectedLob);

        given(userMock.getId()).willReturn(expectedUserId);

        given(attributesMock.get(EMAIL_ATTR)).willReturn(emailAttributeMock);
        given(attributesMock.get(PICTURE_ATTR)).willReturn(pictureAttributeMock);

        given(emailAttributeMock.get()).willReturn(expectedEmail);
        given(pictureAttributeMock.get()).willReturn(expectedPicture);

        // when / then
        compareMapFromAttributesWithExpectedUserData(attributesMock, expectedUserData);
    }

    @Test
    public void mapFromAttributesReturnsUserDataWithNoPictureWhenNotPresent() throws NamingException {
        // given
        var expectedUserId = "user-id";
        var expectedFirstName = "Test";
        var expectedLastName = "Person";
        var expectedLob = "LoB Test";
        var expectedEmail = "test@email.com";
        var expectedUserData = new UserData(userMock, expectedFirstName, expectedLastName, expectedEmail, expectedLob, null);
        var fullName = String.format("%s, %s", expectedLastName, expectedFirstName);

        setUpAttributesMockWithRequiredAttributes(expectedUserId, expectedFirstName, fullName, expectedLob);

        given(userMock.getId()).willReturn(expectedUserId);

        given(attributesMock.get(EMAIL_ATTR)).willReturn(emailAttributeMock);
        given(emailAttributeMock.get()).willReturn(expectedEmail);

        // when / then
        compareMapFromAttributesWithExpectedUserData(attributesMock, expectedUserData);
    }

    @Test
    public void mapFromAttributesReturnsUserDataWithUserPrincipalAsEmailWhenNotSet() throws NamingException {
        // given
        var expectedUserId = "user-id";
        var expectedFirstName = "New";
        var expectedLastName = "Employee";
        var expectedLob = "LoB Test";
        var expectedEmail = "test@email.com";
        var expectedUserData = new UserData(userMock, expectedFirstName, expectedLastName, expectedEmail, expectedLob, null);
        var fullName = String.format("%s, %s", expectedLastName, expectedFirstName);

        setUpAttributesMockWithRequiredAttributes(expectedUserId, expectedFirstName, fullName, expectedLob);

        given(userMock.getId()).willReturn(expectedUserId);

        given(attributesMock.get(PRINCIPAL_NAME_ATTR)).willReturn(userPrincipalNameAttributeMock);
        given(userPrincipalNameAttributeMock.get()).willReturn(expectedEmail);

        // when / then
        compareMapFromAttributesWithExpectedUserData(attributesMock, expectedUserData);
    }

    @Test
    public void mapFromAttributesReturnsUserDataWithAllAttributesWhenPresentButNamePatternDiffers() throws NamingException {
        // given
        var expectedUserId = "user-id";
        var expectedFirstName = "Test";
        var expectedLastName = "Person";
        var expectedLob = "LoB Test";
        var expectedEmail = "test@email.com";
        var expectedUserData = new UserData(userMock, expectedFirstName, expectedLastName, expectedEmail, expectedLob, null);
        var fullName = String.format("%s %s", expectedFirstName, expectedLastName);

        setUpAttributesMockWithRequiredAttributes(expectedUserId, expectedFirstName, fullName, expectedLob);

        given(userMock.getId()).willReturn(expectedUserId);

        given(attributesMock.get(EMAIL_ATTR)).willReturn(emailAttributeMock);
        given(emailAttributeMock.get()).willReturn(expectedEmail);

        // when / then
        compareMapFromAttributesWithExpectedUserData(attributesMock, expectedUserData);
    }

    @Test
    public void mapFromAttributesThrowsExceptionWhenUserNotPresent() throws NamingException {
        // given
        var expectedUserId = "user-id";
        var expectedFirstName = "Test";
        var expectedLastName = "Person";
        var expectedLob = "LoB Test";
        var expectedEmail = "test@email.com";
        var expectedUserData = new UserData(userMock, expectedFirstName, expectedLastName, expectedEmail, expectedLob, null);
        var fullName = String.format("%s %s", expectedFirstName, expectedLastName);

        setUpAttributesMockWithRequiredAttributes(expectedUserId, expectedFirstName, fullName, expectedLob);

        given(userMock.getId()).willReturn("other-user-id");

        given(attributesMock.get(EMAIL_ATTR)).willReturn(emailAttributeMock);
        given(emailAttributeMock.get()).willReturn(expectedEmail);

        // when
        assertThatThrownBy(() -> userDataMapper.mapFromAttributes(attributesMock))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void extractLastNameReturnsLastNameWithLastNameCommaFirstNamePattern() {
        // given
        var expectedLastName = "Person";
        var givenName = "Test";
        var fullName = String.format("%s, %s", expectedLastName, givenName);

        // when / then
        compareExtractLastNameWithExpectedLastName(givenName, fullName, expectedLastName);
    }

    @Test
    public void extractLastNameReturnsLastNameWithFirstNameLastNamePattern() {
        // given
        var expectedLastName = "Person";
        var givenName = "Test";
        var fullName = String.format("%s %s", givenName, expectedLastName);

        // when / then
        compareExtractLastNameWithExpectedLastName(givenName, fullName, expectedLastName);
    }

    @Test
    public void getUserFromUsersByIdReturnsUserWhenPresent() {
        // given
        var expectedUserId = "user-id";

        given(userMock.getId()).willReturn(expectedUserId);

        // when
        var actualUser = userDataMapper.getUserFromUsersById(expectedUserId);

        // then
        assertThat(actualUser).isEqualTo(userMock);
    }

    @Test
    public void getUserFromUsersByIdThrowsExceptionWhenUserNotPresent() {
        // given
        var expectedUserId = "user-id";

        given(userMock.getId()).willReturn("other-user-id");

        // when
        assertThatThrownBy(() -> userDataMapper.getUserFromUsersById(expectedUserId))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void getPictureReturnsNullWhenPictureNotSet() throws NamingException {
        // given

        // when / then
        compareGetPictureWithExpectedPicture(null);
    }

    @Test
    public void getPictureReturnsPictureWhenPictureSet() throws NamingException {
        // given
        var expectedPicture = new byte[] {0, -29, 102, 20, -62};

        given(attributesMock.get(PICTURE_ATTR)).willReturn(pictureAttributeMock);
        given(pictureAttributeMock.get()).willReturn(expectedPicture);

        // when / then
        compareGetPictureWithExpectedPicture(expectedPicture);
    }

    @Test
    public void getEmailReturnsPlaceholderWhenMailAndPrincipalNameNotSet() throws NamingException {
        // given
        var expectedEmail = "placeholder";

        // when / then
        compareGetEmailWithExpectedEmail(expectedEmail);
    }

    @Test
    public void getEmailReturnsMailWhenMailSet() throws NamingException {
        // given
        var expectedEmail = "test@email.com";

        given(attributesMock.get(EMAIL_ATTR)).willReturn(emailAttributeMock);
        given(emailAttributeMock.get()).willReturn(expectedEmail);

        // when / then
        compareGetEmailWithExpectedEmail(expectedEmail);
    }

    @Test
    public void getEmailReturnsPrincipalNameWhenMailNotSet() throws NamingException {
        // given
        var expectedEmail = "test.mail@mail.com";

        given(attributesMock.get(PRINCIPAL_NAME_ATTR)).willReturn(userPrincipalNameAttributeMock);
        given(userPrincipalNameAttributeMock.get()).willReturn(expectedEmail);

        // when / then
        compareGetEmailWithExpectedEmail(expectedEmail);
    }

    public void setUpAttributesMockWithRequiredAttributes(String userId, String firstName, String fullName, String lob) throws NamingException {
        // given
        given(attributesMock.get(ID_ATTR)).willReturn(userIdAttributeMock);
        given(attributesMock.get(FULL_NAME_ATTR)).willReturn(fullNameAttributeMock);
        given(attributesMock.get(GIVEN_NAME_ATTR)).willReturn(firstNameAttributeMock);
        given(attributesMock.get(DIVISION_ATTR)).willReturn(divisionAttributeMock);

        given(userIdAttributeMock.get()).willReturn(userId);
        given(fullNameAttributeMock.get()).willReturn(fullName);
        given(firstNameAttributeMock.get()).willReturn(firstName);
        given(divisionAttributeMock.get()).willReturn(lob);
    }

    public void compareGetEmailWithExpectedEmail(String expectedEmail) throws NamingException {
        // when
        var actualEmail = userDataMapper.getEmail(attributesMock);

        // then
        assertThat(actualEmail).isEqualTo(expectedEmail);
    }

    public void compareGetPictureWithExpectedPicture(byte[] expectedPicture) throws NamingException {
        // when
        var actualPicture = userDataMapper.getPicture(attributesMock);

        // then
        assertThat(actualPicture).isEqualTo(expectedPicture);
    }

    public void compareExtractLastNameWithExpectedLastName(String givenName, String fullName, String expectedLastName) {
        // when
        var actualLastName = userDataMapper.extractLastName(givenName, fullName);

        // then
        assertThat(actualLastName).isEqualTo(expectedLastName);
    }

    public void compareMapFromAttributesWithExpectedUserData(Attributes attributes, UserData expectedUserData) throws NamingException {
        // when
        var actualUserData = userDataMapper.mapFromAttributes(attributes);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);
    }

}