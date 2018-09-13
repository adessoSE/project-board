import { Component } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../environments/environment';
import { AlertService } from './_services/alert.service';
import { AuthenticationService } from './_services/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private oAuthService: OAuthService,
              private authenticationService: AuthenticationService,
              private alertService: AlertService) {

    // URL of the SPA to redirect the user to after login
    this.oAuthService.redirectUri = window.location.origin + '/index.html';

    // The SPA's id. The SPA is registerd with this id at the auth-server
    this.oAuthService.clientId = 'projekt-boerse';

    // set the scope for the permissions the client should request
    // The first three are defined by OIDC. The 4th is a usecase-specific one
    this.oAuthService.scope = 'openid profile email';

    // set to true, to receive also an id_token via OpenId Connect (OIDC) in addition to the
    // OAuth2-based access_token
    this.oAuthService.oidc = true; // ID_Token
    // this.oAuthService.oidc = false;
    this.oAuthService.skipSubjectCheck = true;

    // Use setStorage to use sessionStorage or another implementation of the TS-type Storage
    // instead of localStorage
    this.oAuthService.setStorage(sessionStorage);

    // Discovery Document of your AuthServer as defined by OIDC
    const url = `${environment.authHost}/auth/realms/adesso/.well-known/openid-configuration`;

    this.oAuthService.issuer = `${environment.authHost}/auth/realms/adesso`;

    // For DEV-Purposes to establish a non https connection to the Keycloak VM on an external server
    // this.oAuthService.requireHttps = false;

    // Load Discovery Document and then try to login the user
    this.oAuthService.loadDiscoveryDocument(url).then(() => {

      // This method just tries to parse the token(s) within the url when
      // the auth-server redirects the user back to the web-app
      // It dosn't send the user the the login page
      this.oAuthService.tryLogin({});
    });
  }

  logout() {
    this.authenticationService.logout();
    sessionStorage.clear();
    this.alertService.success('Du wurdest erfolgreich ausgeloggt.');
  }

  isUserAuthenticated() {
    return this.oAuthService.hasValidAccessToken();
  }

  get isAdmin() {
    return this.authenticationService.isAdmin;
  }
}
