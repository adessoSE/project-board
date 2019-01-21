import { Location } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
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

  showEmployees: string[] = [];
  employeeMap: Map<string, Employee[]> = new Map<string, Employee[]>();

  searchText = '';
  loading = true;
  dialogRef: MatDialogRef<EmployeeDialogComponent>;
  sortValue = 0; // 0: alphabetically ascending, 1: column ascending, 2: column descending
  sortMemory: number; // memorizes the last column that was sorted

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
      // TODO: change the picture load process to the new projection way, when the backend is ready
      this.employeeService.getEmployeeWithId(e.id)
        .pipe(takeUntil(this.destroy$), map(employee => employee.picture))
        .subscribe(pic => e.picture = pic);
      // if the employee is a boss, preload the embedded employees
      if (e.boss) {
        this.loadEmbeddedEmployees(e);
      }
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

  sort(memory: number) {
    if (this.sortMemory !== memory) {
      this.sortValue = 0;
      this.sortMemory = memory;
    }

    switch (memory) {
      case 1:
        this.filteredEmployees = this.sortByState(this.filteredEmployees);
        this.employeeMap.forEach((employees: Employee[], key: string) => {
          this.employeeMap.set(key, this.sortByState(employees));
        });
        break;
      case 2:
        this.filteredEmployees = this.sortBySince(this.filteredEmployees);
        this.employeeMap.forEach((employees: Employee[], key: string) => {
          this.employeeMap.set(key, this.sortBySince(employees));
        });
        break;
      case 3:
        this.filteredEmployees = this.sortByUntil(this.filteredEmployees);
        this.employeeMap.forEach((employees: Employee[], key: string) => {
          this.employeeMap.set(key, this.sortByUntil(employees));
        });
        break;
      case 4:
        this.filteredEmployees = this.sortByRequest(this.filteredEmployees);
        this.employeeMap.forEach((employees: Employee[], key: string) => {
          this.employeeMap.set(key, this.sortByRequest(employees));
        });
        break;
      default:
    }
    this.sortValue = ++this.sortValue % 3;
  }

  sortByState(toSort: Employee[]): Employee[] {
    if (this.sortValue === 0) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          if (a.boss && !b.accessInfo.hasAccess || b.boss && a.accessInfo.hasAccess) {
            return -1;
          } else if (a.boss && b.accessInfo.hasAccess || b.boss && !a.accessInfo.hasAccess) {
            return 1;
          }
        }
        return a.accessInfo.hasAccess === b.accessInfo.hasAccess ? 0 : a.accessInfo.hasAccess ? -1 : 1;
      });
    } else if (this.sortValue === 1) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          if (a.boss && !b.accessInfo.hasAccess || b.boss && a.accessInfo.hasAccess) {
            return 1;
          } else if (a.boss && b.accessInfo.hasAccess || b.boss && !a.accessInfo.hasAccess) {
            return -1;
          }
        }
        return a.accessInfo.hasAccess === b.accessInfo.hasAccess ? 0 : a.accessInfo.hasAccess ? 1 : -1;
      });
    } else {
      toSort.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
    }

    return toSort;
  }

  sortBySince(toSort: Employee[]): Employee[] {
    if (this.sortValue === 0) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          if (a.boss && !b.accessInfo.hasAccess || b.boss && a.accessInfo.hasAccess) {
            return -1;
          } else if (a.boss && b.accessInfo.hasAccess || b.boss && !a.accessInfo.hasAccess) {
            return 1;
          }
        }
        if (!a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return 0;
        } else if (a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return -1;
        } else if (!a.accessInfo.hasAccess && b.accessInfo.hasAccess) {
          return 1;
        }
        return new Date(b.accessInfo.accessStart).getTime() - new Date(a.accessInfo.accessStart).getTime();
      });
    } else if (this.sortValue === 1) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          if (a.boss && !b.accessInfo.hasAccess || b.boss && a.accessInfo.hasAccess) {
            return -1;
          } else if (a.boss && b.accessInfo.hasAccess || b.boss && !a.accessInfo.hasAccess) {
            return 1;
          }
        }
        if (!a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return 0;
        } else if (a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return -1;
        } else if (!a.accessInfo.hasAccess && b.accessInfo.hasAccess) {
          return 1;
        }
        return new Date(a.accessInfo.accessStart).getTime() - new Date(b.accessInfo.accessStart).getTime();
      });
    } else {
      toSort.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
    }

    return toSort;
  }

  sortByUntil(toSort: Employee[]): Employee[] {
    if (this.sortValue === 0) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          if (a.boss && !b.accessInfo.hasAccess || b.boss && a.accessInfo.hasAccess) {
            return -1;
          } else if (a.boss && b.accessInfo.hasAccess || b.boss && !a.accessInfo.hasAccess) {
            return 1;
          }
        }
        if (!a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return 0;
        } else if (a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return -1;
        } else if (!a.accessInfo.hasAccess && b.accessInfo.hasAccess) {
          return 1;
        }
        return new Date(a.accessInfo.accessEnd).getTime() - new Date(b.accessInfo.accessEnd).getTime();
      });
    } else if (this.sortValue === 1) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          if (a.boss && !b.accessInfo.hasAccess || b.boss && a.accessInfo.hasAccess) {
            return -1;
          } else if (a.boss && b.accessInfo.hasAccess || b.boss && !a.accessInfo.hasAccess) {
            return 1;
          }
        }
        if (!a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return 0;
        } else if (a.accessInfo.hasAccess && !b.accessInfo.hasAccess) {
          return -1;
        } else if (!a.accessInfo.hasAccess && b.accessInfo.hasAccess) {
          return 1;
        }
        return new Date(b.accessInfo.accessEnd).getTime() - new Date(a.accessInfo.accessEnd).getTime();
      });
    } else {
      toSort.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
    }

    return toSort;
  }

  sortByRequest(toSort: Employee[]): Employee[] {
    if (this.sortValue === 0) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          return a.boss ? 1 : -1;
        }
        return a.applications.count - b.applications.count;
      });
    } else if (this.sortValue === 1) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          return a.boss ? 1 : -1;
        }
        return b.applications.count - a.applications.count;
      });
    } else {
      toSort.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
    }

    return toSort;
  }

  toggleBoss(event, employee: Employee) {
    if (this.showEmployees.includes(employee.id)) {
      this.showEmployees = this.showEmployees.filter(id => id !== employee.id);
    } else {
      this.showEmployees.push(employee.id);
    }
    // load embedded employees of deeper layers
    this.loadEmbeddedEmployees(employee);
    // prevent activation of the table row click event
    event.stopPropagation();
  }

  loadEmbeddedEmployees(boss: Employee) {
    // TODO: change the picture load process to the new projection way, when the backend is ready
    if (!this.employeeMap.has(boss.id)) {
      // load all employees
      this.employeeService.getEmployeesForSuperUser(boss.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(employees => {
          // initialise the map entry
          this.employeeMap.set(boss.id, []);
          // reload employees by id to get the images
          for (const e of employees) {
            this.employeeService.getEmployeeWithId(e.id)
              .pipe(takeUntil(this.destroy$))
              .subscribe(emp => {
                // set the duration attribute to display on the badge
                emp.duration = this.daysUntil(new Date(emp.accessInfo.accessEnd));
                // push each employee into the list and reset the map entry
                const list = this.employeeMap.get(boss.id);
                list.push(emp);
                list.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
                this.employeeMap.set(boss.id, list);
              });
          }
        });
    }
  }
}
