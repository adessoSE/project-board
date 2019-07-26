package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.persistence.specification.ProjectSpecification;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserProjectServiceTest {

    private static final Set<String> LOB_DEPENDENT_STATUS = Set.of("open");

    private static final Set<String> EXCLUDED_STATUS = Set.of("closed");

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectRepository projectRepoMock;

    @Mock
    private HibernateSearchService managerHibernateSearchServiceMock;

    @Mock
    private HibernateSearchService staffHibernateSearchServiceMock;

    @Mock
    private User userMock;

    @Mock
    private UserData userDataMock;

    @Mock
    private Project projectMock;

    @Mock
    private ProjectBoardConfigurationProperties propertiesMock;

    private RepositoryUserProjectService userProjectService;

    @Before
    public void setUp() {
        given(propertiesMock.getLobDependentStatus()).willReturn(new ArrayList<>(LOB_DEPENDENT_STATUS));
        given(propertiesMock.getStatusExcludedFromList()).willReturn(new ArrayList<>(EXCLUDED_STATUS));

        this.userProjectService = new RepositoryUserProjectService(projectRepoMock, userServiceMock,
                managerHibernateSearchServiceMock, staffHibernateSearchServiceMock, propertiesMock);
    }

    @Test
    public void getProjectsForUserReturnsLobDependentProjectsWhenUserIsNoManager() {
        // given
        var userLob = "LoB Test";
        var expectedSpecification = new ProjectSpecification(EXCLUDED_STATUS, LOB_DEPENDENT_STATUS, userLob);
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
    public void getProjectsForUserReturnsAllProjectsWhenUserIsManager() {
        // given
        var expectedSpecification = new ProjectSpecification(EXCLUDED_STATUS, Set.of(), null);
        var sort = Sort.unsorted();
        var expectedProjects = List.of(projectMock);

        given(userServiceMock.userIsManager(userMock)).willReturn(true);
        given(projectRepoMock.findAll(expectedSpecification, sort)).willReturn(expectedProjects);

        // when
        var actualProjects = userProjectService.getProjectsForUser(userMock, sort);

        // then
        assertThat(actualProjects).isEqualTo(expectedProjects);
    }

    @Test
    public void searchProjectsForUserSearchesInAllProjectsWhenUserIsManager() {
        // given
        var expectedQuery = "a cool query";

        given(userServiceMock.userIsManager(userMock)).willReturn(true);
        given(managerHibernateSearchServiceMock.searchProjects(expectedQuery, null)).willReturn(List.of(projectMock));

        // when
        var actualProjects = userProjectService.searchProjectsForUser(userMock, expectedQuery, Sort.unsorted());

        // then
        assertThat(actualProjects).containsExactly(projectMock);
    }

    @Test
    public void searchProjectsForUserSearchesInLobDependentProjectsWhenUserIsNoManager() {
        // given
        var expectedQuery = "a cool query";
        var expectedLob = "LOB Test";

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(expectedLob);
        given(staffHibernateSearchServiceMock.searchProjects(expectedQuery, expectedLob)).willReturn(List.of(projectMock));

        // when
        var actualProjects = userProjectService.searchProjectsForUser(userMock, expectedQuery, Sort.unsorted());

        // then
        assertThat(actualProjects).containsExactly(projectMock);
    }

    @Test
    public void getProjectsForUserPaginatedReturnsLobDependentProjectsWhenUserIsNoManager() {
        // given
        var userLob = "LoB Test1234";
        var pageable = PageRequest.of(0, 100);
        var expectedStatusSpecification = new ProjectSpecification(EXCLUDED_STATUS, LOB_DEPENDENT_STATUS, userLob);
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
    public void getProjectsForUserPaginatedReturnsAllProjectsWhenUserIsManager() {
        // given
        var pageable = PageRequest.of(0, 100);
        var expectedStatusSpecification = new ProjectSpecification(EXCLUDED_STATUS, Set.of(), null);
        var expectedProjects = List.of(projectMock);
        var expectedPage = new PageImpl<>(expectedProjects);

        given(userServiceMock.userIsManager(userMock)).willReturn(true);
        given(projectRepoMock.findAll(expectedStatusSpecification, pageable)).willReturn(expectedPage);

        // when
        var actualPage = userProjectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        assertThat(actualPage).isEqualTo(expectedPage);
    }

    @Test
    public void searchProjectsForUserPaginatedSearchesInAllProjectsWhenUserIsManager() {
        // given
        var expectedQuery = "a cool query";
        var expectedPageable = PageRequest.of(1, 4);

        given(userServiceMock.userIsManager(userMock)).willReturn(true);
        given(managerHibernateSearchServiceMock.searchProjects(expectedQuery, expectedPageable, null))
                .willReturn(new PageImpl<>(List.of(projectMock)));

        // when
        var actualProjectPage = userProjectService.searchProjectsForUserPaginated(expectedQuery, userMock, expectedPageable);

        // then
        assertThat(actualProjectPage).containsExactly(projectMock);
    }

    @Test
    public void searchProjectsForUserPaginatedSearchesInLobDependentProjectsWhenUserIsNoManager() {
        // given
        var expectedQuery = "a cool query";
        var expectedLob = "LOB Test";
        var expectedPageable = PageRequest.of(1, 4);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(expectedLob);
        given(staffHibernateSearchServiceMock.searchProjects(expectedQuery, expectedPageable, expectedLob))
                .willReturn(new PageImpl<>(List.of(projectMock)));

        // when
        var actualProjectPage = userProjectService.searchProjectsForUserPaginated(expectedQuery, userMock, expectedPageable);

        // then
        assertThat(actualProjectPage).containsExactly(projectMock);
    }

}
