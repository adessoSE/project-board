import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Employee, EmployeeService } from '../_services/employee.service';

@Component({
  selector: 'app-admin-ui',
  templateUrl: './admin-ui.component.html',
  styleUrls: ['./admin-ui.component.css']
})
export class AdminUiComponent implements OnInit {
  employees: Employee[] = [];
  options = [];

  selectedEmployee: Employee;

  constructor(private employeeService: EmployeeService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.route.data.subscribe((data: { employees: Employee[] }) => {
      this.employees = data.employees;
      this.route.params.subscribe(params => {
        if (params.id) {
          this.setSelectedEmployee(params.id);
        }
      });
    });
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

  private setSelectedEmployee(employeeId) {
    for (let e of this.employees) {
      if (e.id == employeeId) {
        this.selectedEmployee = e;
        return;
      }
    }
    this.selectedEmployee = null;
  }

  employeeClicked(e) {
    this.selectedEmployee = e;
    this.router.navigate([`/admin/${e.id}`]);
  }
}
