import { Location } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MatIconRegistry } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
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
  sortValue: number;
  sortMemory: number;
  toggle = true;
  dialogRef: MatDialogRef<ProjectDialogComponent>;

  private divToScroll;

  destroy$ = new Subject<void>();
  private searchText$ = new Subject<string>();

  constructor(private employeeService: EmployeeService,
              private matIconRegistry: MatIconRegistry,
              private projectsService: ProjectService,
              private domSanitizer: DomSanitizer,
              private alertService: AlertService,
              private route: ActivatedRoute,
              private location: Location,
              public dialog: MatDialog
  ) {}

  openDialog(p: Project) {
    this.dialogRef = this.dialog.open(ProjectDialogComponent, {
      autoFocus: false,
      panelClass: 'custom-dialog-container',
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
    this.matIconRegistry.addSvgIcon(
      'sort_ascending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/long-arrow-alt-up-solid.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'sort_descending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/long-arrow-alt-down-solid.svg')
    );

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

  sortByLocation(memory: number) {
    if (this.sortMemory !== memory) {
      this.sortValue = 0;
      this.sortMemory = memory;
    }

    if (this.sortValue === 0 || this.sortValue === undefined) {
      this.projects.sort((a: Project, b: Project) => a.location.localeCompare(b.location));
      this.sortValue = 1;
    } else if (this.sortValue === 1) {
      this.projects.sort((a: Project, b: Project) => b.location.localeCompare(a.location));
      this.sortValue = 2;
    } else {
      this.projects.sort((a: Project, b: Project) => {
        return Number(new Date(b.updated)) - Number(new Date(a.updated));
      });
      this.sortValue = 0;
    }
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

  /* Common Functions with Profile - start */

  isProjectApplicable(projectId: string) {
    return this.employeeService.isApplicable(this.applications, projectId);
  }

  isProjectBookmarked(projectId: string) {
    return this.projectsService.isBookmarked(this.bookmarks, projectId);
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

  /* Common Functions with Profile - end */

  onDialogClosed() {
    this.selectedProject = null;
    this.location.replaceState('/browse');
  }

  @HostListener('window:scroll') onScroll() {
    if (!this.mobile) {
      if (((document.getElementById('total-hits').offsetTop - window.scrollY + 60) === 0) && this.toggle) {
        $('#result-table > thead th').css('-webkit-box-shadow', 'inset 0 -1px 1px -1px rgba(128,128,128, 0.6)');
        $('#result-table > thead th').css('-moz-box-shadow', 'inset 0 -1px 1px -1px rgba(128,128,128, 0.6)');
        $('#result-table > thead th').css('box-shadow', 'inset 0 -1px 1px -1px rgba(128,128,128, 0.6)');
        this.toggle = false;
      } else if (!this.toggle && ((document.getElementById('total-hits').offsetTop - window.scrollY + 60) !== 0)) {
        $('#result-table > thead th').css('-webkit-box-shadow', 'none');
        $('#result-table > thead th').css('-moz-box-shadow', 'none');
        $('#result-table > thead th').css('box-shadow', 'none');
        this.toggle = true;
      }
    }
  }

}
