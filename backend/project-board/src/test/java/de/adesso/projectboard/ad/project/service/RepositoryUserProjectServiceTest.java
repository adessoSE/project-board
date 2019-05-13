package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.persistence.specification.StatusSpecification;
import de.adesso.projectboard.base.search.HibernateSearchService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserProjectServiceTest {

    private static final String USER_ID = "user";

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectRepository projectRepoMock;

    @Mock
    private UserRepository userRepoMock;

    @Mock
    private RepositoryProjectService projectServiceMock;

    @Mock
    private HibernateSearchService hibernateSearchServiceMock;

    @Mock
    private User userMock;

    @Mock
    private Project projectMock;

    private RepositoryUserProjectService userProjectService;

    @Before
    public void setUp() {
        this.userProjectService
                = new RepositoryUserProjectService(userServiceMock, projectRepoMock, userRepoMock, projectServiceMock, hibernateSearchServiceMock);
    }

    @Test
    public void getProjectsForUser() {
        // given
        var expectedSpecification = new StatusSpecification(Set.of("offen", "open", "eskaliert", "escalated"));
        var sort = Sort.unsorted();
        var expectedProjects = List.of(projectMock);

        given(projectRepoMock.findAll(expectedSpecification, sort)).willReturn(expectedProjects);

        // when
        var actualProjects = userProjectService.getProjectsForUser(userMock, sort);

        // then
        assertThat(actualProjects).isEqualTo(expectedProjects);
    }

    @Test
    public void searchProjectsForUser() {
        // given
        var searchQuery = "query";
        var sort = Sort.unsorted();
        var status = Set.of("offen", "open", "eskaliert", "escalated");
        var expectedProjects = List.of(projectMock);

        given(hibernateSearchServiceMock.searchProjects(searchQuery, status)).willReturn(expectedProjects);

        // when
        var actualProjects = userProjectService.searchProjectsForUser(userMock, searchQuery, sort);

        // then
        assertThat(actualProjects).isEqualTo(expectedProjects);
    }

    @Test
    public void getProjectsForUserPaginated() {
        // given
        var status = Set.of("offen", "open", "eskaliert", "escalated");
        var pageable = PageRequest.of(0, 100);
        var statusSpecification = new StatusSpecification(status);
        var expectedProjects = List.of(projectMock);
        var expectedPage = new PageImpl<>(expectedProjects);

        given(projectRepoMock.findAll(statusSpecification, pageable)).willReturn(expectedPage);

        // when
        var actualPage = userProjectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        assertThat(actualPage).isEqualTo(expectedPage);
    }

    @Test
    public void searchProjectsForUserPaginated() {
        // given
        var searchQuery = "query";
        var pageable = PageRequest.of(0, 100);
        var expectedProjects = List.of(projectMock);
        var expectedPage = new PageImpl<>(expectedProjects);
        var status = Set.of("offen", "open", "eskaliert", "escalated");

        given(hibernateSearchServiceMock.searchProjects(searchQuery, status, pageable)).willReturn(expectedPage);

        // when
        var actualPage = userProjectService.searchProjectsForUserPaginated(searchQuery, userMock, pageable);

        // then
        assertThat(actualPage).isEqualTo(expectedPage);
    }

}
