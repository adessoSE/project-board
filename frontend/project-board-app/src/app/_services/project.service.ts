import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable()
export class ProjectService {
  constructor(private http: HttpClient) { }

  getAllProjects() {
    return this.http.get<Project[]>(`${environment.resourceServer}/projects`);
  }

  getProjectWithID(projectId) {
    return this.http.get<Project>(`${environment.resourceServer}/projects/${projectId}`);
  }
}

export interface Project {
  id: number;
  effort: number;

  labels: string[];

  description: string;
  title: string;
  key: string;
  issuetype: string;
  job: string;
  lob: string;
  customer: string;
  location: string;
  operationStart: string;
  operationEnd: string;
  skills: string;
  status: string;
  elongation: string;
  freelancer: string;
  other: string;

  created: Date;
  updated: Date;
}
