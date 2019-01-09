import { formatDate } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Application, Employee, EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';
import { ProjectDialogComponent } from '../project-dialog/project-dialog.component';

export interface EmployeeDialogData {
  employee: Employee;
}

@Component({
  selector: 'app-employee-dialog',
  templateUrl: './employee-dialog.component.html',
  styleUrls: ['./employee-dialog.component.scss']
})
export class EmployeeDialogComponent implements OnInit {
  adminControls = true;
  applications: Application[] = [];
  projects: Project[] = [];

  numberOfDaysSelect = [];
  destroy$ = new Subject<void>();

  closeTooltip = 'Dialog schließen.';
  projectDialogRef: MatDialogRef<ProjectDialogComponent>;

  constructor(
    public dialogRef: MatDialogRef<EmployeeDialogComponent>,
    public projectDialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: EmployeeDialogData,
    private employeeService: EmployeeService) {}

  ngOnInit() {
    for (let i = 1; i < 29; i++) {
      this.numberOfDaysSelect.push(i);
    }
    if (this.data.employee) {
      this.getApplications();
    }
  }

  activate(duration) {
    const accessEnd = new Date();
    accessEnd.setDate(accessEnd.getDate() + Number(duration.value));
    accessEnd.setHours(23, 59, 59, 999);
    const dateString = formatDate(accessEnd, 'yyyy-MM-ddTHH:mm:ss.SSS', 'de');
    this.employeeService.setEmployeeAccessInfo(this.data.employee.id, dateString)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.data.employee.accessInfo = user.accessInfo;
        this.data.employee.duration = duration.value;
      });
  }

  deactivate() {
    this.employeeService.deleteEmployeeAccessInfo(this.data.employee.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.data.employee.accessInfo = user.accessInfo;
        this.data.employee.duration = 0;
      });
  }

  getApplications() {
    this.employeeService.getApplications(this.data.employee.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(applications => this.applications = applications);
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
}
