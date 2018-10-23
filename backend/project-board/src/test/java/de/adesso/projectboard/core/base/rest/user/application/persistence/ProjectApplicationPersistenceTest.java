package de.adesso.projectboard.core.base.rest.user.application.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectApplicationPersistenceTest {

    @Autowired
    private ProjectApplicationRepository applicationRepo;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/rest/project/persistence/Projects.sql",
            "classpath:/de/adesso/projectboard/core/base/rest/user/persistence/Users.sql"
    })
    public void testSave() {

    }

}