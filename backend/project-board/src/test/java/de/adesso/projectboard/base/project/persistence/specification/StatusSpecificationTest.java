package de.adesso.projectboard.base.project.persistence.specification;

import de.adesso.projectboard.base.project.persistence.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: implement

@RunWith(MockitoJUnitRunner.class)
public class StatusSpecificationTest {

    @Mock
    private Root<Project> rootMock;

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    private StatusSpecification statusSpecification;

    @Test
    public void allToLowerCase() {
        // given
        var givenStatus = List.of("OffEn", "offen", "OPEN");
        var expectedStatus = Set.of("offen", "open");

        setUp(givenStatus);

        // when
        var actualStatus = statusSpecification.allToLowerCase(givenStatus);

        // then
        assertThat(actualStatus).containsExactlyInAnyOrderElementsOf(expectedStatus);
    }

    @Test
    public void toPredicateReturnsPredicateContainingAllExpectedExpressions() {
        // given
        var givenStatus = Set.of("offen", "eskaliert");

        setUp(givenStatus);

        // when
        var actualPredicate = statusSpecification.toPredicate(rootMock, null, criteriaBuilderMock);

        // then
    }

    @Test
    public void toPredicateReturnsEmptyPredicateWhenNoStatusGiven() {
        // given
        setUp(List.of());

        // when
        var actualPredicate = statusSpecification.toPredicate(rootMock, null, criteriaBuilderMock);

        // then

    }

    private void setUp(Collection<String> status) {
        this.statusSpecification = new StatusSpecification(status);
    }

}
