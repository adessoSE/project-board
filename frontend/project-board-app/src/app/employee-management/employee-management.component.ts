import { Component, Input, OnInit } from '@angular/core';
import { Employee } from '../_services/employee.service';

@Component({
  selector: 'app-employee-management',
  templateUrl: './employee-management.component.html',
  styleUrls: ['./employee-management.component.scss']
})
export class EmployeeManagementComponent implements OnInit {
  @Input() selectedEmployee: Employee;
  options = [];

  constructor() { }

  ngOnInit() {
    for (let i = 1; i < 29; i++)
      this.options.push(i);
  }

  activate(duration) {
    this.selectedEmployee.enabled = true;
    this.selectedEmployee.duration = duration.value;
  }

  deactivate() {
    this.selectedEmployee.enabled = false;
    this.selectedEmployee.duration = null;
  }
}
