import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { faBookmark } from '@fortawesome/free-regular-svg-icons/faBookmark';
import { faGraduationCap } from '@fortawesome/free-solid-svg-icons/faGraduationCap';
import { Subject } from 'rxjs';
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
export class ProjectDetailsComponent {
  @Input() selectedProject: Project;
  @Input() applicable;
  @Input() bookmark = false;
  @Input() isUserBoss = false;
  @Output() bookmarkChanged = new EventEmitter();
  faBookmark = faBookmark;
  faGradCap = faGraduationCap;
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  studTooltip = 'Studentisches Projekt';

  destroy$ = new Subject<void>();

  constructor(private router: Router,
              private alertService: AlertService,
              private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  swipebugplaceholder() {}

  requestProject() {
    this.router.navigate([`/projects/${this.selectedProject.id}/request`]);
  }

  addBookmark() {
    this.employeeService.addBookmark(this.authService.username, this.selectedProject.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.bookmarkChanged.emit(this.selectedProject),
        () => this.alertService.error('Es gab einen Fehler. Das Lesezeichen konnte nicht hinzugefügt werden.')
      );
  }

  removeBookmark() {
    this.employeeService.removeBookmark(this.authService.username, this.selectedProject.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.bookmarkChanged.emit(this.selectedProject),
        () => this.alertService.error('Es gab einen Fehler. Das Lesezeichen konnte nicht entfernt werden.')
      );
  }
}
