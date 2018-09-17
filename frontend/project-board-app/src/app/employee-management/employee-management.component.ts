import { formatDate } from '@angular/common';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, Employee, EmployeeAccessInfo, EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-employee-management',
  templateUrl: './employee-management.component.html',
  styleUrls: ['./employee-management.component.scss']
})
export class EmployeeManagementComponent implements OnInit, OnChanges {
  @Input() selectedEmployee: Employee;
  @Input() adminControls = true;
  numberOfDaysSelect = [];

  favorites: Project[];
  applications: Application[];
  accessInfo: EmployeeAccessInfo;

  constructor(private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.selectedEmployee.currentValue && !changes.selectedEmployee.isFirstChange()) {
      if (!this.adminControls) {
        this.getFavorites();
      }
      this.getApplications();
      this.getAccessInfo();
    }
  }

  ngOnInit() {
    for (let i = 1; i < 29; i++) {
      this.numberOfDaysSelect.push(i);
    }
    if (!this.adminControls) {
      this.getFavorites();
    }
    if (this.selectedEmployee) {
      this.getApplications();
      this.getAccessInfo();
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
    this.employeeService.setEmployeeAccessInfo(this.selectedEmployee.id, dateString).subscribe(accInf => {
      this.accessInfo = accInf;
      this.selectedEmployee.duration = duration.value;
    });
  }

  deactivate() {
    const accessEnd = new Date();
    const dateString = formatDate(accessEnd, 'yyyy-MM-ddT00:00:00', 'de');
    this.employeeService.setEmployeeAccessInfo(this.selectedEmployee.id, dateString).subscribe(accInf => {
      this.accessInfo = accInf;
    });
  }

  getFavorites() {
    this.employeeService.getFavorites(this.selectedEmployee.id).subscribe(fav => this.favorites = fav);
  }

  removeFromFavorites(projectId) {
    this.employeeService.removeFromFavorites(this.authService.username, projectId).subscribe(() => this.getFavorites());
  }

  getApplications() {
    this.employeeService.getApplications(this.selectedEmployee.id).subscribe(appls => this.applications = appls);
  }

  revokeApplication(appId) {
    this.employeeService.revokeApplication(this.authService.username, appId).subscribe(() => this.getApplications());
  }

  getAccessInfo() {
    this.employeeService.getEmployeeAccessInfo(this.selectedEmployee.id).subscribe(accInf => this.accessInfo = accInf);
  }
}
