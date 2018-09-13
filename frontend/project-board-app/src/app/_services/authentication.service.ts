import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { from } from 'rxjs';

@Injectable()
export class AuthenticationService {
  constructor(private http: HttpClient, private oAuthService: OAuthService) {
  }

  login(username: string, password: string) {
    return from(this.oAuthService.fetchTokenUsingPasswordFlowAndLoadUserProfile(username, password));
  }

  logout() {
    this.oAuthService.logOut();
  }

  get token() {
    return this.oAuthService.getAccessToken();
  }

  get name() {
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return null;
    }
    return claims.name;
  }

  get username() {
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return null;
    }
    return claims.preferred_username;
  }

  private hasUserRole(role: string) {
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return false;
    }
    return claims.scope.includes(role);
  }

  get isAdmin() {
    return this.hasUserRole('admin');
  }
}
