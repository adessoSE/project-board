import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { from } from 'rxjs';

@Injectable()
export class AuthenticationService {
  constructor(private http: HttpClient, private oAuthService: OAuthService) {
  }

  login(username: string, password: string) {
    return from(this.oAuthService.fetchTokenUsingPasswordFlow(username, password).then(() => {
      // Loading data about the user
      return this.oAuthService.loadUserProfile();
    }));
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
}

