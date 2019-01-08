import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Project } from './project.service';

@Injectable()
export class EmployeeService {

  constructor(private http: HttpClient) {}

  isUserBoss(userId: string) {
    return this.getEmployeeWithId(userId)
      .pipe(map(empl => empl.boss));
  }

  getEmployeesForSuperUser(superUserId: string) {
    return this.http.get<Employee[]>(`${environment.resourceServer}/users/${superUserId}/staff`);
  }

  getEmployeeWithId(userId: string) {
    return this.http.get<Employee>(`${environment.resourceServer}/users/${userId}`);
  }

  getApplicationsForEmployeesOfUser(userId: string) {
    return this.http.get<Application[]>(`${environment.resourceServer}/users/${userId}/staff/applications`);
  }

  setEmployeeAccessInfo(userId: string, accessEnd) {
    const body = {
      'accessEnd': accessEnd
    };
    return this.http.post<Employee>(`${environment.resourceServer}/users/${userId}/access`, body);
  }

  deleteEmployeeAccessInfo(userId: string) {
    return this.http.delete<Employee>(`${environment.resourceServer}/users/${userId}/access`);
  }

  getApplications(userId: string) {
    return this.http.get<Application[]>(`${environment.resourceServer}/users/${userId}/applications`);
  }

  revokeApplication(userId, appId) {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/applications/${appId}`);
  }

  applyForProject(userId: string, projectId: string, comment: string) {
    const body = {
      'projectId': projectId,
      'comment': comment
    };
    return this.http.post<Application>(`${environment.resourceServer}/users/${userId}/applications`, body);
  }

  getBookmarks(userId: string) {
    return this.http.get<Project[]>(`${environment.resourceServer}/users/${userId}/bookmarks`);
  }

  addBookmark(userId: string, projectId: string) {
    return this.http.post<Project>(`${environment.resourceServer}/users/${userId}/bookmarks`, {projectId});
  }

  removeBookmark(userId: string, projectId: string) {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/bookmarks/${projectId}`);
  }

  getProjects(userId: string) {
    return this.http.get<Project[]>(`${environment.resourceServer}/users/${userId}/projects`);
  }
}

export interface Employee {
  id: string;
  duration: number;
  firstName: string;
  lastName: string;
  email: string;
  boss: boolean;
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
