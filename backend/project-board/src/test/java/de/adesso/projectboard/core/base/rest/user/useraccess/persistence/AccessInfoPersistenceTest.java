package de.adesso.projectboard.core.base.rest.user.useraccess.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccessInfoPersistenceTest {

    @Autowired
    private AccessInfoRepository infoRepo;

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/UserAccess.sql"
    })
    public void testSave() {
        Optional<AccessInfo> infoOptional = infoRepo.findById(3L);
        assertTrue(infoOptional.isPresent());

        AccessInfo info = infoOptional.get();

        assertNotNull(info.getUser());
        assertEquals("User2", info.getUser().getId());
        assertEquals(LocalDateTime.of(2018, 3, 1, 13, 37), info.getAccessStart());
        assertEquals(LocalDateTime.of(2018, 3, 2, 13, 37), info.getAccessEnd());
    }

}