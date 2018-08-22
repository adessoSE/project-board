import { Location } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  @Input() selectedProject: Project;

  constructor(private router: Router, private location: Location) { }

  ngOnInit() {
  }

  requestProject() {
    this.router.navigate([`${this.location.path()}/request`]);
  }
}
