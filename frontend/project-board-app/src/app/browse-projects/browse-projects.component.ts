import { Location } from '@angular/common';
import { Component, HostListener, OnInit, AfterViewInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MatIconRegistry } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
import { combineLatest, Subject, ReplaySubject } from 'rxjs';
import { debounceTime, switchMap, takeUntil, take } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { Application, EmployeeService } from '../_services/employee.service';
import { ProjectLocation } from '../_services/project-location.service';
import { Project, ProjectService } from '../_services/project.service';
import { ProjectDialogComponent } from '../project-dialog/project-dialog.component';
import { APPLICATION_TOOLTIP, BOOKMARK_TOOLTIP, SEARCH_INFO_TOOLTIP } from '../tooltips';

import { FormControl } from '@angular/forms';
import { MatSelect } from '@angular/material';
import { CITIES } from './cities'

@Component({
  selector: 'app-browse-projects',
  templateUrl: './browse-projects.component.html',
  styleUrls: ['./browse-projects.component.scss']
})
export class BrowseProjectsComponent implements OnInit {

  /** list of cities */
  protected cities: string[] = CITIES;

  /** control for the selected city */
  public cityControl: FormControl = new FormControl();

  /** control for the MatSelect filter keyword */
  public cityFilterCtrl: FormControl = new FormControl();

  /** list of cities filtered by search keyword */
  public filteredCities: ReplaySubject<string[]> = new ReplaySubject<string[]>(1);

  @ViewChild('singleSelect') singleSelect: MatSelect;

  /** Subject that emits when the component has been destroyed. */
  protected _onDestroy = new Subject<void>();

  appTooltip = 'Du hast dieses Projekt bereits angefragt.';
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  infoTooltip = SEARCH_INFO_TOOLTIP;

  projects: Project[] = [];
  filteredProjects: Project[] = [];
  applications: Application[] = [];
  bookmarks: Project[] = [];
  selectedProject: Project;
  mobile = false;
  isUserBoss = false;
  searchText = '';
  selectedRange = '0'
  loadingProjects = true;
  projectsFound: number;
  sortValue: number;
  sortMemory: number;
  toggle = true;
  dialogRef: MatDialogRef<ProjectDialogComponent>;
  lonReference: number;
  latReference: number;

  private divToScroll;

  destroy$ = new Subject<void>();
  private searchText$ = new Subject<string>();

  constructor(private employeeService: EmployeeService,
              private matIconRegistry: MatIconRegistry,
              private projectsService: ProjectService,
              private projectLocation: ProjectLocation,
              private domSanitizer: DomSanitizer,
              private alertService: AlertService,
              private route: ActivatedRoute,
              private location: Location,
              public dialog: MatDialog
  ) {}

  openDialog(p: Project): void {
    this.dialogRef = this.dialog.open(ProjectDialogComponent, {
      autoFocus: false,
      panelClass: 'custom-dialog-container',
      data: {
        project: p,
        applicable: this.isProjectApplicable(p.id),
        bookmarked: this.isProjectBookmarked(p.id),
        isUserBoss: this.isUserBoss,
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

  @HostListener('window:resize')
  onResize(): void {
    this.mobile = document.body.clientWidth < 992;
  }

  swipebugplaceholder(): void {}

  loadProjects(): void {
    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        if (data[0].projects) {
          this.projects = data[0].projects;
          this.filteredProjects = this.projects;
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

  protected filterCities() {
    if (!this.cities) {
      return;
    }
    // get the search keyword
    let search = this.cityFilterCtrl.value;
    if (!search || search.length < 2) {
      let indexOfPick = this.cities.indexOf(this.cityControl.value);
      this.filteredCities.next(this.cities.slice(indexOfPick, indexOfPick +1));
      return;
    } else {
      search = search.toLowerCase();
    // filter the cities
      this.filteredCities.next(
        //this.cities.filter(city => city.toLowerCase().indexOf(search) > -1)
        this.cities.filter(city => city.toLowerCase().startsWith(search))
      );
    }
  }

  ngAfterViewInit() {
    this.setInitialValue();
  }

  ngOnDestroy() {
    this._onDestroy.next();
    this._onDestroy.complete();
  }

  /**
   * Sets the initial value after the filteredCities are loaded initially
   */
  protected setInitialValue() {
    this.filteredCities
      .pipe(take(1), takeUntil(this._onDestroy))
      .subscribe(() => {
        // setting the compareWith property to a comparison function
        // triggers initializing the selection according to the initial value of
        // the form control (i.e. _initializeSelection())
        // this needs to be done after the filteredCities are loaded initially
        // and after the mat-option elements are available
        this.singleSelect.compareWith = (a: string, b: string) => a && b && a === b;
      });
  }

  ngOnInit(): void {

    let indexDefault = this.cities.indexOf("DORTMUND");

    // set initial selection
    this.cityControl.setValue(this.cities[indexDefault]); //Standort Attribut des Users

    // load the initial city list
    this.filteredCities.next(this.cities.slice(indexDefault, indexDefault+1));

    // listen for search field value changes
    this.cityFilterCtrl.valueChanges
      .pipe(debounceTime(500))
      .pipe(takeUntil(this._onDestroy))
      .subscribe(() => {
        this.filterCities();
        this.selectedRange = '0';
      });

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
        this.filteredProjects = this.projects;
        this.projectsFound = projects.length;
      });
    this.loadProjects();

    this.divToScroll = document.getElementById('divToScroll');
  }

  searchProjects(): void {
    this.loadingProjects = true;
    this.projects = [];
    this.searchText$.next(this.searchText);
  }

  sortByLocation(memory: number): void {
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

  private setSelectedProject(projectId: string): void {
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


//TO-DO
  filterByDistance(range: number) {
    console.log(this.cityControl.value + " " + range);
      if(range == 0) {
        this.filteredProjects = this.projects;
      } else {
        console.log("To be implemented");
    /* this.projectLocation.getProjectsInRange(referenceLocation, range).
      subscribe((data) => {
        if (data) {
          this.filteredProjects = data;
        } else {
          this.filteredProjects = [];
        }
      }); */

    }
  }

  filterDistanceGreater(limit: number) {
    this.filteredProjects = this.projects;
  }

  isProjectApplicable(projectId: string): boolean {
    return this.applications ? !this.applications.some(a => a && a.project.id === projectId) : true;
  }

  isProjectBookmarked(projectId: string): boolean {
    return this.projectsService.isBookmarked(this.bookmarks, projectId);
  }

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

  /* Common Functions with Profile - end */

  onDialogClosed(): void {
    this.selectedProject = null;
    this.location.replaceState('/browse');
  }

  @HostListener('window:scroll')
  onScroll(): void {
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
