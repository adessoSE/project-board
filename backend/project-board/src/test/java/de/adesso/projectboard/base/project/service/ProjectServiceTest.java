package de.adesso.projectboard.base.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    @Mock
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @Test
    public void saveAllDefaultImplementation() {
        // given
        Project firstExpectedProject = new Project();
        Project secondExpectedProject = new Project();

        List<Project> expectedProjectList = Arrays.asList(firstExpectedProject, secondExpectedProject);

        given(projectService.saveAll(expectedProjectList)).willCallRealMethod();

        given(projectService.save(firstExpectedProject)).willReturn(firstExpectedProject);
        given(projectService.save(secondExpectedProject)).willReturn(secondExpectedProject);

        // when
        List<Project> savedProjects = projectService.saveAll(expectedProjectList);

        // then
        verify(projectService, times(2)).save(projectArgumentCaptor.capture());

        SoftAssertions softly = new SoftAssertions();

        assertThat(savedProjects).isEqualTo(expectedProjectList);
        assertThat(projectArgumentCaptor.getAllValues()).isEqualTo(expectedProjectList);

        softly.assertAll();
    }

}