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
export class AlreadyAppliedGuard implements CanActivate {
  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService,
              private alertService: AlertService,
              private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.employeeService.getApplications(this.authenticationService.username).pipe(map(apps => {
      const application = apps.find(a => {
        return a.project.id.toString() === next.params.id;
      });
      if (application !== undefined) {
        this.alertService.info('Du hast dieses Projekt bereits angefragt.', true);
        this.router.navigate([`/projects/${application.project.key}`]);
      }
      return application === undefined;
    }));
  }
}
