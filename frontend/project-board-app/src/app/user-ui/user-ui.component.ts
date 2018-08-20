import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { $ } from 'jquery';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-user-ui',
  templateUrl: './user-ui.component.html',
  styleUrls: ['./user-ui.component.scss']
})
export class UserUiComponent implements OnInit {
  projects: Project[] = [];
  selectedProject: Project;
  mobile = false;

  constructor(private projectsService: ProjectService, private route: ActivatedRoute, private router: Router, private location: Location) { }

  ngOnInit() {
    this.mobile = window.screen.width <= 425;

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

  projectClicked(project, btn: HTMLButtonElement) {
    this.selectedProject = project;
    // this.router.navigate([`/projects/${project.id}`]);
    this.location.replaceState(`/projects/${project.id}`);
    console.log(btn);
    if(this.mobile) {
      btn.scrollIntoView(true);
      // btn.scrollIntoView({block: 'start', behavior: 'smooth'});
    }
  }
}
