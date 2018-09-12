import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  @Input() selectedProject: Project;
  favorite = false;

  constructor(private router: Router,
              private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  ngOnInit() {
  }

  requestProject() {
    this.router.navigate([`projects/${this.selectedProject.id}/request`]);
  }

  addToFavorites() {
    this.employeeService.addToFavorites(this.authService.username, this.selectedProject.id).subscribe(() => this.favorite = true);
  }

  removeFromFavorites() {
    this.employeeService.removeFromFavorites(this.authService.username, this.selectedProject.id).subscribe(() => this.favorite = false);
  }
}
