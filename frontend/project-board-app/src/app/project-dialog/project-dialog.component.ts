import { Component, EventEmitter, HostListener, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { NavigationStart, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';
import { AlertService } from '../_services/alert.service';
import {
  ABORT_REQUEST_TOOLTIP,
  ADD_BOOKMARK_TOOLTIP,
  APPLICATION_TOOLTIP,
  CLOSE_DIALOG_TOOLTIP,
  GO_TO_JIRA_TOOLTIP,
  REMOVE_BOOKMARK_TOOLTIP,
  SEND_REQUEST_TOOLTIP,
  START_REQUEST_TOOLTIP
} from '../tooltips';

import {FormControl} from '@angular/forms';
import {TooltipPosition} from '@angular/material';

export interface ProjectDialogData {
  project: Project;
  isUserBoss: boolean;
  bookmarked: boolean;
  applicable: boolean;
  hasAccess: boolean;
}

@Component({
  selector: 'app-project-dialog',
  templateUrl: './project-dialog.component.html',
  styleUrls: ['./project-dialog.component.scss']
})
export class ProjectDialogComponent implements OnInit {
  appTooltip = APPLICATION_TOOLTIP;
  bmAddTooltip = ADD_BOOKMARK_TOOLTIP;
  bmRemoveTooltip = REMOVE_BOOKMARK_TOOLTIP;
  closeTooltip = CLOSE_DIALOG_TOOLTIP;
  jiraTooltip = GO_TO_JIRA_TOOLTIP;
  startRequestTooltip = START_REQUEST_TOOLTIP;
  abortRequestTooltip = ABORT_REQUEST_TOOLTIP;
  sendRequestTooltip = SEND_REQUEST_TOOLTIP;

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];
  position = new FormControl(this.positionOptions[2]);
  mobile: boolean;
  @Output() bookmark = new EventEmitter();
  @Output() application = new EventEmitter<Application>();

  destroy$ = new Subject<void>();

  showBox = false;
  comment = '';

  constructor(
    public dialogRef: MatDialogRef<ProjectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectDialogData,
    private authService: AuthenticationService,
    private employeeService: EmployeeService,
    private router: Router,
    private alertService: AlertService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.dialogRef.close();
      }
    });
  }

  ngOnInit(): void {
    this.mobile = document.body.clientWidth < 992;
    this.data.project.skills = this.cleanUpString(this.data.project.skills);
    this.data.project.description = this.cleanUpString(this.data.project.description);
    this.data.project.job = this.cleanUpString(this.data.project.job);
    this.data.project.title = this.cleanUpString(this.data.project.title);
    this.data.project.operationStart = this.cleanUpString(this.data.project.operationStart);
    this.data.project.operationEnd = this.cleanUpString(this.data.project.operationEnd);
  }

  @HostListener('window:resize')
  onResize(): void {
    this.mobile = document.body.clientWidth < 992;
  }

  cleanUpString(string: string): string {
    if (string) {
      string = string.replace(/\t{2,}/, '\t');
      string = string.replace(/ {2,}/, ' ');
      string = string.replace(/\n{3,}/gm, '\n\n');
      string = string.replace(/\s*$/, '');
      string = string.replace(/^\s*/, '');
      string = string.replace(/,$/, '');
      string = string.replace(/,[^ ]/, ', ');
      string = string.replace(/[ \t]*,/, ',');
      return string;
    }
    return null;
  }

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
        this.alertService.success('Deine Anfrage wurde versendet.');
      });
  }

  goToJira() {
    window.open(`https://jira.adesso.de/browse/${this.data.project.id}`, 'blank');
  }
}
