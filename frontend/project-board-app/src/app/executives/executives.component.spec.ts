import { ExecutivesComponent } from './executives.component';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { Employee, EmployeeService, EmployeeAccessInfo } from '../_services/employee.service';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from "../_services/authentication.service";
import { OAuthService, UrlHelperService } from 'angular-oauth2-oidc';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialog } from '@angular/material';
import { OverlayModule } from '@angular/cdk/overlay';
import {MatDialogModule} from '@angular/material';

describe('Component: Executives', () => {

    let component: ExecutivesComponent;
    let fixture: ComponentFixture<ExecutivesComponent>;

    beforeEach(() => {

        TestBed.configureTestingModule({
            declarations: [ExecutivesComponent],
            imports: [HttpClientTestingModule, RouterTestingModule, FormsModule, OverlayModule, MatDialogModule,],
            providers: [AuthenticationService, OAuthService, UrlHelperService, EmployeeService, MatDialog],
            schemas: [CUSTOM_ELEMENTS_SCHEMA] //For Material Elements
        });

        //create component and test fixture
      fixture = TestBed.createComponent(ExecutivesComponent);

      //get test component from the fixture
      component = fixture.componentInstance;

    });

    //Testing the sort methods for employee listing

        //mock data for different testing

        //has acces today

        const employeeAccesInfoMockToday: EmployeeAccessInfo = {
            hasAccess: true,
            accessStart: new Date,
            accessEnd: new Date
        }

        //has no acces
    
        const employeeAccesInfoMockNot: EmployeeAccessInfo = {
            hasAccess: false,
            accessStart: null,
            accessEnd: null
        }

        var d = new Date();
        d.setDate(d.getDate()-1);

        //gained access 1 day before

        const employeeAccesInfoMockEarlier: EmployeeAccessInfo = {
            hasAccess: true,
            accessStart: d,
            accessEnd: new Date
        }

        var e = new Date();
        e.setDate(e.getDate()+1);

        //has acces 1 day longer

        const employeeAccesInfoMockLonger: EmployeeAccessInfo = {
            hasAccess: true,
            accessStart: new Date,
            accessEnd: e
        }

        //general mock-array of employees
    
        const employeeMock: Employee[] = [
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            }
        ];

        //sortByState(Employee[]) Ascending

        const employeeMockByStateAsc: Employee[] = [
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
        ];

        it('sortByState lists employee with access first and bosses behind', () => {
            component.sortValue = 0;
            expect(component.sortByState(employeeMock)).toEqual(employeeMockByStateAsc);
        }); 

        //sortByState(Employee[]) Descending

        const employeeMockByStateDesc: Employee[] = [
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            }
        ];

        it('sortByState lists employee with access last and bosses before', () => {
            component.sortValue = 1;
            expect(component.sortByState(employeeMock)).toEqual(employeeMockByStateDesc);
        });

        //sortBySince(Employee[]) Ascending

        const employeeMockBySinceAsc: Employee[] = [
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            }
        ];

        it('sortBySince lists employee with newest accessStart first and bosses behind', () => {
            component.sortValue = 0;
            expect(component.sortBySince(employeeMock)).toEqual(employeeMockBySinceAsc);
        });

        //sortBySince(Employee[]) Descending

        const employeeMockBySinceDesc: Employee[] = [
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            }
        ];

        it('sortBySince lists employee with newest accessStart last and bosses behind', () => {
            component.sortValue = 1;
            expect(component.sortBySince(employeeMock)).toEqual(employeeMockBySinceDesc);
        });

        //sortByUntil(Employee[]) Ascending

        const employeeMockByUntilAsc: Employee[] = [
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
        ];

        it('sortByUntil lists employee with accessEnd closest first and bosses behind', () => {
            component.sortValue = 0;
            expect(component.sortByUntil(employeeMock)).toEqual(employeeMockByUntilAsc);
        });

        //sortByUntil(Employee[]) Descending

        const employeeMockByUntilDesc: Employee[] = [
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
        ];

        it('sortByUntil lists employee with accessEnd closest last and bosses behind', () => {
            component.sortValue = 1;
            expect(component.sortByUntil(employeeMock)).toEqual(employeeMockByUntilDesc);
        });

        //sortByRequest(Employee[]) Ascending

        const employeeMockByRequestAscending: Employee[] = [
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            }
        ];

        it('sortByRequest lists employee with applications ascending and bosses behind', () => {
            component.sortValue = 0;
            console.log(component.sortByRequest(employeeMock));
            expect(component.sortByRequest(employeeMock)).toEqual(employeeMockByRequestAscending);
        });

        //sortByRequest(Employee[]) Descending

        const employeeMockByRequestDescending: Employee[] = [
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            }
        ];

        it('sortByRequest lists employee with applications descending and bosses behind', () => {
            component.sortValue = 1;
            expect(component.sortByRequest(employeeMock)).toEqual(employeeMockByRequestDescending);
        });


        //sortAlphabetically(Employee[]) Ascending

        const employeeMockAlphabeticallyAsc: Employee[] = [
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            }
        ];

        it('sortAlphabetically lists employee sorted by lastName Ascending', () => {
            component.sortValue = 1;
            expect(component.sortAlphabetically(employeeMock)).toEqual(employeeMockAlphabeticallyAsc);
        });

        //sortAlphabetically(Employee[]) Descending

        const employeeMockAlphabeticallyDesc: Employee[] = [
            {
                id: "Test6",
                duration: null,
                firstName: "Merlin",
                lastName: "Emann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 2,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockEarlier
            },
            {
                id: "Test5",
                duration: null,
                firstName: "Marius",
                lastName: "Dmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 0,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockLonger
            },
            {
                id: "Test3",
                duration: null,
                firstName: "Max",
                lastName: "Cmann",
                picture: "string",
                email: "test@test.com",
                boss: true,
                applications: 0,
                bookmarks: 0,
                accessInfo: null
            },
            {
                id: "Test2",
                duration: 1,
                firstName: "Martin",
                lastName: "Bmann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 1,
                bookmarks: 1,
                accessInfo: employeeAccesInfoMockNot
            },
            {
                id: "Test1",
                duration: 1,
                firstName: "Marco",
                lastName: "Amann",
                picture: "string",
                email: "test@test.com",
                boss: false,
                applications: 3,
                bookmarks: 2,
                accessInfo: employeeAccesInfoMockToday
            }
        ];

        it('sortAlphabetically lists employee sorted by lastName Descending', () => {
            component.sortValue = 0;
            expect(component.sortAlphabetically(employeeMock)).toEqual(employeeMockAlphabeticallyDesc);
        });
});