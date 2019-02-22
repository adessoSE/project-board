package de.adesso.projectboard.base.project.persistence.specification;

import de.adesso.projectboard.base.project.persistence.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StatusSpecificationTest {

    @Mock
    private Root<Project> rootMock;

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private Path<String> pathMock;

    @Mock
    private Predicate completePredicate;

    @Mock
    private Predicate firstPredicateMock;

    @Mock
    private Predicate secondPredicateMock;

    @Test
    public void allToLowerCase() {
        // given
        var statusSpecification = new StatusSpecification(Set.of());
        var givenStatus = List.of("OffEn", "offen", "OPEN");
        var expectedStatus = Set.of("offen", "open");

        // when
        var actualStatus = statusSpecification.allToLowerCase(givenStatus);

        // then
        assertThat(actualStatus).containsExactlyInAnyOrderElementsOf(expectedStatus);
    }

    @Test
    public void toPredicateReturnsPredicateContainingAllExpectedExpressions() {
        // given
        var firstStatus  = "status1";
        var secondStatus = "status2";
        var givenStatus = Set.of(firstStatus, secondStatus);
        var statusSpecification = new StatusSpecification(givenStatus);
        var expectedPredicates = new Predicate[] {firstPredicateMock, secondPredicateMock};

        given(rootMock.get("status")).willAnswer((Answer<Path<String>>) invocation -> pathMock);
        given(criteriaBuilderMock.lower(pathMock)).willReturn(pathMock);
        given(criteriaBuilderMock.equal(pathMock, firstStatus)).willReturn(firstPredicateMock);
        given(criteriaBuilderMock.equal(pathMock, secondStatus)).willReturn(secondPredicateMock);
        given(criteriaBuilderMock.or(expectedPredicates)).willReturn(completePredicate);

        // when
        var actualPredicate = statusSpecification.toPredicate(rootMock, null, criteriaBuilderMock);

        // then
        assertThat(actualPredicate).isEqualTo(completePredicate);
    }

    @Test
    public void toPredicateReturnsEmptyAndPredicateWhenNoStatusGiven() {
        // given
        var statusSpecification = new StatusSpecification(Set.of());

        given(rootMock.get("status")).willAnswer((Answer<Path<String>>) invocation -> pathMock);
        given(criteriaBuilderMock.lower(pathMock)).willReturn(pathMock);
        given(criteriaBuilderMock.and()).willReturn(completePredicate);

        // when
        var actualPredicate = statusSpecification.toPredicate(rootMock, null, criteriaBuilderMock);

        // then
        assertThat(actualPredicate).isEqualTo(completePredicate);
    }

}
