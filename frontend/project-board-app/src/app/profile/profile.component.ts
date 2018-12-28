import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Application, Employee } from '../_services/employee.service';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  user: Employee;
  bookmarks: Project[];
  applications: Application[];
  projects: Project[];

  destroy$ = new Subject<void>();

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.user = data.user;
        this.bookmarks = data.bookmarks;
        this.applications = data.applications;
        this.projects = data.projects;
      });
  }
}