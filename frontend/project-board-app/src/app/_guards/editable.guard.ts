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
export class EditableGuard implements CanActivate {
  constructor(private authenticationService: AuthenticationService,
              private employeeService: EmployeeService,
              private alertService: AlertService,
              private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.employeeService.getProjects(this.authenticationService.username).pipe(map(projects => {
      const project = projects.find(p => {
        return p.id === next.params.id;
      });
      if (project === undefined) {
        this.alertService.info('Du hast keinen Zugriff auf diesen Bereich.', true);
        this.router.navigate([`/overview}`]);
      }
      return project !== undefined;
    }));
  }
}
