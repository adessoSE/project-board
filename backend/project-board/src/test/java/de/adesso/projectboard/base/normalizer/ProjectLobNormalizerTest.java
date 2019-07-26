package de.adesso.projectboard.base.normalizer;

import de.adesso.projectboard.base.project.persistence.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLobNormalizerTest {

    private ProjectLobNormalizer projectLobNormalizer;

    @Mock
    private Project projectMock;

    @Before
    public void setUp() {
        this.projectLobNormalizer = new ProjectLobNormalizer(Set.of());
    }

    @Test
    public void getLobOfReturnsProjectLob() {
        // given
        var expectedLob = "LOB Test 1";

        given(projectMock.getLob()).willReturn(expectedLob);

        // when
        var actualLob = projectLobNormalizer.getFieldValue(projectMock);

        // then
        assertThat(actualLob).isEqualTo(expectedLob);
    }

    @Test
    public void setNormalizedLobSetsLobAndReturnsUpdatedProject() {
        // given
        var normalizedLob = "LOB Test 2";

        given(projectMock.setLob(normalizedLob)).willReturn(projectMock);

        // when
        var updatedProject = projectLobNormalizer.setNormalizedFieldValue(projectMock, normalizedLob);

        // then
        assertThat(updatedProject).isEqualTo(projectMock);
    }

}
