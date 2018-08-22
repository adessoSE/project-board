import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';

@Injectable()
export class ProjectService {
  url = 'https://jira.adesso.de/rest/api/2/search?jql=project=STF&issuetype=Staffinganfrage&status=eskaliert';

  constructor(private http: HttpClient) { }

  getProjects() {
    return this.http.get<Project[]>('./assets/real_projects.json');
  }

  getProjectWithID(key) {
    return this.http.get('./assets/real_projects.json').pipe(map((res: Project[]) => res.find((p) => p.key == key)));
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
