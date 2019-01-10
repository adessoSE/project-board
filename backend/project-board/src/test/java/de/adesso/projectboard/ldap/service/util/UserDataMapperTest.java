package de.adesso.projectboard.ldap.service.util;

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
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserDataMapperTest {

    private final String ID_ATTR = "sAMAccountName";

    private final String MAIL_ATTR = "mail";

    private final String PRINCIPAL_NAME_ATTR = "userPrincipalName";

    private final String PICTURE_ATTR = "thumbnailPhoto";

    @Mock
    private Attributes attributesMock;

    @Mock
    private Attribute userIdAttributeMock;

    @Mock
    private Attribute fullNameAttributeMock;

    @Mock
    private Attribute firstNameAttributeMock;

    @Mock
    private Attribute lobAttributeMock;

    @Mock
    private Attribute mailAttributeMock;

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
    public void mapFromAttributesReturnsUserDataWithAllAttributesWhenPresent() {
        // given
        var expectedUserId = "user-id";
        var expectedFirstName = "Test";
        var expectedLastName = "Person";
        var expectedLob = "LoB Test";
        var expectedEmail = "test@email.com";
        var expectedPicture = new byte[] {2, 68, 43};
        var expectedUserData = new UserData(userMock, expectedFirstName, expectedLastName, expectedEmail, expectedLob, expectedPicture);

        var fullName = String.format("%s, %s", expectedFirstName, expectedLastName);

        // when


        // then
    }

    @Test
    public void extractLastNameReturnsLastNameWithLastNameCommaFirstNamePattern() {
        // given
        var expectedLastName = "Person";
        var givenName = "Test";
        var fullName = String.format("%s, %s", expectedLastName, givenName);

        compareExtractLastNameWithExpectedLastName(givenName, fullName, expectedLastName);
    }

    @Test
    public void extractLastNameReturnsLastNameWithFirstNameLastNamePattern() {
        // given
        var expectedLastName = "Person";
        var givenName = "Test";
        var fullName = String.format("%s %s", givenName, expectedLastName);

        compareExtractLastNameWithExpectedLastName(givenName, fullName, expectedLastName);
    }

    @Test
    public void getPictureReturnsNullWhenPictureNotSet() throws NamingException {
        compareGetPictureWithExpectedPicture(null);
    }

    @Test
    public void getPictureReturnsPictureWhenPictureSet() throws NamingException {
        // given
        var expectedPicture = new byte[] {0, -29, 102, 20, -62};

        given(attributesMock.get(PICTURE_ATTR)).willReturn(pictureAttributeMock);
        given(pictureAttributeMock.get()).willReturn(expectedPicture);

        compareGetPictureWithExpectedPicture(expectedPicture);
    }

    @Test
    public void getEmailReturnsPlaceholderWhenMailAndPrincipalNameNotSet() throws NamingException {
        // given
        var expectedEmail = "placeholder";

        compareGetEmailWithExpectedEmail(expectedEmail);
    }

    @Test
    public void getEmailReturnsMailWhenMailSet() throws NamingException {
        // given
        var expectedEmail = "test@email.com";

        given(attributesMock.get(MAIL_ATTR)).willReturn(mailAttributeMock);
        given(mailAttributeMock.get()).willReturn(expectedEmail);

        compareGetEmailWithExpectedEmail(expectedEmail);
    }

    @Test
    public void getEmailReturnsPrincipalNameWhenMailNotSet() throws NamingException {
        // given
        var expectedEmail = "test.mail@mail.com";

        given(attributesMock.get(PRINCIPAL_NAME_ATTR)).willReturn(userPrincipalNameAttributeMock);
        given(userPrincipalNameAttributeMock.get()).willReturn(expectedEmail);

        compareGetEmailWithExpectedEmail(expectedEmail);
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

}