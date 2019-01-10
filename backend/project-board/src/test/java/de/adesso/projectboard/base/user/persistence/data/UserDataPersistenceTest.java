package de.adesso.projectboard.base.user.persistence.data;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class UserDataPersistenceTest {

    @Autowired
    UserDataRepository userDataRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        User expectedUser = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);

        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedEmail = "Email";
        String expectedLob = "LOB";
        byte[] expectedPicture = Base64.getDecoder().decode("Test");

        UserData userData = new UserData(expectedUser, expectedFirstName, expectedLastName, expectedEmail, expectedLob, expectedPicture);

        // when
        UserData savedUserData = userDataRepo.save(userData);
        UserData retrievedUserData = userDataRepo.findById(savedUserData.getId()).orElseThrow(EntityNotFoundException::new);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(retrievedUserData.getFirstName()).isEqualTo(expectedFirstName);
        softly.assertThat(retrievedUserData.getLastName()).isEqualTo(expectedLastName);
        softly.assertThat(retrievedUserData.getEmail()).isEqualTo(expectedEmail);
        softly.assertThat(retrievedUserData.getLob()).isEqualTo(expectedLob);
        softly.assertThat(retrievedUserData.getPicture()).isEqualTo(expectedPicture);

        softly.assertAll();
    }

    @Test
    @Sql({
         "classpath:de/adesso/projectboard/persistence/Users.sql",
         "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void findByUser() {
        // given
        String firstUserId = "User1";
        String thirdUserId = "User3";

        User firstUser = userRepo.findById(firstUserId).orElseThrow(EntityExistsException::new);
        User thirdUser = userRepo.findById(thirdUserId).orElseThrow(EntityExistsException::new);

        // when
        Optional<UserData> firstUserDataOptional = userDataRepo.findByUser(firstUser);
        Optional<UserData> thirdUserDataOptional = userDataRepo.findByUser(thirdUser);

        // then
        assertThat(firstUserDataOptional).isPresent();
        assertThat(thirdUserDataOptional).isNotPresent();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void findByUserIn() {
        // given
        String firstUserId = "User1";
        String secondUserId = "User2";
        String thirdUserId = "User3";

        User firstUser = userRepo.findById(firstUserId).orElseThrow(EntityExistsException::new);
        User secondUser = userRepo.findById(secondUserId).orElseThrow(EntityExistsException::new);
        User thirdUser = userRepo.findById(thirdUserId).orElseThrow(EntityExistsException::new);

        List<User> userList = Arrays.asList(firstUser, secondUser, thirdUser);

        // when
        List<UserData> userDataList = userDataRepo.findByUserIn(userList, Sort.unsorted());

        // then
        assertThat(userDataList).hasSize(2);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void existsByUser() {
        // given
        String firstUserId = "User1";
        String thirdUserId = "User3";

        User firstUser = userRepo.findById(firstUserId).orElseThrow(EntityExistsException::new);
        User thirdUser = userRepo.findById(thirdUserId).orElseThrow(EntityExistsException::new);

        // when
        boolean userDataForFirstUserExists = userDataRepo.existsByUser(firstUser);
        boolean userDataForThirdUserExists = userDataRepo.existsByUser(thirdUser);

        // then
        assertThat(userDataForFirstUserExists).isTrue();
        assertThat(userDataForThirdUserExists).isFalse();
    }

}