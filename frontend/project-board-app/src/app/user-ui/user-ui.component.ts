import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { faBookmark } from '@fortawesome/free-regular-svg-icons';
import { faEnvelope } from '@fortawesome/free-regular-svg-icons/faEnvelope';
import * as $ from 'jquery';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-user-ui',
  templateUrl: './user-ui.component.html',
  styleUrls: ['./user-ui.component.scss']
})
export class UserUiComponent implements OnInit, AfterViewChecked {
  appTooltip = 'Du hast dieses Projekt bereits angefragt.';
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  faEnvelope = faEnvelope;
  faBookmark = faBookmark;

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
              private route: ActivatedRoute,
              private router: Router,
              private location: Location) { }

  ngOnInit() {
    this.mobile = window.screen.width < 768;

    this.route.data.subscribe(data => {
      this.projects = data.projects;
      this.filteredProjects = this.projects;

      // extract projects from applications
      this.appliedProjects = data.applications.map(app => app.project);

      this.bookmarks = data.bookmarks;
      this.route.params.subscribe(params => {
        if (params.key) {
          this.setSelectedProject(params.key);
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
            if (o = filtered.find(e => e.project.key === p.key)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
          if (p.job && p.job.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.key === p.key)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
          if (p.description && p.description.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.key === p.key)) {
              o.hitCount++;
            } else {
              filtered.push({'project': p, 'hitCount': 1});
            }
          }
          if (p.skills && p.skills.toLowerCase().includes(f)) {
            let o;
            if (o = filtered.find(e => e.project.key === p.key)) {
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
    for (const p of this.projects) {
      if (p.key === projectId) {
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
      this.location.replaceState(`/projects/${project.key}`);
      this.selectedProject = project;
      this.scroll = true;
    }
  }

  ngAfterViewChecked() {
    if (this.mobile && this.scroll && this.selectedProject) {
      const btn = $(`#${this.selectedProject.key}`);
      // navbar has 56 pixels height
      $('html, body').animate({scrollTop: $(btn).offset().top - 56}, 'slow');
      this.scroll = false;
    }
  }

  isProjectApplicable(key) {
    return !this.appliedProjects.some(p => p && p.key === key);
  }

  isProjectBookmarked(key) {
    return this.bookmarks.some(p => p && p.key === key);
  }

  handleBookmark(project) {
    const index = this.bookmarks.findIndex(p => p.key === project.key);
    if (index > -1) {
      this.bookmarks.splice(index, 1);
    } else {
      this.bookmarks.push(project);
    }
  }
}
