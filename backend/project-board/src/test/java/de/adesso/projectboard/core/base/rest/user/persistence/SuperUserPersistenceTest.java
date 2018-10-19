package de.adesso.projectboard.core.base.rest.user.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SuperUserPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("classpath:de/adesso/projectboard/core/base/rest/user/persistence/Users.sql")
    public void testSave() {
        Optional<User> userOptional = userRepository.findById("SuperUser2");
        Optional<User> bossOptional = userRepository.findById("SuperUser1");
        assertTrue(userOptional.isPresent());
        assertTrue(bossOptional.isPresent());

        SuperUser user = (SuperUser) userOptional.get();
        SuperUser boss = (SuperUser) bossOptional.get();

        assertEquals(2L, user.getStaffMembers().size());
        assertEquals("SuperUser2", user.getId());
        assertEquals("Second Test", user.getFirstName());
        assertEquals("Super User", user.getLastName());
        assertEquals("secondtestsuperuser@user.com", user.getEmail());
        assertEquals("LOB Test", user.getLob());
        assertEquals(1L, user.getAccessInfoList().size());
        assertEquals(1L, user.getBookmarks().size());

        assertEquals(boss, user.getBoss());
        assertEquals(2L, boss.getStaffMembers().size());
        assertTrue(boss.getStaffMembers().contains(user));
        assertTrue(boss.getStaffMembers().contains(boss));
    }

}