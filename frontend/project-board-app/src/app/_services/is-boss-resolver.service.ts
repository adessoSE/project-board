import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { EmployeeService } from './employee.service';

@Injectable({
  providedIn: 'root'
})
export class IsBossResolverService implements Resolve<boolean> {

  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.employeeService.isUserBoss(this.authenticationService.username);
  }
}
