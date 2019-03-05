import { formatDate } from '@angular/common';
import { Component, HostListener, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { NavigationStart, Router } from '@angular/router';
import { Moment } from 'moment';
import { combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, Employee, EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';
import { DatepickerHeaderComponent } from '../datepicker-header/datepicker-header.component';
import { ProjectDialogComponent } from '../project-dialog/project-dialog.component';
import { CLOSE_DIALOG_TOOLTIP } from '../tooltips';

export interface EmployeeDialogData {
  employee: Employee;
}

export const CUSTOM_FORMATS = {
  parse: {
    dateInput: 'DD.MM.YYYY'
  },
  display: {
    dateInput: 'DD.MM.YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'DD.MM.YYYY',
    monthYearA11yLabel: 'MMMM YYYY'
  }
};

@Component({
  selector: 'app-employee-dialog',
  templateUrl: './employee-dialog.component.html',
  styleUrls: ['./employee-dialog.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: CUSTOM_FORMATS}
  ]
})
export class EmployeeDialogComponent implements OnInit {
  applications: Application[] = [];
  projects: Project[] = [];
  datepickerControl = new FormControl(new Date());

  daysAlready: number;
  daysLeft: number;

  destroy$ = new Subject<void>();

  minDate: Date;
  maxDate: Date;
  mobile: boolean;
  customHeaderClass = DatepickerHeaderComponent;
  filteredApplications: Application[] = [];
  isDirectEmployee = false;

  closeTooltip = CLOSE_DIALOG_TOOLTIP;
  projectDialogRef: MatDialogRef<ProjectDialogComponent>;

  constructor(
    public dialogRef: MatDialogRef<EmployeeDialogComponent>,
    public projectDialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: EmployeeDialogData,
    private employeeService: EmployeeService,
    private authService: AuthenticationService,
    private router: Router
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.dialogRef.close();
      }
    });
  }

  @HostListener('window:resize')
  onResize() {
    this.mobile = document.body.clientWidth < 992;
  }

  daysInMonth(){
    let date: Date = new Date();
    return new Date(date.getFullYear(), date.getMonth()+1, 0).getDate();
  }

  ngOnInit() {
    console.log("Test: " + this.daysInMonth());

    this.mobile = document.body.clientWidth < 992;
    this.minDate = new Date(Date.now());
    // 86400000 milliseconds are 1 day
    this.maxDate = new Date(this.minDate.getTime() + 86400000*this.daysInMonth());

    this.daysAlready = this.daysUntil(this.data.employee.accessInfo.accessStart);
    this.daysLeft = this.daysUntil(this.data.employee.accessInfo.accessEnd);

    // set the value of the datepicker to the date of the employees access end
    if (this.data.employee.accessInfo.hasAccess) {
      this.datepickerControl.setValue(new Date(this.data.employee.accessInfo.accessEnd));
    } else {
      this.datepickerControl.setValue(this.maxDate);
    }

    if (this.data.employee) {
      this.getApplications();
    }
  }

  activate() {
    const accessEnd = (this.datepickerControl.value instanceof Date) ?
      this.datepickerControl.value :
      (this.datepickerControl.value as Moment).toDate();
    accessEnd.setHours(23, 59, 59, 999);
    const dateString = formatDate(accessEnd, 'yyyy-MM-ddTHH:mm:ss.SSS', 'de');
    this.employeeService.setEmployeeAccessInfo(this.data.employee.id, dateString)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.data.employee.accessInfo = user.accessInfo;
        this.data.employee.duration = this.daysUntil(accessEnd);

        this.daysAlready = this.daysUntil(this.data.employee.accessInfo.accessStart);
        this.daysLeft = this.daysUntil(this.data.employee.accessInfo.accessEnd);
      });
  }

  deactivate() {
    this.employeeService.deleteEmployeeAccessInfo(this.data.employee.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.data.employee.accessInfo = user.accessInfo;
        this.data.employee.duration = 0;
        this.datepickerControl.setValue(this.maxDate);
      });
  }

  daysUntil(date: Date): number {
    date = new Date(date);
    let diff = date.getTime() < Date.now() ?
      Date.now() - date.getTime() :
      date.getTime() - Date.now();
    diff = Math.floor(diff / (1000 * 60 * 60 * 24));
    return diff;
  }

  getApplications() {
    combineLatest(
      this.employeeService.getEmployeesWithoutPicturesForSuperUser(this.authService.username)
        .pipe(takeUntil(this.destroy$)),
      this.employeeService.getApplications(this.data.employee.id)
        .pipe(takeUntil(this.destroy$))
    ).subscribe(([staff, applications]) => {
      this.isDirectEmployee = staff.some(employee => this.data.employee.id === employee.id);
      this.applications = applications;
      this.filteredApplications = applications;
    });
  }

  openProjectDialog(p: Project) {
    this.projectDialogRef = this.projectDialog.open(ProjectDialogComponent, {
      autoFocus: false,
      panelClass: 'custom-dialog-container',
      data: {
        project: p,
        applicable: false,
        bookmarked: false,
        isUserBoss: true
      }
    });
  }

  markAsRead(application: Application) {
    if (!application.readByBoss) {
      this.employeeService.markApplicationAsRead(this.data.employee.id, application.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => application.readByBoss = true);
    }
  }

  toggleFilter() {
    if (this.filteredApplications.length === this.applications.length) {
      this.filteredApplications = this.applications.filter(app => app.readByBoss === false);
    } else {
      this.filteredApplications = this.applications;
    }
  }
}
