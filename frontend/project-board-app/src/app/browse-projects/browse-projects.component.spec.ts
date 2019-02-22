import { TestBed, ComponentFixture } from '@angular/core/testing';
import { BrowseProjectsComponent } from './browse-projects.component';
import { AuthenticationService } from "../_services/authentication.service";
import { Application, Employee, EmployeeService, EmployeeAccessInfo } from '../_services/employee.service';
import { ProjectService } from '../_services/project.service';
import { MatDialog } from '@angular/material';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Project } from '../_services/project.service';
import { RouterTestingModule } from '@angular/router/testing';
import { JwksValidationHandler, OAuthService, UrlHelperService } from 'angular-oauth2-oidc';
import { authConfig } from '../app.component';
import { OverlayModule } from '@angular/cdk/overlay';
import {MatDialogModule} from '@angular/material';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../_services/alert.service';

describe('Component: BrowseProjects', () => {

  let component: BrowseProjectsComponent;
  let fixture: ComponentFixture<BrowseProjectsComponent>;
  let employeeService: EmployeeService;
  let oAuthService: OAuthService;

  beforeEach(() => {

      TestBed.configureTestingModule({
          declarations: [BrowseProjectsComponent],
          imports: [HttpClientTestingModule, RouterTestingModule, OverlayModule, MatDialogModule, FormsModule], //For Http in EmplyeeService
          providers: [AuthenticationService, EmployeeService, MatDialog, OAuthService, UrlHelperService, ProjectService, AlertService],
          schemas: [CUSTOM_ELEMENTS_SCHEMA] //For Material Elements
      });

      //create component and test fixture
      fixture = TestBed.createComponent(BrowseProjectsComponent);

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
        }

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
        }

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
            attachment: false,
        }]


        const applicationsMock: Application[] = [
            {
                id: 12,
                user: employeeMock,
                project: projectMock[0],
                comment:  "string",
                date: new Date
            }
        ]

        //Test cases

        //isProjectApplicable()
    it('isProjectApplicable returns false if project is not applicable', () => {
        component.applications = applicationsMock;
        expect(component.isProjectApplicable("TestIdValid")).toBeFalsy();
        expect(employeeService.getApplications).toHaveBeenCalled;
    });

        //isProjectApplicable()
    it('isProjectApplicable returns true if project is applicable', () => {
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