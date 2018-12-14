import { Location } from '@angular/common';
import { AfterViewChecked, Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as $ from 'jquery';
import { combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
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
  smallMobile = false;
  scroll = true;
  below = false;

  destroy$ = new Subject<void>();

  constructor(private employeeService: EmployeeService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location) { }

  @HostListener('window:resize') onResize() {
    this.mobile = document.body.clientWidth < 1200;
    this.smallMobile = document.body.clientWidth < 768;
  }

  swipebugplaceholder(){}

  ngOnInit() {
    this.mobile = document.body.clientWidth < 1200;
    this.smallMobile = document.body.clientWidth < 768;
    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.employees = data[0].employees
          .map(e => {
            if (e.accessInfo.hasAccess) {
              e.duration = this.daysUntil(e.accessInfo.accessEnd);
            } else {
              e.duration = 0;
            }
            return e;
          })
          .sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
        this.setSelectedEmployee(data[1].id);
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

/* creates a valid Id in case it contains a dot*/
  getValidId(employee){
    var fixedEmployeeId = employee.id;
    if((employee.id + "").indexOf(".") == 1){
      var idAsArray = (employee.id + "").split(".");
      var fixedId = idAsArray[0] + "\\." + idAsArray[1];
      fixedEmployeeId = fixedId;
    }
    return fixedEmployeeId;
  }

  employeeClicked(employee) {

    const newEmployeeOffset = $(`#${this.getValidId(employee)}`).offset().top;

    if (this.selectedEmployee === employee) {
      this.location.replaceState(`/admin`);
      this.selectedEmployee = null;
      this.scroll = false;
      this.below = false;
    } else {
      this.location.replaceState(`/admin/${employee.id}`);
      
      if(this.selectedEmployee){

      const oldEmployeeOffset = $(`#${this.getValidId(this.selectedEmployee)}`).offset().top;

       if(oldEmployeeOffset > newEmployeeOffset){ // set "below" true, if employee is below opened
        this.below = false;
        } else {
        this.below = true;
      }
    }
      this.selectedEmployee = employee;
      this.scroll = true;
    }
  }

  ngAfterViewChecked() {
    if (this.scroll && this.selectedEmployee && this.smallMobile) {
      var offsetBonus = 56;
      const btn = $(`#${this.getValidId(this.selectedEmployee)}`);
      // navbar has 56 pixels height | if opened below selected calculate offset correctly
      if(this.below){
        $('html, body').animate({scrollTop: $(btn).offset().top - (document.getElementById('managementContainer').scrollHeight + offsetBonus)}, 'slow');
      } else {
        $('html, body').animate({scrollTop: $(btn).offset().top -  offsetBonus}, 'slow');
      }
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
    const fullName = `${employee.firstName} ${employee.lastName}`;
    if (employee.boss) {
      return `${fullName} hat als Führungskraft dauerhaften Zugang zum Project Board.`;
    }

    const days = this.daysUntil(employee.accessInfo.accessEnd);
    if (employee.accessInfo.hasAccess) {
      return `${fullName} hat noch ${days} ${days > 1 ? 'Tage' : 'Tag'} Zugang zum Project Board.`;
    }
    return `${employee.firstName} ${employee.lastName} hat keinen Zugang zum Project Board.`;
  }

}
