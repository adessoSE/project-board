import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, Employee, EmployeeService, State } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';
import { EmployeeDialogComponent } from '../employee-dialog/employee-dialog.component';
import { ProjectDialogComponent } from '../project-dialog/project-dialog.component';
import { SafetyqueryDialogComponent } from '../safetyquery-dialog/safetyquery-dialog.component';
import {FormControl} from '@angular/forms';
import {TooltipPosition} from '@angular/material';

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
  filteredEmployeeApplications: Application[] = [];
  unreadApplications: string[];

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];
  position = new FormControl(this.positionOptions[2]);

  user: Employee;
  tabIndex = 0;
  mobile = false;
  loadingEmployeeApplications = true;

  dialogRef: MatDialogRef<ProjectDialogComponent>;

  destroy$ = new Subject<void>();

  constructor(private route: ActivatedRoute,
              private employeeService: EmployeeService,
              private projectService: ProjectService,
              private authService: AuthenticationService,
              public dialog: MatDialog) {}

  ngOnInit(): void {
    this.mobile = document.body.clientWidth < 992;

    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.user = data.user;
        this.bookmarks = data.bookmarks;
        this.applications = data.applications;
      });

    if (this.user.boss) {
      this.tabIndex = 2;
      this.getEmployees();
      this.getEmployeeApplications();
    }
  }

  /* Common Functions with Browse-Projects - start */

  /* Tested Methods Start */

  isProjectApplicable(projectId: string): boolean {
    return this.employeeService.isApplicable(this.applications, projectId);
  }

  isProjectBookmarked(projectId: string): boolean {
    return this.projectService.isBookmarked(this.bookmarks, projectId);
  }

  /* Tested Methods End */

  handleBookmark(project: Project): void {
    const index = this.bookmarks.findIndex(p => p.id === project.id);
    if (index > -1) {
      this.bookmarks.splice(index, 1);
    } else {
      this.bookmarks.push(project);
    }
  }

  handleApplication(application: Application): void {
    this.applications.push(application);
  }

  /* Common Functions with Browse-Projects - end */

  openDialog(p: Project): void {
    this.dialogRef = this.dialog.open(
      ProjectDialogComponent,
      {
        autoFocus: false,
        panelClass: 'custom-dialog-container',
        data: {
          project: p,
          applicable: this.isProjectApplicable(p.id),
          bookmarked: this.isProjectBookmarked(p.id),
          isUserBoss: this.user.boss,
          hasAccess:  this.user.accessInfo.hasAccess
        }
      });
    this.dialogRef.componentInstance.bookmark
      .pipe(takeUntil(this.dialogRef.afterClosed()))
      .subscribe(() => this.handleBookmark(p));
    this.dialogRef.componentInstance.application
      .pipe(takeUntil(this.dialogRef.afterClosed()))
      .subscribe(application => this.handleApplication(application));
  }

  openEmployeeDialog(employee: Employee): void {
    this.employeeService.getFullEmployeeForId(employee.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(fullEmployee => {
        this.dialog.open(
          EmployeeDialogComponent,
          {
            autoFocus: false,
            panelClass: 'custom-dialog-container',
            data: {
              employee: fullEmployee
            }
          });
      });
  }

  selectTab(tab): void {
    this.tabIndex = tab;
    $('.active').removeClass('active');
    document.getElementById('tab' + tab).classList.add('active');
  }

  getEmployees(): void {
    this.employeeService.getEmployeesWithoutPicturesForSuperUser(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(employees => this.employees = employees);
  }

  removeBookmark(projectId): void {
    this.employeeService.removeBookmark(this.authService.username, projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.bookmarks = this.bookmarks.filter(p => p.id !== projectId));
  }

  getEmployeeApplications(): void {
    this.loadingEmployeeApplications = true;
    this.employeeService.getApplicationsForEmployeesOfUser(this.user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(employeeApplications => {
        this.employeeApplications = employeeApplications;
        this.filteredEmployeeApplications = employeeApplications;
        this.loadingEmployeeApplications = false;
      });
  }
  removeApplication(applicationId : number) {
    this.changeStateOfApplication(applicationId, State.DELETED)
  }

  getUndeleted(): number {
    return this.applications.filter(a => a.state !== State.DELETED).length;
  }

  markAsRead(application: Application) {
    if(application.state === "NEW"){
    this.changeStateOfApplication(application.id, State.NONE);
    }
  }

  changeStateOfApplication(applicationId : number, state :State){
      if(state === "DELETED"){
        let dialogRef = this.dialog.open(SafetyqueryDialogComponent, {
          disableClose: false
        });
         dialogRef.afterClosed().subscribe(result => {
           if(result) {
            this.employeeService.changeApplicationState(this.employeeApplications.find(x => x.id === applicationId).user.id, applicationId, state)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => this.employeeApplications = this.employeeApplications.filter(p => p.id !== applicationId));
           }
        });
      }
      if(state === "NONE") {
        this.employeeService.changeApplicationState(this.employeeApplications.find(x => x.id === applicationId).user.id, applicationId, state)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => this.employeeApplications.find(x => x.id === applicationId).state = State.NONE);
      }
  }


  toggleFilter() {
    if (this.filteredEmployeeApplications.length === this.employeeApplications.length) {
      this.filteredEmployeeApplications = this.employeeApplications.filter(app => app.state === "NEW");
    } else {
      this.filteredEmployeeApplications = this.employeeApplications;
    }
  }
}
