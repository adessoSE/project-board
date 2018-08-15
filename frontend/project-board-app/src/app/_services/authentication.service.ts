import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { from } from 'rxjs';

@Injectable()
export class AuthenticationService {
  constructor(private http: HttpClient, private oAuthService: OAuthService) {
  }

  // login(username: string, password: string) {
  //   return this.http.post<any>(environment.authUrl, {username: username, password: password})
  //     .pipe(map(user => {
  //       // login successful if there's a jwt token in the response
  //       if (user && user.token) {
  //         // store user details and jwt token in local storage to keep user logged in between page refreshes
  //         localStorage.setItem('currentUser', JSON.stringify(user));
  //       }
  //
  //       return user;
  //     }));
  // }

  // logout() {
  //   // remove user from local storage to log user out
  //   localStorage.removeItem('currentUser');
  // }

  login(username: string, password: string) {
    // this.oAuthService.initImplicitFlow();
    console.log('name pwd', username, password);
    this.oAuthService.fetchTokenUsingPasswordFlow(username, password).then(() => {
      console.log('load profile');
      // Loading data about the user
      return this.oAuthService.loadUserProfile();
    }).then((profile) => {
      console.log('profile', profile);
      // Using the loaded user data
      let claims: any = this.oAuthService.getIdentityClaims();
      if (claims) console.log('identity claims: given_name', claims.given_name);
      return profile;
    });
    // // this.oAuthService.initImplicitFlow();
    // console.log('name pwd', username, password);
    // return from(this.oAuthService.fetchTokenUsingPasswordFlow(username, password).then(() => {
    //   console.log('load profile');
    //   // Loading data about the user
    //   return this.oAuthService.loadUserProfile();
    // }).then((profile) => {
    //   console.log('profile', profile);
    //   // Using the loaded user data
    //   let claims: any = this.oAuthService.getIdentityClaims();
    //   if (claims) console.log('identity claims: given_name', claims.given_name);
    //   return profile;
    // }));
  }

  logout() {
    this.oAuthService.logOut();
  }

  get name() {
    var claims: any = this.oAuthService.getIdentityClaims();
    if (!claims) return null;

    return claims.name;
  }

  // KEYCLOAK KRAM KOMMT HIER REIN
}

