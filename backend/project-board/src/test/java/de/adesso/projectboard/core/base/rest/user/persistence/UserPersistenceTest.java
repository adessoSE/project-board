package de.adesso.projectboard.core.base.rest.user.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testSave_OK() {
        SuperUser firstUser = new SuperUser("first-user");
        firstUser.setFullName("First", "User");
        firstUser.setEmail("first.user@example.com");
        firstUser.setLob("LOB Test");

        User secondUser = new User("second-user", firstUser);
        secondUser.setFullName("Second", "User");
        secondUser.setEmail("second.user@example.com");
        secondUser.setLob("LOB Test");

        User thirdUser = new User("third-user", firstUser);
        thirdUser.setFullName("Third", "User");
        thirdUser.setEmail("third.user@example.com");
        thirdUser.setLob("LOB Test");

        userRepository.save(firstUser);
        assertEquals(3L, userRepository.count());

        Optional<User> retrievedUserOptional = userRepository.findById("first-user");
        assertTrue(retrievedUserOptional.isPresent());

        SuperUser retrievedUser = (SuperUser) retrievedUserOptional.get();

        assertEquals(3L, retrievedUser.getStaffMembers().size());
        assertEquals("first-user", retrievedUser.getId());
        assertEquals("First", retrievedUser.getFirstName());
        assertEquals("User", retrievedUser.getLastName());
        assertEquals("first.user@example.com", retrievedUser.getEmail());
        assertEquals("LOB Test", retrievedUser.getLob());
        assertEquals(retrievedUser, retrievedUser.getBoss());
        assertTrue(retrievedUser.getStaffMembers().contains(retrievedUser));
    }

}