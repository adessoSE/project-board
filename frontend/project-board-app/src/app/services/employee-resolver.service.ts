import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Employee, EmployeeService } from './employee.service';

@Injectable()
export class EmployeeResolverService implements Resolve<Employee[]> {

  constructor(private employeeService: EmployeeService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Employee[]> {
    return this.employeeService.getEmployees();
  }
}
