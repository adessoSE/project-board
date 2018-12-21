import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable()
export class ProjectService {
  constructor(private http: HttpClient) { }

  /* Nicht benutzen, daBackend damit nicht funktioniert
  getAllProjects() {
    return this.http.get<Project[]>(`${environment.resourceServer}/projects`);
  } */

  getAllProjectsPaginated(page: number, size: number) {
    return this.http.get<Page<Project>>(`${environment.resourceServer}/projects?page=${page}&size=${size}`);
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

  search(keyword) {
    return this.http.get<Project[]>(`${environment.resourceServer}/projects/search?keyword=${keyword}`);
  }

  searchPaginated(keyword: string, page: number, size: number) {
    return this.http.get<Page<Project>>(`${environment.resourceServer}/projects/search?keyword=${keyword}&page=${page}&size=${size}`);
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

export interface Page<T> {
  content: T[];
  pageable: {
    sort: {
      sorted: boolean,
      unsorted: boolean
    }
    offset: number,
    pageSize: number,
    pageNumber: number,
    paged: boolean,
    unpaged: boolean
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    sorted: boolean,
    unsorted: boolean
  };
  first: boolean;
  numberOfElements: number;
}
