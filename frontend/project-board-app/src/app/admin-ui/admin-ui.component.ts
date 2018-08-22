import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as $ from 'jquery';
import { Employee, EmployeeService } from '../_services/employee.service';

@Component({
  selector: 'app-admin-ui',
  templateUrl: './admin-ui.component.html',
  styleUrls: ['./admin-ui.component.scss']
})
export class AdminUiComponent implements OnInit, AfterViewChecked {
  employees: Employee[] = [];
  selectedEmployee: Employee;
  mobile = false;
  scroll = true;

  @HostListener('window:resize') onResize() {
    this.mobile = window.screen.width < 768;
  }

  constructor(private employeeService: EmployeeService, private route: ActivatedRoute, private router: Router, private location: Location) { }

  ngOnInit() {
    this.mobile = window.screen.width <= 425;
    this.route.data.subscribe((data: { employees: Employee[] }) => {
      this.employees = data.employees;
      this.route.params.subscribe(params => {
        if (params.id) {
          this.setSelectedEmployee(params.id);
        }
      });
    });
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

  employeeClicked(employee) {
    if (this.selectedEmployee == employee) {
      this.location.replaceState(`/admin`);
      this.selectedEmployee = null;
      this.scroll = false;
    } else {
      this.location.replaceState(`/admin/${employee.id}`);
      this.selectedEmployee = employee;
      this.scroll = true;
    }
  }

  ngAfterViewChecked() {
    if (this.mobile && this.scroll && this.selectedEmployee) {
      let btn = $(`#${this.selectedEmployee.id}`);
      // navbar has 56 pixels height
      $('html, body').animate({scrollTop: $(btn).offset().top - 56}, 'slow');
      this.scroll = false;
    }
  }
}
