import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Application, Employee } from '../_services/employee.service';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit {
  user: Employee;
  bookmarks: Project[];
  applications: Application[];

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.data.subscribe(data => {
      this.user = data.user;
      this.bookmarks = data.bookmarks;
      this.applications = data.applications;
    });
  }
}
