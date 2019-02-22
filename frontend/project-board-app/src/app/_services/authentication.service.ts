import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { from, Observable } from 'rxjs';

@Injectable()
export class AuthenticationService {
  constructor(private oAuthService: OAuthService) {
  }

  login(username: string, password: string): Observable<any> {
    return from(this.oAuthService.fetchTokenUsingPasswordFlowAndLoadUserProfile(username, password));
  }

  logout(): void {
    this.oAuthService.logOut();
  }

  get token(): string {
    return this.oAuthService.getAccessToken();
  }

  get name(): string {
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return null;
    }
    return claims.name;
  }

  get username(): string {
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return null;
    }
    return claims.preferred_username;
  }

  private hasUserRole(role: string): boolean {
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return false;
    }
    return claims.scope.includes(role);
  }

  get isAdmin(): boolean {
    if (this.hasUserRole('admin')) {
      return true;
    }
  }

  get isBoss(): boolean {
    // check if the "directReports" claim is present
    // if so, the user is a boss
    const claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return false;
    }
    return claims.directReports != null;
  }
}
