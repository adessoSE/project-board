import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';
import { EmployeeService } from './employee.service';
import { Project } from './project.service';

@Injectable({
  providedIn: 'root'
})
export class AppliedProjectsResolverService implements Resolve<Project[]> {

  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Project[]> {
    return this.employeeService.getApplications(this.authenticationService.username).pipe(map(apps => {
      const projects: Project[] = [];
      for (const a of apps) {
        projects.push(a.project);
      }
      return projects;
    }));
  }
}
