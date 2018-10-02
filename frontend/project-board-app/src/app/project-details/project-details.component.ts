import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { faBookmark } from '@fortawesome/free-regular-svg-icons/faBookmark';
import { faGraduationCap } from '@fortawesome/free-solid-svg-icons/faGraduationCap';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  @Input() selectedProject: Project;
  @Input() applicable;
  @Input() bookmark = false;
  @Output() bookmarkChanged = new EventEmitter();
  faBookmark = faBookmark;
  faGradCap = faGraduationCap;
  bmTooltip = 'Du hast ein Lesezeichen an diesem Projekt.';
  studTooltip = 'Studentisches Projekt';

  constructor(private router: Router,
              private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  ngOnInit() {
  }

  requestProject() {
    this.router.navigate([`projects/${this.selectedProject.id}/request`]);
  }

  addBookmark() {
    this.employeeService.addBookmark(this.authService.username, this.selectedProject.id)
      .subscribe(() => this.bookmarkChanged.emit(this.selectedProject));
  }

  removeBookmark() {
    this.employeeService.removeBookmark(this.authService.username, this.selectedProject.id)
      .subscribe(() => this.bookmarkChanged.emit(this.selectedProject));
  }
}
