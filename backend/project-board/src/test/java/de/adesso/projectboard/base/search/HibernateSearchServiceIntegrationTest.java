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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("JpaQlInspection")
@RunWith(SpringRunner.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@TestPropertySource("classpath:application-persistence-test.properties")
@DataJpaTest
public class HibernateSearchServiceIntegrationTest {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private UserRepository userRepository;

    private HibernateSearchService hibernateSearchService;

    @Before
    public void setUp() {
        var hibernateSearchService = new HibernateSearchService(Set.of("offen", "open"), Set.of("closed", "abgeschlossen"));
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.indexExistingEntities(entityManager);

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
    public void searchProjectsNonPaginatedRespectsPageSizeOfRequest() {
        // given
        var hibernateSearchService = new HibernateSearchService(Set.of(), Set.of());
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.indexExistingEntities(entityManager);

        var simpleQuery = "Location";
        var pageable = PageRequest.of(0, 5);

        // when
        var actualProjectsPage = hibernateSearchService.searchProjects(simpleQuery, pageable, null);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualProjectsPage.getTotalPages()).isEqualTo(2L);
        softly.assertThat(actualProjectsPage.getTotalElements()).isEqualTo(10L);

        softly.assertAll();
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedFindsAllProjectsWhenNoExclusionsAndLobConstraintsSet() {
        // given
        var hibernateSearchService = new HibernateSearchService(Set.of(), Set.of());
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.indexExistingEntities(entityManager);

        var simpleQuery = "Location";
        var expectedProjects = findProjectByIds("STF-1", "STF-2", "STF-3", "STF-4", "STF-5", "STF-6",
                "STF-7", "STF-8", "STF-9", "STF-10");

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, null);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedFindsAllProjectsWhenNoExclusionsAndLobConstraintsSet() {
        // given
        var hibernateSearchService = new HibernateSearchService(Set.of(), Set.of());
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.indexExistingEntities(entityManager);

        var simpleQuery = "Location";
        var expectedProjects = findProjectByIds("STF-1", "STF-2", "STF-3", "STF-4", "STF-5", "STF-6",
                "STF-7", "STF-8", "STF-9", "STF-10");

        var pageable = PageRequest.of(0, 10);

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, pageable, null);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedFindsProjectsFromNoLobWhenGivenLobNull() {
        // given
        var excludedStatus = Set.of("eskaliert", "escalated", "abgeschlossen");
        var lobConstraintsStatus = Set.of("offen", "open");

        var hibernateSearchService = new HibernateSearchService(lobConstraintsStatus, excludedStatus);
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.indexExistingEntities(entityManager);

        var simpleQuery = "Location";
        var expectedProjects = findProjectByIds("STF-4", "STF-7");

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, null);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedFindsProjectsFromNoLobWhenGivenLobNull() {
        // given
        var excludedStatus = Set.of("eskaliert", "escalated", "abgeschlossen");
        var lobConstraintsStatus = Set.of("offen", "open");

        var hibernateSearchService = new HibernateSearchService(lobConstraintsStatus, excludedStatus);
        hibernateSearchService.entityManager = entityManager;
        hibernateSearchService.indexExistingEntities(entityManager);

        var simpleQuery = "Location";
        var expectedProjects = findProjectByIds("STF-4", "STF-7");

        var pageable = PageRequest.of(0, 2);

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, pageable, null);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedFindsProjectsWithSameOrNoLobWithoutExcludedStatus() {
        // given
        var simpleQuery = "Location";
        var lob = "LOB Prod";
        var expectedProjects = findProjectByIds("STF-1", "STF-3", "STF-4", "STF-5", "STF-7", "STF-9", "STF-10");

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, lob);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedFindsProjectsWithSameOrNoLobWithoutExcludedStatus() {
        // given
        var lob = "LOB Prod";
        var expectedProjects = findProjectByIds("STF-1", "STF-3", "STF-4", "STF-5", "STF-7", "STF-9", "STF-10");
        var simpleQuery = "Location";
        var pageable = PageRequest.of(0, 10);

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, pageable, lob);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedWithPhraseQueryFindsCorrectLobsAndStatusRespectingExcludes() {
        // given
        var lob = "LOB Prod";
        var simpleQuery = "\"extraordinary\" | \"mockito\" | \"spring\"";
        var expectedProjects = findProjectByIds("STF-3", "STF-9");

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, lob);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedUsesFuzzyAndPrefixAsWellAndFindsCorrectLobsAndStatusRespectingExcludes() {
        // given
        var lob = "LOB Test";
        var simpleQuery = "exrtaordinary | spri | jaava";
        var expectedProjects = findProjectByIds("STF-1", "STF-3", "STF-8");

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, lob);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsNonPaginatedHasAndAsDefaultOperatorAndFindsCorrectLobsAndStatusRespectingExcludes() {
        // given
        var lob = "LOB Test";
        var simpleQuery = "extraordinary description";
        var expectedProjects = findProjectByIds("STF-8");

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, lob);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedWithPhraseQueryFindsExactMatchesAndFindsCorrectLobsAndStatusRespectingExcludes() {
        // given
        var lob = "LOB Test";
        var simpleQuery = "\"extraordinary\" | \"mockito\" | \"spring\"";
        var pageable = PageRequest.of(1, 1);
        var expectedProjects = findProjectByIds("STF-3", "STF-8");

        // when
        var actualProjectPage = hibernateSearchService.searchProjects(simpleQuery, pageable, lob);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualProjectPage.getTotalElements()).isEqualTo(2L);
        softly.assertThat(actualProjectPage.getTotalPages()).isEqualTo(2L);
        softly.assertThat(actualProjectPage.getContent()).containsAnyElementsOf(expectedProjects);

        softly.assertAll();
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedUsesFuzzyAndPrefixAsWellAndFindsCorrectLobsAndStatusRespectingExcludes() {
        // given
        var lob = "LOB Test";
        var simpleQuery = "exrtaordinary | spri | javva";
        var pageable = PageRequest.of(1, 1);
        var expectedProjects = findProjectByIds("STF-1", "STF-3", "STF-8");

        // when
        var actualProjectPage = hibernateSearchService.searchProjects(simpleQuery, pageable, lob);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualProjectPage.getTotalElements()).isEqualTo(3L);
        softly.assertThat(actualProjectPage.getTotalPages()).isEqualTo(3L);
        softly.assertThat(actualProjectPage.getContent()).containsAnyElementsOf(expectedProjects);

        softly.assertAll();
    }

    @Test
    @Sql(scripts = "classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void searchProjectsPaginatedHasAndAsDefaultOperatorAndFindsCorrectLobsAndStatusRespectingExcludes() {
        // given
        var lob = "LOB Test";
        var simpleQuery = "extraordinary description";
        var pageable = PageRequest.of(0, 1);
        var expectedProjects = findProjectByIds("STF-8");

        // when
        var actualProjectPage = hibernateSearchService.searchProjects(simpleQuery, pageable, lob);

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

        var retrievedProject = findProjectByIds("STF-1").toArray(Project[]::new)[0];

        // when
        var actualProjects = hibernateSearchService.searchProjects(simpleQuery, null);

        // then
        assertThat(actualProjects).containsExactly(retrievedProject);
    }

    private Set<Project> findProjectByIds(String... ids) {
        return Arrays.stream(ids)
                .distinct()
                .map(id -> entityManager.find(Project.class, id))
                .collect(Collectors.toSet());
    }

}

