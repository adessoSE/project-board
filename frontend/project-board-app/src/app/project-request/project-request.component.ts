import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-project-request',
  templateUrl: './project-request.component.html',
  styleUrls: ['./project-request.component.scss']
})
export class ProjectRequestComponent implements OnInit {
  project: Project;
  comment: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private employeeService: EmployeeService,
              private authService: AuthenticationService,
              private alertService: AlertService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { project: Project }) => {
      this.project = data.project;
    });
  }

  requestProject() {
    this.employeeService.applyForProject(this.authService.username, this.project.id, this.comment).subscribe(() => {
      this.alertService.success(`Du hast das Projekt mit dem Schlüssel "${this.project.id}" erfolgreich angefragt.`, true);
      this.router.navigate([`/projects/${this.project.id}`]);
      }
    );
  }
}
