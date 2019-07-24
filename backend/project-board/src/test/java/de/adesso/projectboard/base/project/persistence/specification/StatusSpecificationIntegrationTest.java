package de.adesso.projectboard.base.project.persistence.specification;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class StatusSpecificationIntegrationTest {

    private static final Set<String> EXCLUDED_STATUS = Set.of("abgeschlossen", "closed");

    private static final Set<String> LOB_DEPENDENT_STATUS = Set.of("offen", "open");

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void statusSpecificationReturnsAllProjectsWhenNoLobDependentAndExcludedStatusGiven() {
        // given
        var specification = new StatusSpecification(Set.of(), Set.of(), null);

        var expectedProjectIds = List.of("STF-1", "STF-2", "STF-3", "STF-4", "STF-5", "STF-6",
                "STF-7", "STF-8", "STF-9", "STF-10");

        // when / then
        evaluateSpecification(specification, expectedProjectIds);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void statusSpecificationReturnsNoProjectsWithExcludedStatus() {
        // given
        var userLob = "LOB Test";
        var specification = new StatusSpecification(EXCLUDED_STATUS, Set.of(), userLob);

        var expectedProjectIds = List.of("STF-1", "STF-3", "STF-4", "STF-5", "STF-7", "STF-8", "STF-9", "STF-10");

        // when / then
        evaluateSpecification(specification, expectedProjectIds);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void statusSpecificationReturnsProjectsOnlyWithLobNullOrLobEqualsWhenStatusIsLobDependent() {
        // given
        var userLob = "LOB Test";
        var specification = new StatusSpecification(Set.of(), LOB_DEPENDENT_STATUS, userLob);

        var expectedProjectIds = List.of("STF-1", "STF-2", "STF-3", "STF-4", "STF-5", "STF-6", "STF-7", "STF-8");

        // when / then
        evaluateSpecification(specification, expectedProjectIds);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void statusSpecificationReturnsProjectsOnlyWithLobNullOrLobEqualsWhenStatusIsLobDependentWithoutExcludedStatus() {
        // given
        var userLob = "LOB Prod";
        var specification = new StatusSpecification(EXCLUDED_STATUS, LOB_DEPENDENT_STATUS, userLob);

        var expectedProjectIds = List.of("STF-1", "STF-3", "STF-4", "STF-5", "STF-7", "STF-9", "STF-10");

        // when / then
        evaluateSpecification(specification, expectedProjectIds);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void statusSpecificationReturnsProjectsOnlyWithLobNullWhenStatusIsLobDependentAndUserLobNullWithoutExcludedStatus() {
        // given
        var specification = new StatusSpecification(EXCLUDED_STATUS, LOB_DEPENDENT_STATUS, null);

        var expectedProjectIds = List.of("STF-1", "STF-3", "STF-4", "STF-5", "STF-7");

        // when / then
        evaluateSpecification(specification, expectedProjectIds);
    }

    private void evaluateSpecification(Specification<Project> specification, List<String> expectedProjectIds) {
        // given
        var expectedProjects = projectRepository.findAllById(expectedProjectIds);

        // when
        var actualProjects = projectRepository.findAll(specification);

        // then
        assertThat(actualProjects).containsExactlyInAnyOrderElementsOf(expectedProjects);
    }

}
