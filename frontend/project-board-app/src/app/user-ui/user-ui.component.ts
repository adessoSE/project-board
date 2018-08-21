import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as $ from 'jquery';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-user-ui',
  templateUrl: './user-ui.component.html',
  styleUrls: ['./user-ui.component.scss']
})
export class UserUiComponent implements OnInit, AfterViewChecked {
  projects: Project[] = [];
  selectedProject: Project;
  mobile = false;
  scroll = true;

  @HostListener('window:resize') onResize() {this.mobile = window.screen.width <= 425;}

  constructor(private projectsService: ProjectService, private route: ActivatedRoute, private router: Router, private location: Location) { }

  ngOnInit() {
    this.mobile = window.screen.width < 768;

    this.route.data.subscribe((data: { projects: Project[] }) => {
      this.projects = data.projects;
      this.route.params.subscribe(params => {
        if (params.id) {
          this.setSelectedProject(params.id);
        }
      });
    });
  }

  private setSelectedProject(projectId: string) {
    for (let p of this.projects) {
      if (p.id == projectId) {
        this.selectedProject = p;
        return;
      }
    }
    this.selectedProject = null;
  }

  projectClicked(project) {
    if(this.selectedProject == project) {
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
      let btn = $(`#${this.selectedProject.id}`);
      $('html, body').animate({scrollTop: $(btn).offset().top}, 'slow');
      this.scroll = false;
    }
  }
}
