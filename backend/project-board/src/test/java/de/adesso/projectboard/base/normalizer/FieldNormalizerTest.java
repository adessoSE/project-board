package de.adesso.projectboard.base.normalizer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FieldNormalizerTest {

    private FieldNormalizer<FruitNameContainer> fieldNormalizer;

    @Mock
    private RootTermDistanceCalculator firstDistanceCalculatorMock;

    @Mock
    private RootTermDistanceCalculator secondDistanceCalculatorMock;

    @Before
    public void setUp() {
        this.fieldNormalizer = new FieldNormalizer<>(Set.of(firstDistanceCalculatorMock, secondDistanceCalculatorMock)) {

            @Override
            String getFieldValue(FruitNameContainer fieldContainingObject) {
                return fieldContainingObject.getName();
            }

            @Override
            FruitNameContainer setNormalizedFieldValue(FruitNameContainer fieldContainingObject, String normalizedValue) {
                return fieldContainingObject.setName(normalizedValue);
            }

        };
    }

    @Test
    public void normalizeNormalizesLobOfAllContainers() {
        // given
        var firstContainerName = "red thing";
        var normalizedFirstContainerName = "Tomato";
        var firstContainer = new FruitNameContainer(firstContainerName);
        var normalizedFirstContainer = new FruitNameContainer(normalizedFirstContainerName);

        var secondContainerName = "green thing";
        var normalizedSecondContainerName = "Apple";
        var secondContainer = new FruitNameContainer(secondContainerName);
        var normalizedSecondContainer = new FruitNameContainer(normalizedSecondContainerName);

        var givenContainers = List.of(firstContainer, secondContainer);
        var expectedNormalizedContainers = List.of(normalizedFirstContainer, normalizedSecondContainer);

        given(firstDistanceCalculatorMock.getOriginalRootTerm()).willReturn(normalizedFirstContainerName);
        given(firstDistanceCalculatorMock.calculateMinimalDistance(any())).willReturn(100);
        given(firstDistanceCalculatorMock.calculateMinimalDistance(firstContainerName)).willReturn(0);

        given(secondDistanceCalculatorMock.getOriginalRootTerm()).willReturn(normalizedSecondContainerName);
        given(secondDistanceCalculatorMock.calculateMinimalDistance(any())).willReturn(100);
        given(secondDistanceCalculatorMock.calculateMinimalDistance(secondContainerName)).willReturn(0);

        // when
        var actualNormalizedContainers = fieldNormalizer.normalize(givenContainers);

        // then
        assertThat(actualNormalizedContainers).containsExactlyInAnyOrderElementsOf(expectedNormalizedContainers);
    }

    @Data
    @AllArgsConstructor
    private class FruitNameContainer {

        private String name;

    }

}
