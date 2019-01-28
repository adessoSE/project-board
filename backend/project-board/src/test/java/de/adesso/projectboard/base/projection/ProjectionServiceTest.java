package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.projection.exception.MultipleDefaultProjectionsException;
import de.adesso.projectboard.base.projection.exception.MultipleSimilarlyNamedProjectionsException;
import de.adesso.projectboard.base.projection.util.AnnotationUtilsWrapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.data.util.Pair;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class ProjectionServiceTest {

    @Mock
    private NamedProjection namedProjectionAnnotationMock;

    @Mock
    private AnnotatedTypeScanner typeScannerMock;

    @Mock
    private AnnotationUtilsWrapper annotationUtilsWrapperMock;

    private Class annotatedClass;

    private ProjectionService projectionService;

    @Before
    public void setUp() {
        this.annotatedClass = Iterable.class;

        this.projectionService = new ProjectionService(typeScannerMock, annotationUtilsWrapperMock);
    }

    @Test
    public void getByNameOrDefaultReturnsProjectWithMatchingNameAndTarget() {
        // given
        var expectedName = "projection-name";
        var expectedTarget = String.class;
        Pair<String, Class<?>> pair = Pair.of(expectedName, expectedTarget);

        projectionService.projectionClassMap.put(pair, annotatedClass);

        // when
        var actualProjection = projectionService.getByNameOrDefault(expectedName, expectedTarget);

        // then
        assertThat(actualProjection).isEqualTo(annotatedClass);
    }

    @Test
    public void getByNameOrDefaultReturnsDefaultWhenNoProjectionWithGivenNameAndTargetNameExists() {
        // given
        var expectedName = "projection-name";
        var expectedTarget = String.class;

        projectionService.defaultProjectionClassMap.put(expectedTarget, annotatedClass);

        // when
        var actualProjection = projectionService.getByNameOrDefault(expectedName, expectedTarget);

        // then
        assertThat(actualProjection).isEqualTo(annotatedClass);
    }

    @Test
    public void addProjectionInterfacesAddsProjectionInterfaces() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedName = "name";
        var expectedTarget = (Class) Integer.class;
        var expectedBasePackage = "de/test";

        given(typeScannerMock.findTypes(expectedBasePackage)).willReturn(Set.of(annotatedClass));

        given(annotationUtilsWrapperMock.findAnnotation(annotatedClass, NamedProjection.class))
                .willReturn(namedProjectionAnnotationMock);

        given(namedProjectionAnnotationMock.target())
                .willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name())
                .willReturn(expectedName);
        given(namedProjectionAnnotationMock.defaultProjection())
                .willReturn(true);

        // when
        projectionService.addProjectionInterfaces(expectedBasePackage);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap)
                .containsExactly(entry(Pair.of(expectedName, expectedTarget), (Class<?>) annotatedClass));

        softly.assertThat(projectionService.defaultProjectionClassMap)
                .containsExactly(entry((Class<?>) expectedTarget, (Class<?>) annotatedClass));

        softly.assertAll();
    }

    @Test
    public void getAnnotatedMethodsOnlyReturnsInterfaces() {
        // given
        var expectedBasePackage = "de/test";
        var allAnnotatedClasses = Set.of(String.class, Iterable.class, Comparable.class);
        var expectedReturnedClasses = Set.of(Iterable.class, Comparable.class);

        given(typeScannerMock.findTypes(expectedBasePackage)).willReturn(allAnnotatedClasses);

        // when
        var actualAnnotatedClasses = projectionService.getAnnotatedInterfaces(expectedBasePackage);

        // then
        assertThat(actualAnnotatedClasses).containsExactlyInAnyOrderElementsOf(expectedReturnedClasses);
    }

    @Test
    public void addProjectionInterfaceAddsProjectionWithSimpleClassNameWhenNameNotSet() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        var expectedTarget = (Class) Integer.class;
        var expectedName = annotatedClass.getSimpleName().toLowerCase();

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn("");
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(false);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap)
                .containsExactly(entry(Pair.of(expectedName, expectedTarget), (Class<?>) annotatedClass));

        softly.assertThat(projectionService.defaultProjectionClassMap).isEmpty();

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceAddsProjectionAsDefaultWithSimpleClassNameWhenMarkedAsDefaultAndNameNotSet() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedTarget = (Class) Integer.class;
        var expectedName = annotatedClass.getSimpleName().toLowerCase();

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn("");
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap)
                .containsOnly(entry(Pair.of(expectedName, expectedTarget), (Class<?>) annotatedClass));

        softly.assertThat(projectionService.defaultProjectionClassMap)
                .containsExactly(entry((Class<?>) expectedTarget, (Class<?>) annotatedClass));

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceAddsProjectionWithName() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedTarget = (Class) Integer.class;
        var expectedName = "cool-projection-name";

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn(expectedName);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(false);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap)
                .containsOnly(entry(Pair.of(expectedName, expectedTarget), (Class<?>) annotatedClass));

        softly.assertThat(projectionService.defaultProjectionClassMap)
                .isEmpty();

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceAddsProjectionAsDefaultWithNameWhenMarkedAsDefault() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedTarget = (Class) Integer.class;
        var expectedName = "cool-projection-name";

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn(expectedName);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap)
                .containsOnly(entry(Pair.of(expectedName, expectedTarget), (Class<?>) annotatedClass));

        softly.assertThat(projectionService.defaultProjectionClassMap)
                .containsExactly(entry((Class<?>) expectedTarget, (Class<?>) annotatedClass));

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceThrowsExceptionWhenMultipleSimilarlyNamedPresent() {
        // given
        var projectionName = "projection-name";
        var projectionTarget = (Class) Integer.class;

        projectionService.projectionClassMap.put(Pair.of(projectionName, projectionTarget), annotatedClass);

        given(namedProjectionAnnotationMock.name()).willReturn(projectionName);
        given(namedProjectionAnnotationMock.target()).willReturn(projectionTarget);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(false);

        // when / then
        assertThatThrownBy(() -> projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass))
                .isInstanceOf(MultipleSimilarlyNamedProjectionsException.class)
                .hasMessage(String.format("Multiple interfaces annotated with @NamedInterface " +
                        "have the same name ('%s') for the target class '%s'!", projectionName, projectionTarget.getName()));
    }

    @Test
    public void addProjectionInterfaceThrowsExceptionWhenMultipleDefaultsPresent() {
        // given
        var projectionName = "any-name";
        var projectionTarget = (Class) Integer.class;

        projectionService.defaultProjectionClassMap.put(projectionTarget, annotatedClass);

        given(namedProjectionAnnotationMock.name()).willReturn(projectionName);
        given(namedProjectionAnnotationMock.target()).willReturn(projectionTarget);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when / then
        assertThatThrownBy(() -> projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass))
                .isInstanceOf(MultipleDefaultProjectionsException.class)
                .hasMessage(String.format("Multiple interfaces annotated with @NamedInterface " +
                        "are marked as the default projections for target class '%s'!", projectionTarget.getName()));
    }

    @Test
    public void getProjectionNameReturnsNameWhenNameNotEmpty() {
        // given
        var expectedName = "name-value";

        given(namedProjectionAnnotationMock.name()).willReturn("name-value");

        // when / then
        compareProjectionNameWithExpectedName(expectedName);
    }

    @Test
    public void getProjectionNameReturnsLowerCaseSimpleNameWhenNameEmpty() {
        // given
        var expectedName = annotatedClass.getSimpleName().toLowerCase();

        given(namedProjectionAnnotationMock.name()).willReturn("");

        // when / then
        compareProjectionNameWithExpectedName(expectedName);
    }

    private void compareProjectionNameWithExpectedName(String expectedName) {
        // when
        var actualName = projectionService.getProjectionName(namedProjectionAnnotationMock, annotatedClass);

        // then
        assertThat(actualName).isEqualTo(expectedName);
    }

}