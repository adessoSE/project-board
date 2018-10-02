import { formatDate } from '@angular/common';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, Employee, EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-employee-management',
  templateUrl: './employee-management.component.html',
  styleUrls: ['./employee-management.component.scss']
})
export class EmployeeManagementComponent implements OnInit, OnChanges {
  @Input() selectedEmployee: Employee;
  @Input() adminControls = true;
  @Input() revokeable = false;
  @Input() bookmarks: Project[] = [];
  @Input() applications: Application[] = [];
  @Input() projects: Project[] = [];

  numberOfDaysSelect = [];

  constructor(private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService,
              private router: Router) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.selectedEmployee.currentValue && !changes.selectedEmployee.isFirstChange()) {
      if (!this.adminControls) {
        this.getBookmarks();
      }
      this.getApplications();
    }
  }

  ngOnInit() {
    for (let i = 1; i < 29; i++) {
      this.numberOfDaysSelect.push(i);
    }
    if (!this.adminControls) {
      this.getBookmarks();
    }
    if (this.selectedEmployee) {
      this.getApplications();
    }
  }

  activate(duration) {
    const accessEnd = new Date();
    accessEnd.setDate(accessEnd.getDate() + Number(duration.value));
    accessEnd.setHours(23, 59, 59, 999);
    const dateString = formatDate(accessEnd, 'yyyy-MM-ddTHH:mm:ss.SSS', 'de');
    this.employeeService.setEmployeeAccessInfo(this.selectedEmployee.id, dateString).subscribe(user => {
      this.selectedEmployee.accessInfo = user.accessInfo;
      this.selectedEmployee.duration = duration.value;
    });
  }

  deactivate() {
    this.employeeService.deleteEmployeeAccessInfo(this.selectedEmployee.id).subscribe(user => {
      this.selectedEmployee.accessInfo = user.accessInfo;
      this.selectedEmployee.duration = 0;
    });
  }

  getBookmarks() {
    this.employeeService.getBookmarks(this.selectedEmployee.id).subscribe(bookmarks => this.bookmarks = bookmarks);
  }

  removeBookmark(projectId) {
    this.employeeService.removeBookmark(this.authService.username, projectId).subscribe(() => this.getBookmarks());
  }

  getApplications() {
    this.employeeService.getApplications(this.selectedEmployee.id).subscribe(appls => this.applications = appls);
  }

  revokeApplication(appId) {
    this.employeeService.revokeApplication(this.authService.username, appId).subscribe(() => this.getApplications());
  }

  deleteProject(projectId) {
    this.projectService.deleteProject(projectId).subscribe(() => this.projects = this.projects.filter(p => p.id !== projectId));
  }

  editProject(projectId) {
    this.router.navigate([`/projects/${projectId}/edit`]);
  }

  isAdmin() {
    return this.authService.isAdmin;
  }
}
