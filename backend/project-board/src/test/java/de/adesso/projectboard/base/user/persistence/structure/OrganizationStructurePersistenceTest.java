package de.adesso.projectboard.base.user.persistence.structure;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class OrganizationStructurePersistenceTest {

    @Autowired
    OrganizationStructureRepository structureRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void testSave() {
        User user = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);
        User manager = userRepo.findById("User2").orElseThrow(EntityNotFoundException::new);
        Set<User> staff = new HashSet<>(userRepo.findAllById(Arrays.asList("User3", "User4")));

        OrganizationStructure structure = new OrganizationStructure(user, manager, staff, true);
        OrganizationStructure persistedStructure = structureRepo.save(structure);

        assertTrue(persistedStructure.isUserIsManager());
        assertEquals(user, persistedStructure.getUser());
        assertEquals(manager, persistedStructure.getManager());
        assertEquals(2, persistedStructure.getStaffMembers().size());
        assertTrue(persistedStructure.getStaffMembers().stream().anyMatch(member -> "User3".equals(member.getId())));
        assertTrue(persistedStructure.getStaffMembers().stream().anyMatch(member -> "User4".equals(member.getId())));
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void testFindByUser() {
        User first = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);
        User second = userRepo.findById("User2").orElseThrow(EntityNotFoundException::new);

        assertTrue(structureRepo.findByUser(first).isPresent());
        assertFalse(structureRepo.findByUser(second).isPresent());
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void testExistsByUser() {
        User first = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);
        User second = userRepo.findById("User2").orElseThrow(EntityNotFoundException::new);

        assertTrue(structureRepo.existsByUser(first));
        assertFalse(structureRepo.existsByUser(second));
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void testExistsByUserAndUserIsManager() {
        User first = userRepo.findById("User1").orElseThrow(EntityNotFoundException::new);

        assertTrue(structureRepo.existsByUserAndUserIsManager(first, true));
        assertFalse(structureRepo.existsByUserAndUserIsManager(first, false));
    }

}