import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { Employee, EmployeeService } from './employee.service';

@Injectable({
  providedIn: 'root'
})
export class UserResolverService implements Resolve<Employee> {

  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Employee> {
    return this.employeeService.getEmployeeWithId(this.authenticationService.username);
  }
}
