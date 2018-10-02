import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { EmployeeService } from './employee.service';
import { Project } from './project.service';

@Injectable()
export class CreatedProjectsResolverService implements Resolve<Project[]> {

  constructor(private employeeService: EmployeeService,
              private authenticationService: AuthenticationService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Project[]> {
    return this.employeeService.getProjects(this.authenticationService.username);
  }
}
