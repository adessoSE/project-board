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
        SuperUser superUser = new SuperUser("user-1");
        superUser.setEmail("user.1@example.com");
        superUser.setFullName("First Test", "User");
        superUser.setLob("LOB Test");
        userRepository.save(superUser);

        User user = new User("user-2", superUser);
        user.setEmail("user.2@example.com");
        user.setFullName("Second Test", "User");
        user.setLob("LOB Test");
        userRepository.save(user);

        Optional<User> superUserOptional = userRepository.findById("user-1");
        Optional<User> userOptional = userRepository.findById("user-2");

        assertTrue(superUserOptional.isPresent());
        User savedSuperUser = superUserOptional.get();
        assertEquals("user-1", savedSuperUser.getId());
        assertEquals("First Test", savedSuperUser.getFirstName());
        assertEquals("User", savedSuperUser.getLastName());
        assertEquals("LOB Test", savedSuperUser.getLob());
        assertEquals(savedSuperUser, savedSuperUser.getBoss());
        assertEquals(2L, savedSuperUser.getStaffMembers().size());

        assertTrue(userOptional.isPresent());
        User savedUser = userOptional.get();
        assertEquals("user-2", savedUser.getId());
        assertEquals("Second Test", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
        assertEquals("LOB Test", savedUser.getLob());
        assertEquals(savedSuperUser, savedUser.getBoss());
        assertEquals(0L, savedUser.getStaffMembers().size());
    }

}