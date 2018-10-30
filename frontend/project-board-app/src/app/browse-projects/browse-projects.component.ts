import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { faBookmark } from '@fortawesome/free-regular-svg-icons';
import { faEnvelope } from '@fortawesome/free-regular-svg-icons/faEnvelope';
import { faGraduationCap } from '@fortawesome/free-solid-svg-icons/faGraduationCap';
import * as $ from 'jquery';
import { combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-browse-projects',
  templateUrl: './browse-projects.component.html',
  styleUrls: ['./browse-projects.component.scss']
})
export class BrowseProjectsComponent implements OnInit, AfterViewChecked {
  appTooltip = 'Du hast dieses Projekt bereits angefragt.';
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  studTooltip = 'Studentisches Projekt';
  faEnvelope = faEnvelope;
  faBookmark = faBookmark;
  faGradCap = faGraduationCap;

  projects: Project[] = [];
  filteredProjects: Project[] = [];
  appliedProjects: Project[] = [];
  bookmarks: Project[] = [];
  selectedProject: Project;
  mobile = false;
  scroll = true;
  isUserBoss = false;

  destroy$ = new Subject<void>();

  @HostListener('window:resize') onResize() {
    this.mobile = window.screen.width <= 425;
  }

  constructor(private employeeService: EmployeeService,
              private projectsService: ProjectService,
              private alertService: AlertService,
              private route: ActivatedRoute,
              private location: Location,
              private router: Router) { }

  ngOnInit() {
    this.mobile = window.screen.width < 768;

    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.projects = data[0].projects;
        this.filteredProjects = this.projects;

        // extract projects from applications
        this.appliedProjects = data[0].applications ? data[0].applications.map(app => app.project) : [];
        this.bookmarks = data[0].bookmarks;
        this.isUserBoss = data[0].isUserBoss;

        // set selected project
        this.setSelectedProject(data[1].id);
      });
  }

  filterProjects(filterInput) {
    const filter = filterInput.toLowerCase().split(' ').filter(e => e.length > 2);
    if (filter.length > 0) {
      this.location.replaceState(`/browse`);
      this.selectedProject = null;
      const filtered: { project: Project, hitCount: number }[] = [];
      this.projects.forEach(p => {
        for (const f of filter) {
          if (p.title && p.title.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.id === p.id)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
          if (p.job && p.job.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.id === p.id)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
          if (p.description && p.description.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.id === p.id)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
          if (p.skills && p.skills.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.id === p.id)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
        }
      });
      this.filteredProjects = filtered.sort((a, b) => a.hitCount >= b.hitCount ? -1 : 1).map(e => e.project);
    } else {
      this.filteredProjects = this.projects;
    }
  }

  private setSelectedProject(projectId: string) {
    if (!projectId) {
      this.selectedProject = null;
      return;
    }

    for (const p of this.filteredProjects) {
      if (p.id === projectId) {
        this.selectedProject = p;
        return;
      }
    }
    this.alertService.info('Das angegebene Projekt wurde nicht gefunden.');
  }

  projectClicked(project) {
    if (this.selectedProject === project) {
      this.location.replaceState(`/browse`);
      this.selectedProject = null;
      this.scroll = false;
    } else {
      this.location.replaceState(`/browse/${project.id}`);
      this.selectedProject = project;
      this.scroll = true;
    }
  }

  ngAfterViewChecked() {
    if (this.mobile && this.scroll && this.selectedProject) {
      const btn = $(`#${this.selectedProject.id}`);
      // navbar has 56 pixels height
      $('html, body').animate({scrollTop: $(btn).offset().top - 56}, 'slow');
      this.scroll = false;
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
}
