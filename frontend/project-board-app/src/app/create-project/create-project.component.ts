import { Component, OnInit } from '@angular/core';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.scss']
})
export class CreateProjectComponent implements OnInit {
  id: number;
  effort: number;

  labels = [];

  description = '';
  title = '';
  key = '';
  issuetype = '';
  job = '';
  lob = '';
  customer = '';
  location = '';
  operationStart = '';
  operationEnd = '';
  skills = '';
  status = '';
  elongation = '';
  freelancer = '';
  other = '';

  constructor(private projectService: ProjectService) { }

  ngOnInit() {
  }

  createProject() {
    const project: Project = {
      'id': this.id,
      'effort': this.effort,

      'labels': this.labels,

      'description': this.description,
      'title': this.title,
      'key': this.key,
      'issuetype': this.issuetype,
      'job': this.job,
      'lob': this.lob,
      'customer': this.customer,
      'location': this.location,
      'operationStart': this.operationStart,
      'operationEnd': this.operationEnd,
      'skills': this.skills,
      'status': this.status,
      'elongation': this.elongation,
      'freelancer': this.freelancer,
      'other': this.other,
      'created': new Date(),
      'updated': new Date()
    };

    console.log(project);
    console.log('TODO: implement create project');
    // TODO: make the HTTP request to create a project in the backend
    // this.projectService.createProject(project).subscribe();
  }
}
