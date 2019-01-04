import { Location } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as $ from 'jquery';
import { combineLatest, Subject } from 'rxjs';
import { debounceTime, switchMap, takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-browse-projects',
  templateUrl: './browse-projects.component.html',
  styleUrls: ['./browse-projects.component.scss']
})
export class BrowseProjectsComponent implements OnInit {

  appTooltip = 'Du hast dieses Projekt bereits angefragt.';
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  studTooltip = 'Studentisches Projekt';

  projects: Project[] = [];
  appliedProjects: Project[] = [];
  bookmarks: Project[] = [];
  selectedProject: Project;
  below = false;
  mobile = false;
  scroll = true;
  isUserBoss = false;
  currentPage = 0;
  searchText = '';
  loadingProjects = false;
  projectsFound: number;
  infiniteScrollDisabled = false;

  private divToScroll;

  destroy$ = new Subject<void>();
  private searchText$ = new Subject<string>();

  constructor(private employeeService: EmployeeService,
              private projectsService: ProjectService,
              private alertService: AlertService,
              private route: ActivatedRoute,
              private location: Location,
              private router: Router
  ) {}

  @HostListener('window:resize') onResize() {
    this.mobile = document.body.clientWidth < 992;
  }

  swipebugplaceholder() {}

  loadProjects() {
    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        if (data[0].projects) {
          this.projects = data[0].projects;
        }

      // extract projects from applications
      this.appliedProjects = data[0].applications ? data[0].applications.map(app => app.project) : [];
      this.bookmarks = data[0].bookmarks;
      this.isUserBoss = data[0].isUserBoss;

      // set selected project
      this.setSelectedProject(data[1].id);
    });
  }

  ngOnInit() {
    this.mobile = document.body.clientWidth < 992;

    this.searchText$
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(500),
        switchMap(searchText => {
          return this.projectsService.search(searchText);
        }))
      .subscribe(projects => {
        this.loadingProjects = false;
        this.projects = projects;
        this.projectsFound = projects.length;
      });
      this.loadProjects();

    this.divToScroll = document.getElementById('divToScroll');
  }

  searchProjects() {
    this.infiniteScrollDisabled = false;
    this.loadingProjects = true;
    this.projects = [];
    this.currentPage = 0;
    this.searchText$.next(this.searchText);
  }

  private setSelectedProject(projectId: string) {
    if (!projectId) {
      this.selectedProject = null;
      return;
    }

    for (const p of this.projects) {
      if (p.id === projectId) {
        this.selectedProject = p;
        return;
      }
    }
    this.projectsService.getProjectWithID(projectId)
      .subscribe(
        project => this.selectedProject = project,
        () => this.alertService.info('Das angegebene Projekt wurde nicht gefunden.')
      );
  }

  projectClicked(project) {

    const newProjectOffset = $(`#${project.id}`).offset().top;

    if (this.selectedProject === project) {
      this.location.replaceState(`/browse`);
      this.selectedProject = null;
      this.scroll = false;
      this.below = false;
    } else {
      this.location.replaceState(`/browse/${project.id}`);
      if (this.selectedProject) {

        const oldProjectOffset = $(`#${this.selectedProject.id}`).offset().top;

        if (oldProjectOffset > newProjectOffset) {
          this.below = false;
        } else {
          this.below = true;
        }
      }
      this.selectedProject = project;
      this.scroll = true;
    }
  }

  isProjectApplicable(projectId) {
    return this.appliedProjects ? !this.appliedProjects.some(p => p && p.id === projectId) : true;
  }

  isProjectBookmarked(projectId) {
    return this.bookmarks ? this.bookmarks.some(p => p && p.id === projectId) : false;
  }

  handleBookmark(project) {
    const index = this.bookmarks.findIndex(p => p.id === project.id);
    if (index > -1) {
      this.bookmarks.splice(index, 1);
    } else {
      this.bookmarks.push(project);
    }
  }

  showDetails(p: Project) {
    this.router.navigate(['/browse/' + p.id]);
  }
}
