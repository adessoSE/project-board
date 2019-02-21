import { OverlayModule } from '@angular/cdk/overlay';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { JwksValidationHandler, OAuthService, UrlHelperService } from 'angular-oauth2-oidc';
import { AuthenticationService } from '../_services/authentication.service';
import { Application, Employee, EmployeeAccessInfo, EmployeeService } from '../_services/employee.service';
import { Project, ProjectService } from '../_services/project.service';
import { authConfig } from '../app.component';
import { ProfileComponent } from './profile.component';

describe('Component: App', () => {

  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let employeeService: EmployeeService;
  let oAuthService: OAuthService;

  beforeEach(() => {

      TestBed.configureTestingModule({
          declarations: [ProfileComponent],
          imports: [HttpClientTestingModule, RouterTestingModule, OverlayModule, MatDialogModule], //For Http in EmplyeeService
          providers: [AuthenticationService, EmployeeService, MatDialog, OAuthService, UrlHelperService, ProjectService],
          schemas: [CUSTOM_ELEMENTS_SCHEMA] //For Material Elements
      });

      //create component and test fixture
      fixture = TestBed.createComponent(ProfileComponent);

      //get test component from the fixture
      component = fixture.componentInstance;

      //Services Provided to the TestBed
      employeeService = TestBed.get(EmployeeService);
      oAuthService = TestBed.get(OAuthService);
      oAuthService.configure(authConfig);
      oAuthService.tokenValidationHandler = new JwksValidationHandler();
      oAuthService.setupAutomaticSilentRefresh();
  });

        //Mocking for Tests

        const employeeAccesInfoMock: EmployeeAccessInfo = {
            hasAccess: true,
            accessStart: new Date,
            accessEnd: new Date
        };

        const employeeMock: Employee = {
            id: "TestEmployeeId",
            duration: 12,
            firstName: "TestFirstName",
            lastName: "TestLastName",
            picture:  "TestPicture",
            email:  "test@testmail.com",
            boss: true,
            applications: 2,
            bookmarks: 2,
            accessInfo: employeeAccesInfoMock
        };

        const projectMock: Project[] = [{
            labels: ["Label 1", "Label 2"],
            customer: "TestCustomer 1",
            description: "TestDescription 1",
            effort: "TestEffort 1",
            elongation: "TestElongation 1",
            freelancer: "TestFreelancer 1",
            id: "TestIdValid",              //This Id is relevant for testing
            issuetype: "TestIssuetype 1",
            job: "TestJob 1",
            lob: "TestLob 1",
            location: "TestLocation 1",
            operationEnd: "TestOperationEnd 1",
            operationStart: "TestOperationStart 1",
            other: "TestOther 1",
            skills: "TestSkills 1",
            status: "TestStatus 1",
            title: "TestTitle 1",
            dailyRate: "TestDailyRate 1",
            travelCostsCompensated: "TestTravelCostsCompensated 1",
            created: new Date(),
            updated: new Date(),
        }];


        const applicationsMock: Application[] = [
            {
                id: 12,
                user: employeeMock,
                project: projectMock[0],
                comment:  "string",
                date: new Date
            }
        ];

        //Test cases

        //isProjectApplicable()
    it('isProjectApplicable returns false if project is already applicated', () => {
        component.applications = applicationsMock;
        expect(component.isProjectApplicable("TestIdValid")).toBeFalsy();
        expect(employeeService.getApplications).toHaveBeenCalled;
    });

        //isProjectApplicable()
    it('isProjectApplicable returns true if project is not applicated', () => {
        component.applications = applicationsMock;
        expect(component.isProjectApplicable("TestIdInvalid")).toBeTruthy();
        expect(employeeService.getApplications).toHaveBeenCalled;
    });

        //isProjectBookmarked()
    it('isProjectBookmarked returns true if project is bookmarked', () => {
        component.bookmarks = projectMock;
        expect(component.isProjectBookmarked("TestIdValid")).toBeTruthy();
        expect(employeeService.getBookmarks).toHaveBeenCalled;
    });

        //isProjectBookmarked()
    it('isProjectBookmarked returns false if project is not bookmarked', () => {
        component.bookmarks = projectMock;
        expect(component.isProjectBookmarked("TestIdInvalid")).toBeFalsy();
        expect(employeeService.getBookmarks).toHaveBeenCalled;
    });
});
