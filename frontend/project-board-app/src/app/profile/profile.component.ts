import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
import { Subject, combineLatest } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Application, Employee, EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';
import { formatDate } from '@angular/common';
import { Input, OnChanges, SimpleChanges } from '@angular/core';
import { AuthenticationService } from '../_services/authentication.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit, OnChanges {
  @Input() adminControls = true;
  @Input() bookmarks: Project[] = [];
  @Input() applications: Application[] = [];
  @Input() projects: Project[] = [];
  employees: Employee[] = [];

  user: Employee;
  tabIndex: number;

  destroy$ = new Subject<void>();

  constructor(private route: ActivatedRoute,
    private employeeService: EmployeeService,
    private authService: AuthenticationService, ) 
    {
      this.tabIndex = 0;
    }

  ngOnInit() {

    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.user = data.user;
        this.bookmarks = data.bookmarks;
        this.applications = data.applications;
        this.projects = data.projects;
      });

      if(this.user.boss) {
        this.tabIndex = 2;
      }

      if(this.user.boss) {
        this.getEmployees();
      }

      if (!this.adminControls) {
        this.getBookmarks();
      }

      if (this.user) {
        this.getApplications();
      }
  }

  selectTab(tab) {
    this.tabIndex = tab;
    $('.active').removeClass('active');
    document.getElementById("tab" + tab).classList.add('active');
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.user.currentValue && !changes.user.isFirstChange()) {
      if (!this.adminControls) {
        this.getBookmarks();
      }
      this.getApplications();
    }
  }

  activate(duration) {
    const accessEnd = new Date();
    accessEnd.setDate(accessEnd.getDate() + Number(duration.value));
    accessEnd.setHours(23, 59, 59, 999);
    const dateString = formatDate(accessEnd, 'yyyy-MM-ddTHH:mm:ss.SSS', 'de');
    this.employeeService.setEmployeeAccessInfo(this.user.id, dateString)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.user.accessInfo = user.accessInfo;
        this.user.duration = duration.value;
      });
  }

  deactivate() {
    this.employeeService.deleteEmployeeAccessInfo(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.user.accessInfo = user.accessInfo;
        this.user.duration = 0;
      });
  }

  getEmployees() {
    this.employeeService.getEmployeesForSuperUser(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(employees => this.employees = employees);
  }

  getBookmarks() {
    this.employeeService.getBookmarks(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(bookmarks => this.bookmarks = bookmarks);
  }

  removeBookmark(projectId) {
    this.employeeService.removeBookmark(this.authService.username, projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {this.bookmarks = this.bookmarks.filter(p => p.id != projectId);});
  }

  getApplications() {
    this.employeeService.getApplications(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(appls => this.applications = appls);
  }

  isAdmin() {
    return this.authService.isAdmin;
  }
}
