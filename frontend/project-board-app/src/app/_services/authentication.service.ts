import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { from } from 'rxjs';

@Injectable()
export class AuthenticationService {
  constructor(private http: HttpClient, private oAuthService: OAuthService) {
  }

  login(username: string, password: string) {
    console.log('name pwd', username, password);
    return from(this.oAuthService.fetchTokenUsingPasswordFlow(username, password).then((xyz) => {
      // Loading data about the user
      return this.oAuthService.loadUserProfile();
    }));
  }

  logout() {
    this.oAuthService.logOut();
  }

  get name() {
    let claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) return null;

    return claims.name;
  }
}

