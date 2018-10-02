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

  createProject(project) {
    return this.http.post<Project>(`${environment.resourceServer}/projects`, project);
  }

  updateProject(project) {
    return this.http.put<Project>(`${environment.resourceServer}/projects/${project.id}`, project);
  }

  deleteProject(projectId) {
    return this.http.delete(`${environment.resourceServer}/projects/${projectId}`);
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

  created: Date;
  updated: Date;
}
