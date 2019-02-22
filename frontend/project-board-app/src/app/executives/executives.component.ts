import { Location } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MatIconRegistry } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
import { combineLatest, Subject } from 'rxjs';
import { debounceTime, switchMap, takeUntil } from 'rxjs/operators';
import { AuthenticationService } from '../_services/authentication.service';
import { Employee, EmployeeService } from '../_services/employee.service';
import { EmployeeDialogComponent } from '../employee-dialog/employee-dialog.component';
import { SEARCH_INFO_TOOLTIP } from '../tooltips';

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
  toggle = true;
  showEmployees: string[] = [];
  employeeMap: Map<string, Employee[]> = new Map<string, Employee[]>();

  infoTooltip = SEARCH_INFO_TOOLTIP;
  searchText = '';
  loading = true;
  dialogRef: MatDialogRef<EmployeeDialogComponent>;
  sortValue = 0; // 0: alphabetically ascending, 1: column ascending, 2: alphabetically/column descending
  sortMemory = 0; // memorizes the last column that was sorted

  private searchText$ = new Subject<string>();
  destroy$ = new Subject<void>();

  constructor(private authService: AuthenticationService,
              private employeeService: EmployeeService,
              private matIconRegistry: MatIconRegistry,
              private domSanitizer: DomSanitizer,
              private route: ActivatedRoute,
              private location: Location,
              public dialog: MatDialog) { }

  @HostListener('window:resize')
  onResize(): void {
    this.mobile = document.body.clientWidth < 992;
  }

  ngOnInit(): void {
    this.matIconRegistry.addSvgIcon(
      'sort_alpha_ascending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/sort-alpha-down-solid.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'sort_alpha_descending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/sort-alpha-up-solid.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'sort_ascending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/long-arrow-alt-up-solid.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'sort_descending',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/long-arrow-alt-down-solid.svg')
    );
    this.mobile = document.body.clientWidth < 992;
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
    this.searchText$
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(500),
        switchMap(searchText => {
          return this.employeeService.search(searchText, this.authService.username);
        }))
      .subscribe(employees => {
        this.loading = false;
        this.employees = employees;
        this.filteredEmployees = this.employees;
      });
    // lazy load pictures for employees
    this.employeeService.getEmployeePicturesForSuperUser(this.authService.username)
      .pipe(takeUntil(this.destroy$))
      .subscribe(pictures => {
        for (const e of this.employees) {
          e.picture = pictures.find(emp => emp.id === e.id).picture;
        }
      });
  }

  searchEmployees(): void {
    this.loading = true;
    this.employees = [];
    this.searchText$.next(this.searchText);
  }

  private setSelectedEmployee(employeeId): void {
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

  badgeTooltip(employee): string {
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

  openDialog(e: Employee): void {
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

  sort(memory: number, sortValue?: number): void {
    if (this.sortMemory !== memory) {
      this.sortValue = sortValue || 0;
      this.sortMemory = memory;
    }

    switch (memory) {
      case 0:
        this.filteredEmployees = this.sortAlphabetically(this.filteredEmployees);
        this.employeeMap.forEach((employees: Employee[], key: string) => {
          this.employeeMap.set(key, this.sortAlphabetically(employees));
        });
        this.sortValue = this.sortValue === 0 ? ++this.sortValue : this.sortValue;
        break;
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
    if (this.sortMemory !== 0 && this.sortValue === 2) {
      this.sort(0, 2);
    } else {
      this.sortValue = ++this.sortValue % 3;
    }
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
        return a.applications - b.applications;
      });
    } else if (this.sortValue === 1) {
      toSort.sort((a: Employee, b: Employee) => {
        if (a.boss && b.boss) {
          return 0;
        } else if (a.boss !== b.boss) {
          return a.boss ? 1 : -1;
        }
        return b.applications - a.applications;
      });
    }

    return toSort;
  }

  sortAlphabetically(toSort: Employee[]): Employee[] {
    if (this.sortValue === 0) {
      toSort.sort((a: Employee, b: Employee) => a.lastName >= b.lastName ? -1 : 1);
    } else {
      toSort.sort((a: Employee, b: Employee) => a.lastName >= b.lastName ? 1 : -1);
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
    if (!this.employeeMap.has(boss.id)) {
      // load all employees
      this.employeeService.getEmployeesWithoutPicturesForSuperUser(boss.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(employees => {
          employees = employees.map(emp => {
            emp.duration = this.daysUntil(new Date(emp.accessInfo.accessEnd));
            return emp;
          });
          employees.sort((a, b) => a.lastName >= b.lastName ? 1 : -1);
          // initialise the map entry
          this.employeeMap.set(boss.id, employees);

          // reload employees by id to get the images
          this.employeeService.getEmployeePicturesForSuperUser(boss.id)
            .pipe(takeUntil(this.destroy$))
            .subscribe(pictures => {
              for (const e of employees) {
                e.picture = pictures.find(emp => emp.id === e.id).picture;
              }
              this.employeeMap.set(boss.id, employees);
            });
        });
    }
  }


  @HostListener('window:scroll')
  onScroll() {
    if (!this.mobile) {
      if (((document.getElementById('total-hits').offsetTop - window.scrollY + 60) === 0) && this.toggle) {
        $('#result-table > thead th').css('-webkit-box-shadow', 'inset 0 -1px 1px -1px rgba(128,128,128, 0.6)');
        $('#result-table > thead th').css('-moz-box-shadow', 'inset 0 -1px 1px -1px rgba(128,128,128, 0.6)');
        $('#result-table > thead th').css('box-shadow', 'inset 0 -1px 1px -1px rgba(128,128,128, 0.6)');
        this.toggle = false;
      } else if (!this.toggle && ((document.getElementById('total-hits').offsetTop - window.scrollY + 60) !== 0)) {
        $('#result-table > thead th').css('-webkit-box-shadow', 'none');
        $('#result-table > thead th').css('-moz-box-shadow', 'none');
        $('#result-table > thead th').css('box-shadow', 'none');
        this.toggle = true;
      }
    }
  }
}
