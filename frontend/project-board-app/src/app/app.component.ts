import {Component, DoCheck, ElementRef, HostListener, NgZone, OnInit, Renderer, ViewChild} from '@angular/core';
import {Event as RouterEvent, NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from '@angular/router';
import {MatSidenav} from '@angular/material';
import {faChevronUp} from '@fortawesome/free-solid-svg-icons/faChevronUp';
import {AuthConfig, JwksValidationHandler, OAuthService} from 'angular-oauth2-oidc';
import * as $ from 'jquery';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {environment} from '../environments/environment';
import {AuthenticationService} from './_services/authentication.service';
import {EmployeeService} from './_services/employee.service';
import {
  CONTACT_SUPPORT_TOOLTIP,
  EMPLOYEES_TOOLTIP,
  FAQ_TOOLTIP,
  LOGOUT_TOOLTIP,
  NO_ACCESS_TOOLTIP,
  PROFILE_TOOLTIP,
  PROJECTS_TOOLTIP
} from './tooltips';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, DoCheck {
  
  faChevronUp = faChevronUp;
  username = 'default';
  boss: boolean;
  hasAccess = false;
  noAccessTooltip = NO_ACCESS_TOOLTIP;
  contactSupportTooltip = CONTACT_SUPPORT_TOOLTIP;
  profileTooltip = PROFILE_TOOLTIP;
  projectsTooltip = PROJECTS_TOOLTIP;
  employeesTooltip = EMPLOYEES_TOOLTIP;
  faqTooltip = FAQ_TOOLTIP;
  logoutTooltip = LOGOUT_TOOLTIP;
  supportEmail: string;
  destroy$ = new Subject();

  @ViewChild('snav') sidenav: MatSidenav;
  @ViewChild('spinnerElement')
  spinnerElement: ElementRef;

  constructor(private authenticationService: AuthenticationService,
              private employeeService: EmployeeService,
              private oAuthService: OAuthService,
              private router: Router,
              private ngZone: NgZone,
              private renderer: Renderer
  ) { 
    router.events.subscribe((event: RouterEvent) => {
      this._navigationInterceptor(event)
    });
    this.configureWithNewConfigApi(); 
  }

   // Shows and hides the loading spinner during RouterEvent changes
   private _navigationInterceptor(event: RouterEvent): void {
    if (event instanceof NavigationStart) {
      this.ngZone.runOutsideAngular(() => {
        this.renderer.setElementStyle(
          this.spinnerElement.nativeElement,
          'display',
          'block'
        )
      })
    }
    if (event instanceof NavigationEnd) {
      this._hideSpinner()
    }
    // Set loading state to false in both of the below events to
    // hide the spinner in case a request fails
    if (event instanceof NavigationCancel) {
      this._hideSpinner()
    }
    if (event instanceof NavigationError) {
      this._hideSpinner()
    }
  }

  private _hideSpinner(): void {
    // We wanna run this function outside of Angular's zone to
    // bypass change detection,
    this.ngZone.runOutsideAngular(() => {
      // For simplicity we are going to turn opacity on / off
      // you could add/remove a class for more advanced styling
      // and enter/leave animation of the spinner
      this.renderer.setElementStyle(
        this.spinnerElement.nativeElement,
        'display',
        'none'
      )
    })
  }

  private configureWithNewConfigApi(): void {
    this.oAuthService.configure(authConfig);
    this.oAuthService.tokenValidationHandler = new JwksValidationHandler();
    this.oAuthService.setupAutomaticSilentRefresh();
    this.oAuthService.clearHashAfterLogin = false;
    this.oAuthService.loadDiscoveryDocumentAndLogin().then(loggedIn => {
      if (loggedIn) {
        this.router.navigateByUrl(sessionStorage.getItem("info"));
        if (this.isBoss) {
          this.hasAccess = true;
          return;
        }
        this.employeeService.hasUserAccess(this.authenticationService.username)
          .pipe(takeUntil(this.destroy$))
          .subscribe(response => this.hasAccess = response.hasAccess);
      }
    });
  }

  ngOnInit(): void {
    this.sidenav.openedStart.subscribe(() => this.onNavOpen());
    this.sidenav.closedStart.subscribe(() => this.onNavClosed());
    this.miniNavVisibility();
    this.mainNavPosition();
    this.supportEmail = environment.supportEmail;
  }

  /* Tested Methods Start */

  isUserAuthenticated(): boolean {
    return this.oAuthService.hasValidAccessToken();
  }

  get isBoss(): boolean {
    return this.authenticationService.isBoss;
  }

  getUsername(): string {
    return this.authenticationService.username;
  }

  /* Tested Methods End */

  /* Sidenav responsive */

  toggleNav(): void {
    if (this.sidenav.opened) {
      this.sidenav.close();
    } else {
      this.sidenav.open();
    }
  }

  openNav(): void {
    if (window.innerWidth < 992) {
      this.sidenav.open();
    }
  }

  closeNav(): void {
    if (window.innerWidth < 992) {
      this.sidenav.close();
    }
  }

  onNavOpen(): void {
    if (/Mobi/.test(navigator.userAgent)) {
      $('body').css('overflow', 'hidden');
      document.getElementById('top-badge').style.visibility = 'hidden';
    }
  }

  onNavClosed(): void {
    if (/Mobi/.test(navigator.userAgent)) {
      $('body').css('overflow', 'auto');
      document.getElementById('top-badge').style.visibility = 'visible';
    }
  }

  onResize(): void {
    this.sidenav.close();
    $('body').css('overflow', 'auto');
    document.getElementById('top-badge').style.visibility = 'visible';
    this.miniNavVisibility();
    this.mainNavPosition();
  }

  miniNavVisibility(): void {
    if (/Mobi/.test(navigator.userAgent) || (window.innerWidth < 992)) {
      document.getElementById('mini-nav').style.display = 'none';
    } else {
      document.getElementById('mini-nav').style.display = 'block';
    }
  }

  mainNavPosition(): void {
    if (!(/Mobi/.test(navigator.userAgent) || (window.innerWidth < 992))) {
      document.getElementById('main-nav').style.position = 'relative';
    } else {
      document.getElementById('main-nav').style.position = 'sticky';
    }
  }

  ngDoCheck(): void {
    this.username = this.getUsername();
  }

  logout(): void {
    this.oAuthService.logOut();
    sessionStorage.clear();
    /* this.alertService.success('Du wurdest erfolgreich ausgeloggt.'); */
  }

  @HostListener('window:scroll')
  onScroll(): void {
    // Toggle for the mini-menu
    if (document.documentElement.scrollTop > 340) {
      if ((document.getElementById('mini-nav').offsetLeft === -45) && !($('#mini-nav').is(':animated'))) {
        $('#mini-nav').animate({left: '0px'}, function (): void {
          if (document.documentElement.scrollTop <= 340) {
            $('#mini-nav').animate({left: '-45px'});
          }
        });
      }
    } else {
      if ((document.getElementById('mini-nav').offsetLeft === 0) && !($('#mini-nav').is(':animated'))) {
        $('#mini-nav').animate({left: '-45px'}, function (): void {
          if (document.documentElement.scrollTop > 340) {
            $('#mini-nav').animate({left: '0px'});
          }
        });
      }
    }

    if (/Mobi/.test(navigator.userAgent)) {
      // mobile!
      if (document.body.scrollTop > 400 || document.documentElement.scrollTop > 400) {
        document.getElementById('top-badge').style.setProperty('display', 'inline');
      } else {
        document.getElementById('top-badge').style.setProperty('display', 'none');
      }
    }
  }

  scrollTop(): void {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }

  mailToSupport() {
    window.location.href = `mailTo:${this.supportEmail}`;
  }
}

export const authConfig: AuthConfig = {

  // Url of the Identity Provider
  issuer: `${environment.authHost}/auth/realms/adesso`,

  // URL of the SPA to redirect the user to after login
  redirectUri: window.location.origin + '/index.html',

  // The SPA's id. The SPA is registerd with this id at the auth-server
  clientId: 'projectboard-frontend',

  // set the scope for the permissions the client should request
  // The first three are defined by OIDC. The 4th is a usecase-specific one
  scope: 'openid profile email directReports',
  oidc: true

  // requireHttps: false,
};
