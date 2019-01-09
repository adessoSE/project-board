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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class OrganizationStructurePersistenceTest {

    @Autowired
    private OrganizationStructureRepository structureRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        User user = userRepo.findById("User1").orElseThrow();
        User manager = userRepo.findById("User2").orElseThrow();
        Set<User> staff = new HashSet<>(userRepo.findAllById(Arrays.asList("User3", "User4")));

        OrganizationStructure expectedStructure = new OrganizationStructure(user, manager, staff, true);

        // when
        OrganizationStructure persistedStructure = structureRepo.save(expectedStructure);
        OrganizationStructure actualStructure = structureRepo.findById(persistedStructure.getId()).orElseThrow();

        // then
        expectedStructure.setId(persistedStructure.getId());
        assertThat(actualStructure).isEqualTo(expectedStructure);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void findByUserReturnsStructureForUserWhenPresent() {
        // given
        User user = userRepo.findById("User1").orElseThrow();
        OrganizationStructure expectedStructure = structureRepo.findById(1L).orElseThrow();

        // when
        OrganizationStructure actualStructure = structureRepo.findByUser(user).orElseThrow();

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void findByUserReturnsNoStructureForUserWhenNoneIsPresent() {
        // given
        User user = userRepo.findById("User2").orElseThrow();

        // when
        Optional<OrganizationStructure> structureOptional = structureRepo.findByUser(user);

        // then
        assertThat(structureOptional).isNotPresent();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserReturnsTrueWhenStructureIsPresent() {
        // given
        User user = userRepo.findById("User1").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUser(user);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserReturnsFalseWhenNoStructureIsPresent() {
        // given
        User user = userRepo.findById("User2").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUser(user);

        // then
        assertThat(actualExists).isFalse();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserAndManagingUserReturnsTrueWhenStructurePresentAndValueMatches() {
        // given
        User user = userRepo.findById("User1").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUserAndManagingUser(user, true);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserAndManagingUserReturnsFalseWhenStructurePresentButValueNotMatches() {
        // given
        User user = userRepo.findById("User1").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUserAndManagingUser(user, false);

        // then
        assertThat(actualExists).isFalse();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserAndManagingUserReturnsFalseWhenNotStructurePresent() {
        // given
        User user = userRepo.findById("User2").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUserAndManagingUser(user, true);

        // then
        assertThat(actualExists).isFalse();
    }


    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void findAllByUserInReturnsStructuresForUserWherePresent() {
        // given
        User first = userRepo.findById("User1").orElseThrow();
        User second = userRepo.findById("User2").orElseThrow();

        OrganizationStructure expectedStructure = structureRepo.findByUser(first).orElseThrow();

        // when
        List<OrganizationStructure> allByUserIn = structureRepo.findAllByUserIn(Arrays.asList(first, second));

        // then
        assertThat(allByUserIn).containsExactly(expectedStructure);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserAndStaffMemberContainingReturnsTrueWhenStructurePresentAndContainsUser() {
        // given
        User user = userRepo.findById("User1").orElseThrow();
        User staffMember = userRepo.findById("User3").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUserAndStaffMembersContaining(user, staffMember);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserAndStaffMemberContainingReturnsFalseWhenStructurePresentAndButDoesNotContainUser() {
        // given
        User user = userRepo.findById("User1").orElseThrow();
        User staffMember = userRepo.findById("User5").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUserAndStaffMembersContaining(user, staffMember);

        // then
        assertThat(actualExists).isFalse();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/OrgStructure.sql"
    })
    public void existsByUserAndStaffMemberContainingReturnsFalseWhenStructureNotPresent() {
        // given
        User user = userRepo.findById("User3").orElseThrow();
        User staffMember = userRepo.findById("User5").orElseThrow();

        // when
        boolean actualExists = structureRepo.existsByUserAndStaffMembersContaining(user, staffMember);

        // then
        assertThat(actualExists).isFalse();
    }

}