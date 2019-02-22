import { TestBed } from '@angular/core/testing';
import { ProjectService, Project } from './project.service';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('Service: ProjectService', () => {        // suite for the tests
  let projectService: ProjectService; 

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [ProjectService]       // setup service we want to test
    });

    projectService = TestBed.get(ProjectService); // inject service to suite
  });

  //test if service is created
  it('should be created', () => {
    expect(projectService).toBeTruthy();
  });

  //testing getAllProjects
    describe('getAllProjects', () => {
        it('should return an array of all projects', () => {
            const projectArrayResponse: Project[] = [
                {
                    labels: ["Label 1", "Label 2"],
                    customer: "TestCustomer 1",
                    description: "TestDescription 1",
                    effort: "TestEffort 1",
                    elongation: "TestElongation 1",
                    freelancer: "TestFreelancer 1",
                    id: "TestId 1",
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
                },
                {
                    labels: ["Label 3", "Label 4"],
                    customer: "TestCustomer 2",
                    description: "TestDescription 2",
                    effort: "TestEffort 2",
                    elongation: "TestElongation 2",
                    freelancer: "TestFreelancer 2",
                    id: "TestId 2",
                    issuetype: "TestIssuetype 2",
                    job: "TestJob 2",
                    lob: "TestLob 2",
                    location: "TestLocation 2",
                    operationEnd: "TestOperationEnd 2",
                    operationStart: "TestOperationStart 2",
                    other: "TestOther 2",
                    skills: "TestSkills 2",
                    status: "TestStatus 2",
                    title: "TestTitle 2",
                    dailyRate: "TestDailyRate 2",
                    travelCostsCompensated: "TestTravelCostsCompensated 2",
                    created: new Date(),
                    updated: new Date(),
                }
            ];

            let response;
            spyOn(projectService, 'getAllProjects').and.returnValue(of(projectArrayResponse));

            projectService.getAllProjects().subscribe(res => {
                response = res;
            });
            
            expect(response).toEqual(projectArrayResponse);
        });
    });  
    
    //testing getProjectWithID
    describe('getProjectWithID', () => {
        it('should return a single project', () => {
            const singleProjectResponse =
                {
                    labels: ["Label 1", "Label 2"],
                    customer: "TestCustomer 1",
                    description: "TestDescription 1",
                    effort: "TestEffort 1",
                    elongation: "TestElongation 1",
                    freelancer: "TestFreelancer 1",
                    id: "TestId 1",
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
                };

            let response;
            spyOn(projectService, 'getProjectWithID').and.returnValue(of(singleProjectResponse));

            projectService.getProjectWithID("testId").subscribe(res => {
                response = res;
            });
            
            expect(response).toEqual(singleProjectResponse);
        });
    });

    //testing search
    describe('search', () => {
        it('should return an array of filtered projects', () => {
            const projectFilteredArrayResponse = [
                {
                    labels: ["Label 1", "Label 2"],
                    customer: "TestCustomer 1",
                    description: "TestDescription 1",
                    effort: "TestEffort 1",
                    elongation: "TestElongation 1",
                    freelancer: "TestFreelancer 1",
                    id: "TestId 1",
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
                },
                {
                    labels: ["Label 3", "Label 4"],
                    customer: "TestCustomer 2",
                    description: "TestDescription 2",
                    effort: "TestEffort 2",
                    elongation: "TestElongation 2",
                    freelancer: "TestFreelancer 2",
                    id: "TestId 2",
                    issuetype: "TestIssuetype 2",
                    job: "TestJob 2",
                    lob: "TestLob 2",
                    location: "TestLocation 2",
                    operationEnd: "TestOperationEnd 2",
                    operationStart: "TestOperationStart 2",
                    other: "TestOther 2",
                    skills: "TestSkills 2",
                    status: "TestStatus 2",
                    title: "TestTitle 2",
                    dailyRate: "TestDailyRate 2",
                    travelCostsCompensated: "TestTravelCostsCompensated 2",
                    created: new Date(),
                    updated: new Date(),
                }
            ];

            let response;
            spyOn(projectService, 'search').and.returnValue(of(projectFilteredArrayResponse));

            projectService.search("testKeyword").subscribe(res => {
                response = res;
            });
            
            expect(response).toEqual(projectFilteredArrayResponse);
        });
    });  
});