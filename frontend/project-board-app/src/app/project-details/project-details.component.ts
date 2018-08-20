import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  @Input() selectedProject: Project;

  constructor() { }

  ngOnInit() {
  }
}
