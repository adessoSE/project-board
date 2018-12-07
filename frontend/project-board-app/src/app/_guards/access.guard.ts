import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';

@Injectable({
  providedIn: 'root'
})
export class AccessGuard implements CanActivate {
  constructor(private authenticationService: AuthenticationService,
              private employeeService: EmployeeService,
              private alertService: AlertService,
              private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.employeeService.getEmployeeWithId(this.authenticationService.username).pipe(map(user => {
      if (!user.accessInfo.hasAccess && !user.boss && !this.authenticationService.isAdmin) { //admin has acces too
        this.alertService.info('Du bist nicht für das Project Board freigeschaltet.', true);
        this.router.navigate(['/profile']);
      }
      return user.accessInfo.hasAccess || user.boss || this.authenticationService.isAdmin;
    }));
  }
}
