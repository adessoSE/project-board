package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SuperUserPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    private Project project;

    @Before
    public void setUp() {
        this.project = new Project().setId("STF-1");
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/UserAccess.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Bookmarks.sql",
    })
    public void testSave_OK() {
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

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/CreatedProjects.sql"
    })
    public void testFindAllByCreatedProjectsContaining() {
        List<User> users = userRepository.findAllByCreatedProjectsContaining(project);

        assertEquals(1L, users.size());
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql"
    })
    public void testFindAllByBossEquals() {
        SuperUser superUser = (SuperUser) userRepository.findById("SuperUser2").get();

        List<User> employees = userRepository.findAllByBossEquals(superUser, Sort.unsorted());

        assertEquals(2L, employees.size());
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Bookmarks.sql"
    })
    public void testFindAllByBookmarksContaining() {
        List<User> users = userRepository.findAllByBookmarksContaining(project);

        assertEquals(2L, users.size());
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Bookmarks.sql",
    })
    public void testExistsByIdAndBookmarksContaining() {
        Project nonExistentProject = new Project()
            .setId("non-existent-project");

        assertFalse(userRepository.existsByIdAndBookmarksContaining("User2", project));
        assertFalse(userRepository.existsByIdAndBookmarksContaining("non-existent-user", project));
        assertFalse(userRepository.existsByIdAndBookmarksContaining("User2", nonExistentProject));

        assertTrue(userRepository.existsByIdAndBookmarksContaining("SuperUser2", project));
        assertTrue(userRepository.existsByIdAndBookmarksContaining("User1", project));
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql"
    })
    public void testExistsByIdAndBoss() {
        SuperUser boss1 = (SuperUser) userRepository.findById("SuperUser1").get();
        SuperUser boss2 = (SuperUser) userRepository.findById("SuperUser2").get();

        assertFalse(userRepository.existsByIdAndBoss("User1", boss1));
        assertTrue(userRepository.existsByIdAndBoss("SuperUser1", boss1));
        assertTrue(userRepository.existsByIdAndBoss("User1", boss2));
        assertTrue(userRepository.existsByIdAndBoss("User2", boss2));
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/CreatedProjects.sql"
    })
    public void testExistsByIdAndCreatedProjectsContaining() {
        assertFalse(userRepository.existsByIdAndCreatedProjectsContaining("SuperUser2", project));
        assertTrue(userRepository.existsByIdAndCreatedProjectsContaining("SuperUser1", project));
    }

}