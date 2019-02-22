import { TestBed, ComponentFixture } from '@angular/core/testing';
import  {AppComponent } from './app.component';
import { AuthenticationService } from "./_services/authentication.service";
import { EmployeeService } from './_services/employee.service';
import { JwksValidationHandler, OAuthService, UrlHelperService } from 'angular-oauth2-oidc';
import { authConfig } from './app.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('Component: App', () => {

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let authenticationService: AuthenticationService;
  let oAuthService: OAuthService;

  beforeEach(() => {

      TestBed.configureTestingModule({
          declarations: [AppComponent],
          imports: [HttpClientTestingModule], //For Http in EmplyeeService
          providers: [AuthenticationService, EmployeeService, OAuthService, UrlHelperService],
          schemas: [CUSTOM_ELEMENTS_SCHEMA] //For Material Elements
      });

      //create component and test fixture
      fixture = TestBed.createComponent(AppComponent);

      //get test component from the fixture
      component = fixture.componentInstance;

      //Services Provided to the TestBed
      authenticationService = TestBed.get(AuthenticationService);
      oAuthService = TestBed.get(OAuthService);
      oAuthService.configure(authConfig);
      oAuthService.tokenValidationHandler = new JwksValidationHandler();
      oAuthService.setupAutomaticSilentRefresh();

  });

        //Test cases

        //isUserAuthenticated()
    it('isUserAuthenticated returns true if user is authenticated', () => {
          spyOn(oAuthService, 'hasValidAccessToken').and.returnValue(true);
          expect(component.isUserAuthenticated()).toBeTruthy;
          expect(oAuthService.hasValidAccessToken).toHaveBeenCalled;
    });

        //isUserAuthenticated()
    it('isUserAuthenticated returns false if user is not authenticated', () => {
        spyOn(oAuthService, 'hasValidAccessToken').and.returnValue(false);
        expect(component.isUserAuthenticated()).toBeFalsy;
        expect(oAuthService.hasValidAccessToken).toHaveBeenCalled;
    });

        //getUsername()
    it('getUsername returns username if IdentityClaims available', () => {
        spyOnProperty(authenticationService, 'username', 'get').and.returnValue('TestUserName');
        expect(component.getUsername()).toEqual('TestUserName');
        expect(authenticationService.username).toHaveBeenCalled;
    });

        //getUsername()
    it('getUsername returns null if IdentityClaims not available', () => {
        spyOnProperty(authenticationService, 'username', 'get').and.returnValue(null);
        expect(component.getUsername()).toEqual(null);
        expect(authenticationService.username).toHaveBeenCalled;
    });

        //isBoss()
    it('isBoss returns true if the directReports claim is present', () => {
        spyOnProperty(authenticationService, 'isBoss', 'get').and.returnValue(true);
        expect(component.isBoss).toBeTruthy;
        expect(authenticationService.isBoss).toHaveBeenCalled;
    });

        //isBoss()
    it('isBoss returns false if the directReports claim is not present', () => {
        spyOnProperty(authenticationService, 'isBoss', 'get').and.returnValue(false);
        expect(component.isBoss).toBeFalsy;
        expect(authenticationService.isBoss).toHaveBeenCalled;
    });
  });