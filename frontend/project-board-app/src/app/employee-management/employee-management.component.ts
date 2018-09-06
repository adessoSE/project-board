import { Component, Input, OnInit } from '@angular/core';
import { Employee } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-employee-management',
  templateUrl: './employee-management.component.html',
  styleUrls: ['./employee-management.component.scss']
})
export class EmployeeManagementComponent implements OnInit {
  @Input() selectedEmployee: Employee;
  @Input() adminControls = true;
  numberOfDaysSelect = [];

  favorites: Project[];
  applications: Project[];

  constructor(private projectService: ProjectService) { }

  ngOnInit() {
    for (let i = 1; i < 29; i++) {
      this.numberOfDaysSelect.push(i);
    }
    this.getFavorites();
    this.getApplications();
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
    this.projectService.getFavorites(this.selectedEmployee.id).subscribe(fav => this.favorites = fav);
  }

  getApplications() {
    this.projectService.getApplicationsForUser(this.selectedEmployee.id).subscribe(appls => this.applications = appls);
  }
}
