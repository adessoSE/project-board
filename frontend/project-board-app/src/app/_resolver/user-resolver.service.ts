import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../_services/authentication.service';
import { Employee, EmployeeService } from '../_services/employee.service';

@Injectable({
  providedIn: 'root'
})
export class UserResolverService implements Resolve<Employee> {

  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Employee> {
    return this.employeeService.getFullEmployeeForId(this.authenticationService.username);
  }
}
