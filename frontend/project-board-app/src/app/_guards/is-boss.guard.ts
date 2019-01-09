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
export class IsBossGuard implements CanActivate {
  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService,
              private alertService: AlertService,
              private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.employeeService.isUserBoss(this.authenticationService.username)
      .pipe(
        map(isBoss => {
          // only allowed to boss users
          if (!isBoss) {
            this.alertService.info('Du hast keinen Zugriff auf diesen Bereich.', true);
            this.router.navigate([`/browse`]);
          }
          return isBoss;
        })
      );
  }
}
