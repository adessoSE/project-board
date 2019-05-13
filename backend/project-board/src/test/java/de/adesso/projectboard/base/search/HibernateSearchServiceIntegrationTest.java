package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@TestPropertySource("classpath:application-persistence-test.properties")
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = HibernateSimpleQueryUtils.class
        )
)
public class HibernateSearchServiceIntegrationTest {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HibernateSimpleQueryUtils hibernateSimpleQueryUtils;

    private HibernateSearchService hibernateSearchService;

    @Before
    public void setUp() {
        var hibernateSearchService = new HibernateSearchService(hibernateSimpleQueryUtils);
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.initialize(entityManager);

        this.hibernateSearchService = hibernateSearchService;
    }

    @After
    public void tearDown() {
        this.projectRepository.deleteAll();
        this.userDataRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedWithPhraseQueryFindsExactMatches() {
        // given
        var simpleQuery = "\"extraordinary\" | \"mockito\" | \"spring\"";
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
    public void searchProjectsNonPaginatedUsesFuzzyAndPrefixAsWell() {
        // given
        var simpleQuery = "exrtaordinary | spri | jaava";
        var status = Set.of("eskaliert", "open", "offen");
        var expectedProjects =
                entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                                "WHERE p.id = 'STF-3' OR p.id = 'STF-8' OR p.id = 'STF-1'",
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
    public void searchProjectsPaginatedWithPhraseQueryFindsExactMatches() {
        // given
        var simpleQuery = "\"extraordinary\" | \"mockito\" | \"spring\"";
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
    public void searchProjectsPaginatedUsesFuzzyAndPrefixAsWell() {
        // given
        var simpleQuery = "exrtaordinary | spri | javva";
        var status = Set.of("eskaliert", "open", "offen");
        var pageable = PageRequest.of(1, 1);
        var expectedProjects =
                entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                                "WHERE p.id = 'STF-3' OR p.id = 'STF-8' OR p.id = 'STF-1'",
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
    public void searchUserDataWithPhraseQueryFindsExactMatches() {
        // given
        var simpleQuery = "\"first\" | \"third\"";
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
    public void searchUserDataUsesFuzzyAndPrefixAsWell() {
        // given
        var simpleQuery = "frist | thi";
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

    @Test
    public void searchProjectsFindsNewlyAddedEntities() {
        // given
        var simpleQuery = "spring";

        var updateCreateTime = LocalDateTime.of(2019, 4, 9, 14, 9);
        var projectStatus = "eskaliert";
        var projectId = "STF-1";
        var project = new Project(projectId, projectStatus, "Type", "Searching Spring Expert", List.of(), "Job", "Skills", "Description",
                "LoB", "Customer", "Location", "Start", "End", "Effort", updateCreateTime, updateCreateTime, "Freelancer",
                "Elongation", "Other", "dailyRate", "travelCostsCompensated");
        projectRepository.save(project);

        var retrievedProject = entityManager.createQuery("SELECT p FROM de.adesso.projectboard.base.project.persistence.Project AS p " +
                "WHERE p.id = '" + projectId + "'", Project.class)
                .getResultList().get(0);

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, Set.of("eskaliert"));

        // then
        assertThat(actualProjects).containsExactly(retrievedProject);
    }

}

