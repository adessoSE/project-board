import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { Application, EmployeeService } from './employee.service';

@Injectable({
  providedIn: 'root'
})
export class ApplicationsResolverService implements Resolve<Application[]> {

  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Application[]> {
    return this.employeeService.getApplications(this.authenticationService.username);
  }
}
