import { formatDate } from '@angular/common';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
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

  numberOfDaysSelect = [];

  constructor(private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

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
    // console.log('now: ', accessEnd);
    accessEnd.setDate(accessEnd.getDate() + Number(duration.value));
    // console.log(`now plus ${duration.value} day: ${accessEnd}`);
    accessEnd.setHours(23, 59, 59, 999);
    // console.log('set hours to 24: ', accessEnd);
    const dateString = formatDate(accessEnd, 'yyyy-MM-ddTHH:mm:ss.SSS', 'de');
    this.employeeService.setEmployeeAccessInfo(this.selectedEmployee.id, dateString).subscribe(user => {
      this.selectedEmployee.accessInfo = user.accessInfo;
      this.selectedEmployee.duration = duration.value;
      this.selectedEmployee.enabled = true;
    });
  }

  deactivate() {
    this.employeeService.deleteEmployeeAccessInfo(this.selectedEmployee.id).subscribe(user => {
      this.selectedEmployee.accessInfo = user.accessInfo;
      this.selectedEmployee.duration = 0;
      this.selectedEmployee.enabled = false;
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
}
