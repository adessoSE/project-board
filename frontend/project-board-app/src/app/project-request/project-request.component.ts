import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-project-request',
  templateUrl: './project-request.component.html',
  styleUrls: ['./project-request.component.scss']
})
export class ProjectRequestComponent implements OnInit {
  project: Project;

  constructor(private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.data.subscribe((data: { project: Project }) => {
      this.project = data.project;
    });
  }

}
