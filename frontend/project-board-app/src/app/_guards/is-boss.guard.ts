import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class IsBossGuard implements CanActivate {
  constructor(private authenticationService: AuthenticationService,
              private alertService: AlertService,
              private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if (!this.authenticationService.isBoss) {
      this.alertService.info('Du hast keinen Zugriff auf diesen Bereich.', true);
      this.router.navigate([`/browse`]);
      return false;
    }
    return true;
  }
}