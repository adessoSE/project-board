import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';

@Injectable({
  providedIn: 'root'
})
export class BookmarksResolverService implements Resolve<Project[]> {

  constructor(private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  resolve() {
    return this.employeeService.getBookmarks(this.authService.username);
  }
}
