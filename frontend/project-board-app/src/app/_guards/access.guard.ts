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
              private router: Router,
  ) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    const userId = this.authenticationService.username;
    if (this.authenticationService.isBoss) {
      return true;
    }
    return this.employeeService.hasUserAccess(userId)
      .pipe(
        map((hasAccess) => {
          if (!hasAccess.hasAccess) {
            /**
            this.alertService.info('Project Board ist ein schwarzes Brett für Mitarbeiter, die in naher Zukunft ein neues Projekt suchen. Sollte dies für dich interessant sein, sprich bitte deine Führungskraft an.', true);
            */
            this.router.navigate(['/profile']);
            this.alertService.openAccessDialog();
            return false;
          }
          return true;
        })
      );
  }
}
