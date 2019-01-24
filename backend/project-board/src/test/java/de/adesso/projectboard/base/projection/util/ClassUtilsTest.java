package de.adesso.projectboard.base.projection.util;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsTest {

    private ClassUtils classUtils;

    @Before
    public void setUp() {
        this.classUtils = new ClassUtils();
    }

    @Test
    public void getClassForNameReturnsExpectedClass() throws ClassNotFoundException {
        // given
        var expectedClass = String.class;
        var fullyQualifiedName = expectedClass.getName();

        // when
        var actualClass = classUtils.getClassForName(fullyQualifiedName);

        // then
        assertThat(actualClass).isEqualTo(expectedClass);
    }

}