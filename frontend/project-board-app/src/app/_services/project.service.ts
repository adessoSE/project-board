import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable()
export class ProjectService {
  constructor(private http: HttpClient) { }

  getAllProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${environment.resourceServer}/projects`);
  }

  getProjectWithID(projectId): Observable<Project> {
    return this.http.get<Project>(`${environment.resourceServer}/projects/${projectId}`);
  }

  search(query: string): Observable<Project[]> {
    query = encodeURI(query.replace('&', ' '));
    return this.http.get<Project[]>(`${environment.resourceServer}/projects/search?query=${query}`);
  }

  isBookmarked(bookmarks: Project[], projectId: string): boolean {
    return bookmarks ? bookmarks.some(p => p && p.id === projectId) : false;
  }
}

export interface Project {
  labels: string[];
  customer: string;
  description: string;
  effort: string;
  elongation: string;
  freelancer: string;
  id: string;
  issuetype: string;
  job: string;
  lob: string;
  location: string;
  operationEnd: string;
  operationStart: string;
  other: string;
  skills: string;
  status: string;
  title: string;
  dailyRate: string;
  travelCostsCompensated: string;
  created: Date;
  updated: Date;
}
