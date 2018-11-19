package de.adesso.projectboard.base.user.persistence.data;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityExistsException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

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
    public void testSave() {
        User user = userRepo.findById("User1").orElseThrow(EntityExistsException::new);

        UserData userData = new UserData(user, "Test", "User", "test.user@test.com", "LOB Test");
        UserData persistedData = userDataRepo.save(userData);

        assertNotNull(persistedData.getUser());
        assertEquals("User1", persistedData.getUser().getId());
        assertEquals("Test", persistedData.getFirstName());
        assertEquals("User", persistedData.getLastName());
        assertEquals("test.user@test.com", persistedData.getEmail());
        assertEquals("LOB Test", persistedData.getLob());
    }

    @Test
    @Sql({
         "classpath:de/adesso/projectboard/persistence/Users.sql",
         "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void testFindByUser() {
        User first = userRepo.findById("User1").orElseThrow(EntityExistsException::new);
        User third = userRepo.findById("User3").orElseThrow(EntityExistsException::new);

        assertTrue(userDataRepo.findByUser(first).isPresent());
        assertFalse(userDataRepo.findByUser(third).isPresent());
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void testFindByUserIn() {
        User first = userRepo.findById("User1").orElseThrow(EntityExistsException::new);
        User second = userRepo.findById("User2").orElseThrow(EntityExistsException::new);
        User third = userRepo.findById("User3").orElseThrow(EntityExistsException::new);

        List<UserData> userDataList = userDataRepo.findByUserIn(Arrays.asList(first, second, third), Sort.unsorted());

        assertEquals(2, userDataList.size());
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/UserData.sql"
    })
    public void testExistsByUser() {
        User first = userRepo.findById("User1").orElseThrow(EntityExistsException::new);
        User third = userRepo.findById("User3").orElseThrow(EntityExistsException::new);

        assertTrue(userDataRepo.existsByUser(first));
        assertFalse(userDataRepo.existsByUser(third));
    }

}