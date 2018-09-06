import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router,
              private oAuthService: OAuthService,
              private authenticationService: AuthenticationService,
              private alertService: AlertService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (this.oAuthService.hasValidAccessToken()) {
      // logged in so return true
      return true;
    }

    if (state.url) {
      this.alertService.error('Deine Sitzung ist abgelaufen.', true);
    }

    // not logged in so redirect to login page with the return url
    this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
    return false;
  }
}
