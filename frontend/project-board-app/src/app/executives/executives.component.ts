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
  sortValue: number;
  sortMemory: number;

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

  swipebugplaceholder() { }

  ngOnInit() {
    this.mobile = document.body.clientWidth < 992;
    this.smallMobile = document.body.clientWidth < 768;
    combineLatest(this.route.data, this.route.params)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.employees = data[0].employees
          .map(e => {
            if (e.accessInfo.hasAccess) {
              e.duration = this.daysUntil(new Date(e.accessInfo.accessEnd));
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
    for (const e of this.employees) {
      this.employeeService.getEmployeeWithId(e.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(employee => e.picture = employee.picture);
    }
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

  private daysUntil(date: Date): number {
    let diff = date.getTime() - Date.now();
    diff = Math.floor(diff / (1000 * 60 * 60 * 24));
    return diff;
  }

  badgeTooltip(employee) {
    const fullName = `${employee.firstName} ${employee.lastName}`;
    if (employee.boss) {
      return `${fullName} hat als Führungskraft dauerhaften Zugang zum Project Board.`;
    }

    const days = employee.duration;
    if (employee.accessInfo.hasAccess) {
      if (days === 0) {
        return `${fullName} hat nur noch heute Zugang zum Project Board.`;
      }
      return `${fullName} hat noch ${days} ${days > 1 ? 'Tage' : 'Tag'} Zugang zum Project Board.`;
    }
    return `${employee.firstName} ${employee.lastName} hat keinen Zugang zum Project Board.`;
  }

  searchEmployees(): void {
    // TODO: outsource to the backend
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

  sortByState(memory: number) {
    if (this.sortMemory !== memory) {
      this.sortValue = 0;
      this.sortMemory = memory;
    }

    if (this.sortValue === 0 || this.sortValue === undefined) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        return (a.accessInfo.hasAccess === b.accessInfo.hasAccess) ? 0 : a.accessInfo.hasAccess ? -1 : 1
      });
      this.sortValue = 1;
    }
    else if (this.sortValue === 1) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        return (a.accessInfo.hasAccess === b.accessInfo.hasAccess) ? 0 : b.accessInfo.hasAccess ? -1 : 1
      });
      this.sortValue = 2;
    }
    else {
      this.filteredEmployees.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
      this.sortValue = 0;
    }
  }

  sortBySince(memory: number) {
    if (this.sortMemory !== memory) {
      this.sortValue = 0;
      this.sortMemory = memory;
    }

    if (this.sortValue === 0 || this.sortValue === undefined) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        return Number(new Date(b.accessInfo.accessStart)) - Number(new Date(a.accessInfo.accessStart))
      });
      this.sortValue = 1;
    }
    else if (this.sortValue === 1) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        if(a.accessInfo.accessEnd === null ){
          return 1;
        }
        return Number(new Date(a.accessInfo.accessStart)) - Number(new Date(b.accessInfo.accessStart))
      });
      this.sortValue = 2;
    }
    else {
      this.filteredEmployees.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
      this.sortValue = 0;
    }
  }

  sortByUntil(memory: number) {
    if (this.sortMemory !== memory) {
      this.sortValue = 0;
      this.sortMemory = memory;
    }
    if (this.sortValue === 0 || this.sortValue === undefined) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        if(a.boss){
          console.log(a.lastName);
        }
        return Number(new Date(b.accessInfo.accessEnd)) - Number(new Date(a.accessInfo.accessEnd))
      });
      this.sortValue = 1;
    }
    else if (this.sortValue === 1) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        if(a.accessInfo.accessEnd === null ){
          return 1;
        }
        return Number(new Date(a.accessInfo.accessEnd)) - Number(new Date(b.accessInfo.accessEnd))
      });
      this.sortValue = 2;
    }
    else {
      this.filteredEmployees.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
      this.sortValue = 0;
    }
  }

  sortByRequest(memory: number) {
    if (this.sortMemory !== memory) {
      this.sortValue = 0;
      this.sortMemory = memory;
    }

    if (this.sortValue === 0 || this.sortValue === undefined) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        return b.applications.count - a.applications.count
      });
      this.sortValue = 1;
    }
    else if (this.sortValue === 1) {
      this.filteredEmployees.sort((a: Employee, b: Employee) => {
        return a.applications.count - b.applications.count
      });
      this.sortValue = 2;
    }
    else {
      this.filteredEmployees.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
      this.sortValue = 0;
    }
  }

  toggleBoss() {
    // TODO
    console.log('toggle clicked');
  }
}
