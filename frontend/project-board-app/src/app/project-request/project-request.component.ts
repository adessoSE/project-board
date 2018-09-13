import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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

  constructor(private router: Router, private route: ActivatedRoute, private employeeService: EmployeeService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { project: Project }) => {
      this.project = data.project;
    });
  }

  requestProject() {
    this.employeeService.applyForProject('jacobs', this.project.id, this.comment).subscribe();
  }
}
