import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, Employee, EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';
import { ProjectDialogComponent } from '../project-dialog/project-dialog.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  @Input() adminControls = true;
  @Input() bookmarks: Project[] = [];
  @Input() applications: Application[] = [];
  @Input() projects: Project[] = [];
  employees: Employee[] = [];
  employeeApplications: Application[] = [];

  user: Employee;
  tabIndex = 0;
  mobile = false;
  loadingEmployeeApplications = true;

  dialogRef: MatDialogRef<ProjectDialogComponent>;

  destroy$ = new Subject<void>();

  constructor(private route: ActivatedRoute,
              private employeeService: EmployeeService,
              private authService: AuthenticationService,
              public dialog: MatDialog) {}

  ngOnInit() {
    this.mobile = document.body.clientWidth < 992;

    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.user = data.user;
        this.bookmarks = data.bookmarks;
        this.applications = data.applications;
        this.projects = data.projects;
      });

    if (this.user.boss) {
      this.tabIndex = 2;
      this.getEmployees();
      this.getEmployeeApplications();
    }
  }

  openDialog(p: Project) {
    this.dialogRef = this.dialog.open(ProjectDialogComponent, {
      autoFocus: false,
      panelClass: 'custom-dialog-container',
      data: {
        project: p,
        applicable: this.isProjectApplicable(p.id),
        bookmarked: this.isProjectBookmarked(p.id),
        isUserBoss: this.user.boss
      }
    });
    this.dialogRef.componentInstance.bookmark
      .pipe(takeUntil(this.dialogRef.afterClosed()))
      .subscribe(() => this.handleBookmark(p));
    this.dialogRef.componentInstance.application
      .pipe(takeUntil(this.dialogRef.afterClosed()))
      .subscribe(application => this.handleApplication(application));
  }

  selectTab(tab) {
    this.tabIndex = tab;
    $('.active').removeClass('active');
    document.getElementById('tab' + tab).classList.add('active');
  }

  getEmployees() {
    this.employeeService.getEmployeesForSuperUser(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(employees => this.employees = employees);
  }

  removeBookmark(projectId) {
    this.employeeService.removeBookmark(this.authService.username, projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.bookmarks = this.bookmarks.filter(p => p.id !== projectId));
  }

  getEmployeeApplications() {
    this.loadingEmployeeApplications = true;
    this.employeeService.getApplicationsForEmployeesOfUser(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(employeeApplications => {
        this.employeeApplications = employeeApplications;
        this.loadingEmployeeApplications = false;
      });
  }


  isProjectApplicable(projectId: string) {
    return this.projects ? !this.projects.some(p => p && p.id === projectId) : true;
  }

  isProjectBookmarked(projectId: string) {
    return this.bookmarks ? this.bookmarks.some(p => p && p.id === projectId) : false;
  }

  handleBookmark(project: Project) {
    const index = this.bookmarks.findIndex(p => p.id === project.id);
    if (index > -1) {
      this.bookmarks.splice(index, 1);
    } else {
      this.bookmarks.push(project);
    }
  }

  handleApplication(application: Application) {
    this.applications.push(application);
  }
}
