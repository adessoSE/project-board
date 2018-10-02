import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { faBookmark } from '@fortawesome/free-regular-svg-icons';
import { faEnvelope } from '@fortawesome/free-regular-svg-icons/faEnvelope';
import { faGraduationCap } from '@fortawesome/free-solid-svg-icons/faGraduationCap';
import * as $ from 'jquery';
import { AlertService } from '../_services/alert.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-user-ui',
  templateUrl: './user-ui.component.html',
  styleUrls: ['./user-ui.component.scss']
})
export class UserUiComponent implements OnInit, AfterViewChecked {
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

  @HostListener('window:resize') onResize() {
    this.mobile = window.screen.width <= 425;
  }

  constructor(private projectsService: ProjectService,
              private alertService: AlertService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location) { }

  ngOnInit() {
    this.mobile = window.screen.width < 768;

    this.route.data.subscribe(data => {
      this.projects = data.projects;
      this.filteredProjects = this.projects;

      // extract projects from applications
      this.appliedProjects = data.applications ? data.applications.map(app => app.project) : [];
      this.bookmarks = data.bookmarks;
      this.route.params.subscribe(params => {
        if (params.id) {
          this.setSelectedProject(params.id);
          if (!this.selectedProject) {
            this.alertService.info('Das angegebene Projekt wurde nicht gefunden.');
          }
        }
      });
    });
  }

  filterProjects(filterInput) {
    const filter = filterInput.toLowerCase().split(' ').filter(e => e.length > 2);
    if (filter.length > 0) {
      this.location.replaceState(`/projects`);
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
    for (const p of this.filteredProjects) {
      if (p.id === projectId) {
        this.selectedProject = p;
        return;
      }
    }
    this.selectedProject = null;
  }

  projectClicked(project) {
    if (this.selectedProject === project) {
      this.location.replaceState(`/projects`);
      this.selectedProject = null;
      this.scroll = false;
    } else {
      this.location.replaceState(`/projects/${project.id}`);
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
