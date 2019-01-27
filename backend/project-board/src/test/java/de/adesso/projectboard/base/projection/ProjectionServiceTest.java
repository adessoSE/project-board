package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.projection.exception.MultipleDefaultProjectionsException;
import de.adesso.projectboard.base.projection.exception.MultipleSimilarlyNamedProjectionsException;
import de.adesso.projectboard.base.projection.util.ClassUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.data.util.Pair;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProjectionServiceTest {

    @Mock
    private NamedProjectionCandidateComponentProvider interfaceProviderMock;

    @Mock
    private ClassUtils classUtilsMock;

    @Mock
    private NamedProjection namedProjectionAnnotationMock;

    @Mock
    private BeanDefinition beanDefinitionMock;

    private Class annotatedClass;

    private ProjectionService projectionService;

    @Before
    public void setUp() {
        this.annotatedClass = String.class;
        this.projectionService = new ProjectionService(interfaceProviderMock, classUtilsMock);
    }


    @Test
    public void getByNameOrDefaultReturnsProjectWithMatchingNameAndTarget() {
        // given
        var expectedName = "projection-name";
        var expectedTarget = ProjectionTarget.USER;
        var pair = Pair.of(expectedName, expectedTarget);

        projectionService.projectionClassMap.put(pair, annotatedClass);

        // when
        var actualProjection = projectionService.getByNameOrDefault(expectedName, expectedTarget);

        // then
        assertThat(actualProjection).isEqualTo(annotatedClass);
    }

    @Test
    public void getByNameOrDefaultReturnsDefaultWhenNoProjectionWithGivenAndTargetNameExists() {
        // given
        var expectedName = "projection-name";
        var expectedTarget = ProjectionTarget.USER;

        projectionService.defaultProjectionClassMap.put(expectedTarget, annotatedClass);

        // when
        var actualProjection = projectionService.getByNameOrDefault(expectedName, expectedTarget);

        // then
        assertThat(actualProjection).isEqualTo(annotatedClass);
    }

    @Test
    public void addProjectionInterfacesAddsAllProjectionInterfaces() throws ClassNotFoundException, MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var basePackage = "de/test";
        var beanClassName = "TestClass";
        var expectedProjectionName = "projection-name";
        var expectedProjectionTarget = ProjectionTarget.USER;
        var expectedPair = Pair.of(expectedProjectionName, expectedProjectionTarget);

        given(interfaceProviderMock.findCandidateComponents(basePackage)).willReturn(Set.of(beanDefinitionMock));
        given(beanDefinitionMock.getBeanClassName()).willReturn(beanClassName);
        given(classUtilsMock.getClassForName(beanClassName)).willReturn(annotatedClass);
        given(classUtilsMock.getAnnotation(annotatedClass, NamedProjection.class)).willReturn(namedProjectionAnnotationMock);

        given(namedProjectionAnnotationMock.name()).willReturn(expectedProjectionName);
        given(namedProjectionAnnotationMock.target()).willReturn(expectedProjectionTarget);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when
        projectionService.addProjectionInterfaces(basePackage);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap).containsExactly(entry(expectedPair, (Class<?>) annotatedClass));
        softly.assertThat(projectionService.defaultProjectionClassMap).containsExactly(entry(expectedProjectionTarget, (Class<?>) annotatedClass));

        softly.assertAll();
    }

    @Test
    public void getAnnotatedInterfaceReturnsAllAnnotatedInterfaces() throws ClassNotFoundException {
        // given
        var basePackage = "de/test";
        var beanClassName = "TestClass";

        given(interfaceProviderMock.findCandidateComponents(basePackage)).willReturn(Set.of(beanDefinitionMock));
        given(beanDefinitionMock.getBeanClassName()).willReturn(beanClassName);
        given(classUtilsMock.getClassForName(beanClassName)).willReturn(annotatedClass);
        given(classUtilsMock.getAnnotation(annotatedClass, NamedProjection.class)).willReturn(namedProjectionAnnotationMock);

        // when
        var actualAnnotatedInterfaces = projectionService.getAnnotatedInterfaces(basePackage);

        // then
        assertThat(actualAnnotatedInterfaces).containsExactly(Pair.of(namedProjectionAnnotationMock, annotatedClass));
    }

    @Test
    public void addProjectionInterfaceDoesNotAddProjectionAsDefaultWithSimpleClassNameWhenNotMarkedAsDefaultAndNameNotSet() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        var expectedTarget = ProjectionTarget.USER;
        var expectedName = annotatedClass.getSimpleName().toLowerCase();
        var expectedPair = Pair.of(expectedName, expectedTarget);

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn("");

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap).containsExactly(entry(expectedPair, (Class<?>) annotatedClass));
        softly.assertThat(projectionService.defaultProjectionClassMap).isEmpty();

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceAddsProjectionsAsDefaultWithSimpleClassNameWhenMarkedAsDefaultAndNameNotSet() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedTarget = ProjectionTarget.USER;
        var expectedName = annotatedClass.getSimpleName().toLowerCase();
        var expectedPair = Pair.of(expectedName, expectedTarget);

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn("");
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap).containsOnly(entry(expectedPair, (Class<?>) annotatedClass));
        softly.assertThat(projectionService.defaultProjectionClassMap).containsExactly(entry(expectedTarget, (Class<?>) annotatedClass));

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceDoesNotAddProjectionAsDefaultWithNameWhenNotMarkedAsDefault() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedTarget = ProjectionTarget.USER;
        var expectedName = "cool-projection-name";
        var expectedPair = Pair.of(expectedName, expectedTarget);

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn("");
        given(namedProjectionAnnotationMock.name()).willReturn(expectedName);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap).containsOnly(entry(expectedPair, (Class<?>) annotatedClass));
        softly.assertThat(projectionService.defaultProjectionClassMap).isEmpty();

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceAddsProjectionAsDefaultWithNameWhenMarkedAsDefault() throws MultipleDefaultProjectionsException, MultipleSimilarlyNamedProjectionsException {
        // given
        var expectedTarget = ProjectionTarget.USER;
        var expectedName = "cool-projection-name";
        var expectedPair = Pair.of(expectedName, expectedTarget);

        given(namedProjectionAnnotationMock.target()).willReturn(expectedTarget);
        given(namedProjectionAnnotationMock.name()).willReturn(expectedName);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when
        projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(projectionService.projectionClassMap).containsOnly(entry(expectedPair, (Class<?>) annotatedClass));
        softly.assertThat(projectionService.defaultProjectionClassMap).containsExactly(entry(expectedTarget, (Class<?>) annotatedClass));

        softly.assertAll();
    }

    @Test
    public void addProjectionInterfaceThrowsExceptionWhenMultipleSimilarlyNamedPresent() {
        // given
        var projectionName = "projection-name";
        var projectionTarget = ProjectionTarget.USER;

        projectionService.projectionClassMap.put(Pair.of(projectionName, projectionTarget), annotatedClass);

        given(namedProjectionAnnotationMock.name()).willReturn(projectionName);
        given(namedProjectionAnnotationMock.target()).willReturn(projectionTarget);

        // when / then
        assertThatThrownBy(() -> projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass))
                .isInstanceOf(MultipleSimilarlyNamedProjectionsException.class)
                .hasMessage(String.format("Multiple interfaces annotated with @NamedInterface " +
                        "have the same name ('%s') for the target '%s'!", projectionName, projectionTarget.toString()));
    }

    @Test
    public void addProjectionInterfaceThrowsExceptionWhenMultipleDefaultsPresent() {
        // given
        var defaultTarget = ProjectionTarget.USER;
        var projectionName = "any-name";

        projectionService.defaultProjectionClassMap.put(defaultTarget, annotatedClass);

        given(namedProjectionAnnotationMock.name()).willReturn(projectionName);
        given(namedProjectionAnnotationMock.target()).willReturn(defaultTarget);
        given(namedProjectionAnnotationMock.defaultProjection()).willReturn(true);

        // when / then
        assertThatThrownBy(() -> projectionService.addProjectionInterface(namedProjectionAnnotationMock, annotatedClass))
                .isInstanceOf(MultipleDefaultProjectionsException.class)
                .hasMessage(String.format("Multiple interfaces annotated with @NamedInterface " +
                        "are marked as the default projections for target '%s'!", defaultTarget.toString()));
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