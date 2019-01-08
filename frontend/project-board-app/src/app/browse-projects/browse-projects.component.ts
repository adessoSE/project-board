import { Location } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Subject } from 'rxjs';
import { debounceTime, switchMap, takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { Application, EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';
import { ProjectDialogComponent } from '../project-dialog/project-dialog.component';

@Component({
  selector: 'app-browse-projects',
  templateUrl: './browse-projects.component.html',
  styleUrls: ['./browse-projects.component.scss']
})
export class BrowseProjectsComponent implements OnInit {

  appTooltip = 'Du hast dieses Projekt bereits angefragt.';
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';

  projects: Project[] = [];
  applications: Application[] = [];
  bookmarks: Project[] = [];
  selectedProject: Project;
  mobile = false;
  isUserBoss = false;
  searchText = '';
  loadingProjects = true;
  projectsFound: number;

  dialogRef: MatDialogRef<ProjectDialogComponent>;

  private divToScroll;

  destroy$ = new Subject<void>();
  private searchText$ = new Subject<string>();

  constructor(private employeeService: EmployeeService,
              private projectsService: ProjectService,
              private alertService: AlertService,
              private route: ActivatedRoute,
              private location: Location,
              public dialog: MatDialog
  ) {}

  openDialog(p: Project) {
    this.dialogRef = this.dialog.open(ProjectDialogComponent, { autoFocus: false, panelClass: 'custom-dialog-container',
      data: {
        project: p,
        applicable: this.isProjectApplicable(p.id),
        bookmarked: this.isProjectBookmarked(p.id),
        isUserBoss: this.isUserBoss
      }
    });
    this.dialogRef.componentInstance.bookmark
      .pipe(takeUntil(this.dialogRef.afterClosed()))
      .subscribe(() => this.handleBookmark(p));
    this.dialogRef.componentInstance.application
      .pipe(takeUntil(this.dialogRef.afterClosed()))
      .subscribe(application => this.handleApplication(application));
    this.dialogRef.afterClosed().subscribe(() => this.onDialogClosed());

    this.location.replaceState(`/browse/${p.id}`);
  }

  @HostListener('window:resize') onResize() {
    this.mobile = document.body.clientWidth < 992;
  }

  swipebugplaceholder() {}

  loadProjects() {
    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        if (data[0].projects) {
          this.projects = data[0].projects;
          this.loadingProjects = false;
        }

        // extract projects from applications
        this.applications = data[0].applications;
        this.bookmarks = data[0].bookmarks;
        this.isUserBoss = data[0].isUserBoss;

        // set selected project
        this.setSelectedProject(data[1].id);

        if (this.selectedProject) {
          this.openDialog(this.selectedProject);
        }
      });
  }

  ngOnInit() {
    this.mobile = document.body.clientWidth < 992;

    this.searchText$
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(500),
        switchMap(searchText => {
          return this.projectsService.search(searchText);
        }))
      .subscribe(projects => {
        this.loadingProjects = false;
        this.projects = projects;
        this.projectsFound = projects.length;
      });
    this.loadProjects();

    this.divToScroll = document.getElementById('divToScroll');
  }

  searchProjects() {
    this.loadingProjects = true;
    this.projects = [];
    this.searchText$.next(this.searchText);
  }

  private setSelectedProject(projectId: string) {
    if (!projectId) {
      this.selectedProject = null;
      return;
    }
    for (const p of this.projects) {
      if (p.id === projectId) {
        this.selectedProject = p;
        return;
      }
    }
    this.alertService.info('Das angegebene Projekt wurde nicht gefunden.');
  }

  isProjectApplicable(projectId: string) {
    return this.applications ? !this.applications.some(a => a && a.project.id === projectId) : true;
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

  onDialogClosed() {
    this.selectedProject = null;
    this.location.replaceState('/browse');
  }
}
