import { Location } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Employee, EmployeeService } from '../_services/employee.service';
import { EmployeeDialogComponent } from '../employee-dialog/employee-dialog.component';

@Component({
  selector: 'app-executives',
  templateUrl: './executives.component.html',
  styleUrls: ['./executives.component.scss']
})
export class ExecutivesComponent implements OnInit {
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  selectedEmployee: Employee;
  mobile = false;
  smallMobile = false;

  searchText = '';
  loading = true;
  dialogRef: MatDialogRef<EmployeeDialogComponent>;

  destroy$ = new Subject<void>();

  constructor(private employeeService: EmployeeService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location,
              public dialog: MatDialog) { }

  @HostListener('window:resize') onResize() {
    this.mobile = document.body.clientWidth < 992;
    this.smallMobile = document.body.clientWidth < 768;
  }

  swipebugplaceholder() {}

  ngOnInit() {
    this.mobile = document.body.clientWidth < 992;
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
        this.filteredEmployees = this.employees;
        this.loading = false;
        this.setSelectedEmployee(data[1].id);

        if (this.selectedEmployee) {
          this.openDialog(this.selectedEmployee);
        }
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

  searchEmployees(): void {
    this.filteredEmployees = this.employees.filter(e => {
      return (e.firstName + ' ' + e.lastName)
        .toLowerCase()
        .includes(this.searchText.toLowerCase());
    });
    if (this.searchText === '') {
      this.filteredEmployees = this.employees;
    }
  }

  openDialog(e: Employee) {
    this.dialogRef = this.dialog.open(
      EmployeeDialogComponent,
      {
        autoFocus: false,
        panelClass: 'custom-dialog-container',
        data: {
          employee: e
        }
      });

    this.location.replaceState(`/employees/${e.id}`);
    this.dialogRef.afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.location.replaceState('/employees'));
  }

  toggleBoss() {
    console.log('toggle clicked');
  }
}
