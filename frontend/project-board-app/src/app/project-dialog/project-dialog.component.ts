import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';

export interface DialogData {
  project: Project;
  isUserBoss: boolean;
  bookmarked: boolean;
  applicable: boolean;
}

@Component({
  selector: 'app-project-dialog',
  templateUrl: './project-dialog.component.html',
  styleUrls: ['./project-dialog.component.scss']
})
export class ProjectDialogComponent implements OnInit {
  appTooltip = 'Du hast dieses Projekt bereits angefragt.';
  bmAddTooltip = 'Lesezeichen hinzufügen.';
  bmRemoveTooltip = 'Lesezeichen entfernen.';
  closeTooltip = 'Dialog schließen.';
  mobile: boolean;
  @Output() bookmark = new EventEmitter();
  @Output() application = new EventEmitter<Application>();

  destroy$ = new Subject<void>();

  showBox = false;
  comment = '';

  ngOnInit() {
    this.mobile = document.body.clientWidth < 992;
  }

  constructor(
    public dialogRef: MatDialogRef<ProjectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private authService: AuthenticationService,
    private employeeService: EmployeeService) {}

  toggleRequestArea() {
    this.showBox = !this.showBox;
  }

  bookmarkClicked() {
    let obs;
    if (this.data.bookmarked) {
      obs = this.employeeService.removeBookmark(this.authService.username, this.data.project.id)
        .pipe(takeUntil(this.destroy$));
    } else {
      obs = this.employeeService.addBookmark(this.authService.username, this.data.project.id)
        .pipe(takeUntil(this.destroy$));
    }
    obs.subscribe(() => {
      this.data.bookmarked = !this.data.bookmarked;
      this.bookmark.emit();
    });
  }

  sendApplication() {
    this.employeeService.applyForProject(this.authService.username, this.data.project.id, this.comment)
      .pipe(takeUntil(this.destroy$))
      .subscribe(application => {
        this.application.emit(application);
        this.dialogRef.close();
      });
  }
}
