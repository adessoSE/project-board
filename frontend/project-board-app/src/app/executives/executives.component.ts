import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as $ from 'jquery';
import { Employee, EmployeeService } from '../_services/employee.service';

@Component({
  selector: 'app-executives',
  templateUrl: './executives.component.html',
  styleUrls: ['./executives.component.scss']
})
export class ExecutivesComponent implements OnInit, AfterViewChecked {
  employees: Employee[] = [];
  selectedEmployee: Employee;
  mobile = false;
  scroll = true;

  @HostListener('window:resize') onResize() {
    this.mobile = window.screen.width < 768;
  }

  constructor(private employeeService: EmployeeService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location) { }

  ngOnInit() {
    this.mobile = window.screen.width <= 768;
    this.route.data.subscribe((data: { employees: Employee[] }) => {
      this.employees = data.employees.map(e => {
        if (e.accessInfo.hasAccess) {
          e.duration = this.daysUntil(e.accessInfo.accessEnd);
        } else {
          e.duration = 0;
        }
        return e;
      }).sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
      this.route.params.subscribe(params => {
        if (params.id) {
          this.setSelectedEmployee(params.id);
        }
      });
    });
  }

  private setSelectedEmployee(employeeId) {
    for (const e of this.employees) {
      if (e.id === employeeId) {
        this.selectedEmployee = e;
        return;
      }
    }
    this.selectedEmployee = null;
  }

  employeeClicked(employee) {
    if (this.selectedEmployee === employee) {
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
      const btn = $(`#${this.selectedEmployee.id}`);
      // navbar has 56 pixels height
      $('html, body').animate({scrollTop: $(btn).offset().top - 56}, 'slow');
      this.scroll = false;
    }
  }

  private daysUntil(date: Date) {
    date = new Date(date);
    const time1 = new Date().getTime();
    const time2 = date.getTime();
    if (time1 >= time2) {
      return 0;
    }
    let days = time2 - time1;
    days /= 86400000;
    days -= days % 1;
    return days;
  }

  badgeTooltip(employee) {
    const days = this.daysUntil(employee.accessInfo.accessEnd);
    const fullName = `${employee.firstName} ${employee.lastName}`;
    if (employee.accessInfo.hasAccess) {
      return `${fullName} hat noch ${days} ${days > 1 ? 'Tage' : 'Tag'} Zugang zum Project Board.`;
    }
    return `${employee.firstName} ${employee.lastName} hat keinen Zugang zum Project Board.`;
  }
}
