import { Component, HostListener } from '@angular/core';
import { faChevronUp } from '@fortawesome/free-solid-svg-icons/faChevronUp';
import { AuthConfig, JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../environments/environment';
import { AlertService } from './_services/alert.service';
import { AuthenticationService } from './_services/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  faChevronUp = faChevronUp;
  username = "default";

  constructor(private oAuthService: OAuthService,
              private authenticationService: AuthenticationService,
              private alertService: AlertService) {
    this.configureWithNewConfigApi();
  }

  private configureWithNewConfigApi() {
    this.oAuthService.configure(authConfig);
    this.oAuthService.tokenValidationHandler = new JwksValidationHandler();
    this.oAuthService.setupAutomaticSilentRefresh();
    this.oAuthService.loadDiscoveryDocumentAndLogin();
  }

  getUsername(){
    return this.authenticationService.username;
  }

  ngDoCheck(){
    this.username = this.getUsername();
  }

  logout() {
    this.oAuthService.logOut();
    sessionStorage.clear();
    this.alertService.success('Du wurdest erfolgreich ausgeloggt.');
  }

  isUserAuthenticated() {
    return this.oAuthService.hasValidAccessToken();
  }

  get isAdmin() {
    return this.authenticationService.isAdmin;
  }

  @HostListener('window:scroll') onScroll() {
    if (/Mobi/.test(navigator.userAgent)) {
      // mobile!
      if (document.body.scrollTop > 400 || document.documentElement.scrollTop > 400) {
        document.getElementById('top-badge').style.setProperty('display', 'inline');
      } else {
        document.getElementById('top-badge').style.setProperty('display', 'none');
      }
    }
  }

  scrollTop() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }
}

export const authConfig: AuthConfig = {

  // Url of the Identity Provider
  issuer: `${environment.authHost}/auth/realms/adesso`,

  // URL of the SPA to redirect the user to after login
  redirectUri: window.location.origin + '/index.html',

  // The SPA's id. The SPA is registerd with this id at the auth-server
  clientId: 'projekt-boerse-frontend',

  // set the scope for the permissions the client should request
  // The first three are defined by OIDC. The 4th is a usecase-specific one
  scope: 'openid profile email',
  oidc: true
};
