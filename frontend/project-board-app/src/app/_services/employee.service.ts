import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Project } from './project.service';

@Injectable()
export class EmployeeService {

  constructor(private http: HttpClient) { }

  getEmployeesForSuperUser(superUserId) {
    return this.http.get<Employee[]>(`${environment.resourceServer}/users/${superUserId}/staff`);
  }

  getEmployeeWithId(userId) {
    return this.http.get<Employee>(`${environment.resourceServer}/users/${userId}`);
  }

  setEmployeeAccessInfo(userId, accessEnd) {
    const body = {
      'accessEnd': accessEnd
    };
    return this.http.post<Employee>(`${environment.resourceServer}/users/${userId}/access`, body);
  }

  deleteEmployeeAccessInfo(userId) {
    return this.http.delete<Employee>(`${environment.resourceServer}/users/${userId}/access`);
  }

  getApplications(userId) {
    return this.http.get<Application[]>(`${environment.resourceServer}/users/${userId}/applications`);
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

  getBookmarks(userId) {
    return this.http.get<Project[]>(`${environment.resourceServer}/users/${userId}/bookmarks`);
  }

  addBookmark(userId, projectId) {
    return this.http.post(`${environment.resourceServer}/users/${userId}/bookmarks`, {projectId});
  }

  removeBookmark(userId, projectId) {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/bookmarks/${projectId}`);
  }

  getProjects(userId) {
    return this.http.get<Project[]>(`${environment.resourceServer}/users/${userId}/projects`);
  }
}

export interface Employee {
  id: string;
  duration: number;
  firstName: string;
  lastName: string;
  email: string;
  applications: {
    count: number;
    path: string;
  };
  bookmarks: {
    count: number;
    path: string;
  };
  accessInfo: EmployeeAccessInfo;
}

export interface EmployeeAccessInfo {
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
