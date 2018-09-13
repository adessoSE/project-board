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
  numberOfDaysSelect = [];

  favorites: Project[];
  applications: Application[];

  constructor(private projectService: ProjectService,
              private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.selectedEmployee.currentValue && !changes.selectedEmployee.isFirstChange()) {
      if (!this.adminControls) {
        this.getFavorites();
      }
      this.getApplications();
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
    }
  }

  activate(duration) {
    this.selectedEmployee.enabled = true;
    this.selectedEmployee.duration = duration.value;
  }

  deactivate() {
    this.selectedEmployee.enabled = false;
    this.selectedEmployee.duration = null;
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
}
