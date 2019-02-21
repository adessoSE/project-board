package de.adesso.projectboard.base.user.persistence.data;

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

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class UserDataPersistenceTest {

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        var expectedUser = userRepo.findById("User1").orElseThrow();

        var expectedFirstName = "First";
        var expectedLastName = "Last";
        var expectedEmail = "Email";
        var expectedLob = "LOB";
        var expectedPicture = new byte[] {10, -3, 54, 20};

        var userData = new UserData(expectedUser, expectedFirstName, expectedLastName,
                expectedEmail, expectedLob, expectedPicture);

        // when
        var savedUserData = userDataRepo.save(userData);
        var retrievedUserData = userDataRepo.findById(savedUserData.getId()).orElseThrow(EntityNotFoundException::new);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(retrievedUserData.firstName).isEqualTo(expectedFirstName);
        softly.assertThat(retrievedUserData.lastName).isEqualTo(expectedLastName);
        softly.assertThat(retrievedUserData.email).isEqualTo(expectedEmail);
        softly.assertThat(retrievedUserData.lob).isEqualTo(expectedLob);
        softly.assertThat(retrievedUserData.picture).isEqualTo(expectedPicture);

        softly.assertAll();
    }

    @Test
    @Sql({
         "classpath:de/adesso/projectboard/persistence/Users.sql",
         "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void findByUser() {
        // given
        var firstUserId = "User1";
        var thirdUserId = "User4";

        var firstUser = userRepo.findById(firstUserId).orElseThrow();
        var thirdUser = userRepo.findById(thirdUserId).orElseThrow();

        // when
        var firstUserDataOptional = userDataRepo.findByUser(firstUser);
        var thirdUserDataOptional = userDataRepo.findByUser(thirdUser);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(firstUserDataOptional).isPresent();
        softly.assertThat(thirdUserDataOptional).isNotPresent();

        softly.assertAll();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void findByUserIn() {
        // given
        var firstUserId = "User1";
        var secondUserId = "User2";
        var thirdUserId = "User4";

        var firstUser = userRepo.findById(firstUserId).orElseThrow();
        var secondUser = userRepo.findById(secondUserId).orElseThrow();
        var thirdUser = userRepo.findById(thirdUserId).orElseThrow();

        var users = List.of(firstUser, secondUser, thirdUser);

        // when
        var userDataList = userDataRepo.findByUserIn(users, Sort.unsorted());

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
        var firstUserId = "User1";
        var thirdUserId = "User4";

        var firstUser = userRepo.findById(firstUserId).orElseThrow();
        var thirdUser = userRepo.findById(thirdUserId).orElseThrow();

        // when
        boolean userDataForFirstUserExists = userDataRepo.existsByUser(firstUser);
        boolean userDataForThirdUserExists = userDataRepo.existsByUser(thirdUser);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(userDataForFirstUserExists).isTrue();
        softly.assertThat(userDataForThirdUserExists).isFalse();

        softly.assertAll();
    }

}