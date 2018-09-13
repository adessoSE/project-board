import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Project } from './project.service';

@Injectable()
export class EmployeeService {

  constructor(private http: HttpClient) { }

  getEmployees() {
    return this.http.get<Employee[]>('./assets/employees.json');
  }

  // getEmployees() {
  //   return this.http.get<Employee[]>(`${environment.resourceServer}/users`);
  // }

  getEmployeeWithId(userId) {
    return this.http.get<Employee>(`${environment.resourceServer}/users/${userId}`);
  }

  getEmployeeAccessInfo(userId) {
    return this.http.get<EmployeeAccessInfo>(`${environment.resourceServer}/users/${userId}/access`);
  }

  setEmployeeAccessInfo(userId, accessEnd: Date) {
    return this.http.post(`${environment.resourceServer}/users/${userId}/access`, accessEnd);
  }

  getApplications(userId) {
    return this.http.get<Application[]>(`${environment.resourceServer}/users/${userId}/applications`);
  }

  getApplicationsMock(userId) {
    return this.http.get<Application[]>('./assets/applications.json').pipe(
      map(data => data.filter(a => a.user.id === userId))
    );
  }

  revokeApplication(userId, appId) {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/applications/${appId}`);
  }

  applyForProject(userId, projectId, comment) {
    const body = {
      'projectId': projectId,
      'comment': comment
    };
    return this.http.post(`${environment.resourceServer}/users/${userId}/applications`, body);
  }

  getFavorites(userId) {
    return this.http.get<Project[]>(`${environment.resourceServer}/users/${userId}/bookmarks`);
  }

  addToFavorites(userId, projectId) {
    return this.http.post(`${environment.resourceServer}/users/${userId}/bookmarks`, {projectId});
  }

  removeFromFavorites(userId, projectId) {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/bookmarks/${projectId}`);
  }
}

export interface Employee {
  id: string;
  enabled: boolean;
  duration: number;
  name: {
    first: string,
    last: string
  };
  fullName: string;
  email: string;
  applications: {
    count: number;
    path: string;
  };
  bookmarks: {
    count: number;
    path: string;
  };
}

export interface EmployeeAccessInfo {
  user: Employee;
  hasAccess: boolean;
  accessStart: Date;
  accessEnd: Date;
}

export interface Application {
  id: number;
  user: Employee;
  project: Project;
  comment: string;
  date: Date;
}
