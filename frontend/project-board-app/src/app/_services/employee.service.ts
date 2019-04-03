import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Project } from './project.service';

@Injectable()
export class EmployeeService {

  constructor(private http: HttpClient) {}

  hasUserAccess(userId: string): Observable<{ id: string, hasAccess: boolean }> {
    return this.http.get<{ id: string, hasAccess: boolean }>(`${environment.resourceServer}/users/${userId}?projection=hasAccess`);
  }

  getEmployeesWithoutPicturesForSuperUser(superUserId: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${environment.resourceServer}/users/${superUserId}/staff`);
  }

  getEmployeePicturesForSuperUser(superUserId: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${environment.resourceServer}/users/${superUserId}/staff?projection=pictureonly`);
  }

  getFullEmployeeForId(userId: string): Observable<Employee> {
    return this.http.get<Employee>(`${environment.resourceServer}/users/${userId}?projection=withpicture`);
  }

  getApplications(userId: string): Observable<Application[]> {
    return this.http.get<Application[]>(`${environment.resourceServer}/users/${userId}/applications`);
  }

  search(query: string, userId: string): Observable<Employee[]> {
    query = encodeURI(query.replace('&', ' '));
    return this.http.get<Employee[]>(`${environment.resourceServer}/users/${userId}/staff/search?query=${query}&projection=withpicture`);
  }

  changeApplicationState(userId: string, appId: number, state: State): Observable<Application> {
    const body = {
      'state': state
    };
    return this.http.put<Application>(`${environment.resourceServer}/users/${userId}/applications/${appId}`, body);
  }

  applyForProject(userId: string, projectId: string, comment: string): Observable<Application> {
    const body = {
      'projectId': projectId,
      'comment': comment
    };
    return this.http.post<Application>(
      `${environment.resourceServer}/users/${userId}/applications`,
      body
    );
  }

  getApplicationsForEmployeesOfUser(userId: string): Observable<Application[]> {
    return this.http.get<Application[]>(`${environment.resourceServer}/users/${userId}/staff/applications`);
  }

  markApplicationAsRead(userId: string, applicationId: number): Observable<Application> {
    return this.http.post<Application>(
      `${environment.resourceServer}/users/${userId}/staff/applications/${applicationId}`,
      null
    );
  }

  setEmployeeAccessInfo(userId: string, accessEnd): Observable<Employee> {
    const body = {
      'accessEnd': accessEnd
    };
    return this.http.post<Employee>(
      `${environment.resourceServer}/users/${userId}/access`,
      body
    );
  }

  deleteEmployeeAccessInfo(userId: string): Observable<any> {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/access`);
  }

  getBookmarks(userId: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${environment.resourceServer}/users/${userId}/bookmarks`);
  }

  addBookmark(userId: string, projectId: string): Observable<Project> {
    const body = {
      'projectId': projectId
    };
    return this.http.post<Project>(
      `${environment.resourceServer}/users/${userId}/bookmarks`,
      body
    );
  }

  removeBookmark(userId: string, projectId: string): Observable<any> {
    return this.http.delete(`${environment.resourceServer}/users/${userId}/bookmarks/${projectId}`);
  }

  isApplicable(applications: Application[], projectId: string): boolean {
    return applications ? !applications.some(a => a && a.project.id === projectId) : true;
  }
}

export interface Employee {
  id: string;
  duration: number;
  firstName: string;
  lastName: string;
  picture: string;
  email: string;
  boss: boolean;
  applications: number;
  bookmarks: number;
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
  state: State;
}

export enum State {
  NONE = "NONE",
  NEW = "NEW",
  DELETED = "DELETED",
  OFFERED = "OFFERED",
}
