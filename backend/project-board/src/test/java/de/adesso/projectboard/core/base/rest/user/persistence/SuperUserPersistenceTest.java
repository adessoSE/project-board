package de.adesso.projectboard.core.base.rest.user.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SuperUserPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSave() {
        SuperUser firstUser = new SuperUser("first-user");
        firstUser.setFullName("First", "User");
        firstUser.setEmail("first.user@example.com");
        firstUser.setLob("LOB Test");
        userRepository.save(firstUser);

        User secondUser = new User("second-user", firstUser);
        secondUser.setFullName("Second", "User");
        secondUser.setEmail("second.user@example.com");
        secondUser.setLob("LOB Test");
        userRepository.save(secondUser);

        User thirdUser = new User("third-user", firstUser);
        thirdUser.setFullName("Third", "User");
        thirdUser.setEmail("third.user@example.com");
        thirdUser.setLob("LOB Test");
        userRepository.save(thirdUser);

        assertEquals(3L, userRepository.count());

        // first user
        Optional<User> retrievedUserOptional = userRepository.findById("first-user");
        assertTrue(retrievedUserOptional.isPresent());

        SuperUser firstRetrievedUser = (SuperUser) retrievedUserOptional.get();

        assertEquals(3L, firstRetrievedUser.getStaffMembers().size());
        assertEquals("first-user", firstRetrievedUser.getId());
        assertEquals("First", firstRetrievedUser.getFirstName());
        assertEquals("User", firstRetrievedUser.getLastName());
        assertEquals("first.user@example.com", firstRetrievedUser.getEmail());
        assertEquals("LOB Test", firstRetrievedUser.getLob());
        assertEquals(firstRetrievedUser, firstRetrievedUser.getBoss());
        assertTrue(firstRetrievedUser.getStaffMembers().contains(firstRetrievedUser));

        // second user
        retrievedUserOptional = userRepository.findById("first-user");
        assertTrue(retrievedUserOptional.isPresent());

        User secondRetrievedUser = retrievedUserOptional.get();

        assertEquals("second-user", secondRetrievedUser.getId());
        assertEquals("Second", secondRetrievedUser.getFirstName());
        assertEquals("User", secondRetrievedUser.getLastName());
        assertEquals("second.user@example.com", secondRetrievedUser.getEmail());
        assertEquals("LOB Test", secondRetrievedUser.getLob());
        assertEquals(firstRetrievedUser, secondRetrievedUser.getBoss());
        assertTrue(secondRetrievedUser.getStaffMembers().contains(secondRetrievedUser));

        // third user
        retrievedUserOptional = userRepository.findById("first-user");
        assertTrue(retrievedUserOptional.isPresent());

        User thirdRetrievedUser = retrievedUserOptional.get();

        assertEquals("third-user", thirdRetrievedUser.getId());
        assertEquals("Third", thirdRetrievedUser.getFirstName());
        assertEquals("User", thirdRetrievedUser.getLastName());
        assertEquals("third.user@example.com", thirdRetrievedUser.getEmail());
        assertEquals("LOB Test", thirdRetrievedUser.getLob());
        assertEquals(firstRetrievedUser, thirdRetrievedUser.getBoss());
        assertTrue(thirdRetrievedUser.getStaffMembers().contains(thirdRetrievedUser));
    }

}