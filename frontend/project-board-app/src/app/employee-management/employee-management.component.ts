import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Employee } from '../_services/employee.service';
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
  applications: Project[];

  constructor(private projectService: ProjectService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.selectedEmployee.currentValue) {
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
    this.projectService.getFavorites(this.selectedEmployee.id).subscribe(fav => this.favorites = fav);
  }

  getApplications() {
    this.projectService.getApplicationsForUser(this.selectedEmployee.id).subscribe(appls => this.applications = appls);
  }
}
