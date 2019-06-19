package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.persistence.specification.StatusSpecification;
import de.adesso.projectboard.base.search.HibernateSearchService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserProjectServiceTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectRepository projectRepoMock;

    @Mock
    private HibernateSearchService hibernateSearchServiceMock;

    @Mock
    private User userMock;

    @Mock
    private UserData userDataMock;

    @Mock
    private Project projectMock;

    private RepositoryUserProjectService userProjectService;

    @Before
    public void setUp() {
        this.userProjectService
                = new RepositoryUserProjectService(projectRepoMock, userServiceMock, hibernateSearchServiceMock);
    }

    @Test
    public void getProjectsForUser() {
        // given
        var userLob = "LoB Test";
        var expectedSpecification = new StatusSpecification(RepositoryUserProjectService.LOB_INDEPENDENT_STATUS, RepositoryUserProjectService.LOB_DEPENDENT_STATUS, userLob);
        var sort = Sort.unsorted();
        var expectedProjects = List.of(projectMock);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);
        given(projectRepoMock.findAll(expectedSpecification, sort)).willReturn(expectedProjects);

        // when
        var actualProjects = userProjectService.getProjectsForUser(userMock, sort);

        // then
        assertThat(actualProjects).isEqualTo(expectedProjects);
    }

    @Test
    public void searchProjectsForUser() {
        // TODO: implement
    }

    @Test
    public void getProjectsForUserPaginated() {
        // given
        var userLob = "LoB Test1234";
        var pageable = PageRequest.of(0, 100);
        var expectedStatusSpecification = new StatusSpecification(RepositoryUserProjectService.LOB_INDEPENDENT_STATUS, RepositoryUserProjectService.LOB_DEPENDENT_STATUS, userLob);
        var expectedProjects = List.of(projectMock);
        var expectedPage = new PageImpl<>(expectedProjects);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);
        given(projectRepoMock.findAll(expectedStatusSpecification, pageable)).willReturn(expectedPage);

        // when
        var actualPage = userProjectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        assertThat(actualPage).isEqualTo(expectedPage);
    }

    @Test
    public void searchProjectsForUserPaginated() {
        //TODO: implement
    }

}
