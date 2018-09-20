import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { AuthenticationService } from './authentication.service';
import { EmployeeService } from './employee.service';
import { Project } from './project.service';

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
