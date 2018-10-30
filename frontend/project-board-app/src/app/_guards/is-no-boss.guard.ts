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
export class IsNoBossGuard implements CanActivate {
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
          if (isBoss) {
            this.alertService.info('Führungskräfte dürfen keine Projekte über dieses Tool anfragen.', true);
            const projectId = state.url.match('(STF|STD)-[\\d]*')[0];
            if (projectId) {
              this.router.navigate([`/browse/${projectId}`]);
            } else {
              this.router.navigate([`/browse`]);
            }
          }
          return !isBoss;
        })
      );
  }
}
