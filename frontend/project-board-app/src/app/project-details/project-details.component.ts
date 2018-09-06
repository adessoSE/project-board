import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  @Input() selectedProject: Project;

  constructor(private router: Router, private projectService: ProjectService) { }

  ngOnInit() {
  }

  requestProject() {
    this.router.navigate([`projects/${this.selectedProject.id}/request`]);
  }

  addToFavorites() {
    // TODO: receive real userId from somewhere
    this.projectService.addToFavorites(1, this.selectedProject.id).subscribe(r => console.log(r));
  }
}
