import { Component, OnInit } from '@angular/core';
import { EmployeeService } from '../employee.service';

@Component({
  selector: 'app-admin-ui',
  templateUrl: './admin-ui.component.html',
  styleUrls: ['./admin-ui.component.css']
})
export class AdminUiComponent implements OnInit {
  // duration = 0;
  employees = [];
  options = [];

  selectedEmployee: any;

  constructor(private employeeService: EmployeeService) { }

  ngOnInit() {
    this.loadEmployees();
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

  private loadEmployees() {
    this.employeeService.getEmployees().subscribe(e => {this.employees = e;});
  }
}
