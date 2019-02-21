package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-persistence-test.properties")
@DataJpaTest
public class HibernateSearchServiceIntegrationTest {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    private HibernateSearchService hibernateSearchService;

    @Before
    public void setUp() {
        var hibernateSearchService = new HibernateSearchService();
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.initialize(entityManager);

        this.hibernateSearchService = hibernateSearchService;
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginated() {
        // given
        var simpleQuery = "extraordinary | mockito | spring";
        var status = Set.of("eskaliert", "open", "offen");
        var expectedProjects =
                entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                                "WHERE p.id = 'STF-3' OR p.id = 'STF-8' OR p.id = 'STF-9'",
                        Project.class).getResultList();

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, status);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedHasAndAsDefaultOperator() {
        // given
        var simpleQuery = "extraordinary description";
        var status = Set.of("offen");
        var expectedProjects =
                entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                                "WHERE p.id = 'STF-8'",
                        Project.class).getResultList();

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, status);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginated() {
        // given
        var simpleQuery = "extraordinary | mockito | spring";
        var status = Set.of("eskaliert", "open", "offen");
        var pageable = PageRequest.of(1, 1);
        var expectedProjects =
                entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                                "WHERE p.id = 'STF-3' OR p.id = 'STF-8' OR p.id = 'STF-9'",
                        Project.class).getResultList();

        // when
        var actualProjectPage = hibernateSearchService.searchProjects(simpleQuery, status, pageable);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualProjectPage.getTotalElements()).isEqualTo(3L);
        softly.assertThat(actualProjectPage.getTotalPages()).isEqualTo(3L);
        softly.assertThat(actualProjectPage.getContent()).containsAnyElementsOf(expectedProjects);

        softly.assertAll();
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedHasAndAsDefaultOperator() {
        // given
        var simpleQuery = "extraordinary description";
        var status = Set.of("offen");
        var pageable = PageRequest.of(0, 1);
        var expectedProjects =
                entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                                "WHERE p.id = 'STF-8'",
                        Project.class).getResultList();

        // when
        var actualProjectPage = hibernateSearchService.searchProjects(simpleQuery, status, pageable);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualProjectPage.getTotalElements()).isEqualTo(1L);
        softly.assertThat(actualProjectPage.getTotalPages()).isEqualTo(1L);
        softly.assertThat(actualProjectPage.getContent()).containsAnyElementsOf(expectedProjects);

        softly.assertAll();
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Users.sql"),
            @Sql(scripts = "classpath:de/adesso/projectboard/persistence/UserData.sql")
    })
    public void searchUserData() {
        // given
        var simpleQuery = "first | third";
        var givenUsers = entityManager.createQuery("SELECT u FROM de.adesso.projectboard.base.user.persistence.User AS u " +
                "WHERE u.id LIKE 'User%'", User.class).getResultList();
        var expectedData = entityManager.createQuery("SELECT d FROM de.adesso.projectboard.base.user.persistence.data.UserData AS d " +
                "WHERE d.id = 1 OR d.id = 3", UserData.class).getResultList();

        // when
        var actualData = hibernateSearchService.searchUserData(givenUsers, simpleQuery);

        // then
        assertThat(actualData).containsExactlyInAnyOrderElementsOf(expectedData);
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Users.sql"),
            @Sql(scripts = "classpath:de/adesso/projectboard/persistence/UserData.sql")
    })
    public void searchUserDataHasAndAsDefaultOperator() {
        // given
        var simpleQuery = "first user";
        var givenUsers = entityManager.createQuery("SELECT u FROM de.adesso.projectboard.base.user.persistence.User AS u " +
                "WHERE u.id LIKE 'User%'", User.class).getResultList();
        var expectedData = entityManager.createQuery("SELECT d FROM de.adesso.projectboard.base.user.persistence.data.UserData AS d " +
                "WHERE d.id = 1", UserData.class).getResultList();

        // when
        var actualData = hibernateSearchService.searchUserData(givenUsers, simpleQuery);

        // then
        assertThat(actualData).containsExactlyInAnyOrderElementsOf(expectedData);
    }

}
