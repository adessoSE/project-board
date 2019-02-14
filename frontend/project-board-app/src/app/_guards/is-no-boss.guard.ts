import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class IsNoBossGuard implements CanActivate {
  constructor(private authenticationService: AuthenticationService,
              private alertService: AlertService,
              private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if (this.authenticationService.isBoss) {
      this.alertService.info('Führungskräfte dürfen keine Projekte über dieses Tool anfragen.', true);
      const projectId = state.url.match('(STF|AD)-[\\d]*')[0];
      if (projectId) {
        this.router.navigate([`/browse/${projectId}`]);
      } else {
        this.router.navigate([`/browse`]);
      }
      return false;
    }
    return true;
  }
}