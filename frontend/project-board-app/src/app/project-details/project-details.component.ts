import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  selectedProject: Project;
  applicable: boolean;
  bookmarked: boolean;
  isUserBoss = false;
  @Output() bookmarkChanged = new EventEmitter();
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  studTooltip = 'Studentisches Projekt';

  destroy$ = new Subject<void>();

  constructor(private router: Router,
              private route: ActivatedRoute,
              private alertService: AlertService,
              private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  ngOnInit(): void {
    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        if (data[0].project) {
          this.selectedProject = data[0].project;
        }

        // extract projects from applications
        console.log(data);
        this.applicable = !(data[0].applications.map(app => app.project.id) as string[]).includes(this.selectedProject.id);
        console.log(this.applicable);
        this.bookmarked = (data[0].bookmarks.map(proj => proj.id) as string[]).includes(this.selectedProject.id);
        this.isUserBoss = data[0].isUserBoss;
      });
  }

  swipebugplaceholder(): void {}

  requestProject(): void {
    this.router.navigate([`/projects/${this.selectedProject.id}/request`]);
  }

  addBookmark(): void {
    this.employeeService.addBookmark(this.authService.username, this.selectedProject.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.bookmarked = true,
        () => this.alertService.error('Es gab einen Fehler. Das Lesezeichen konnte nicht hinzugefügt werden.')
      );
  }

  removeBookmark(): void {
    this.employeeService.removeBookmark(this.authService.username, this.selectedProject.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.bookmarked = false,
        () => this.alertService.error('Es gab einen Fehler. Das Lesezeichen konnte nicht entfernt werden.')
      );
  }
}
