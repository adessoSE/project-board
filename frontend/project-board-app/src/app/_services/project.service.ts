import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class ProjectService {
  url = 'https://jira.adesso.de/rest/api/2/search?jql=project=STF&issuetype=Staffinganfrage&status=eskaliert';

  constructor(private http: HttpClient, private authService: AuthenticationService) { }

  getProjects() {
    return this.http.get<Project[]>('http://localhost:8081/projects'/*, httpOptions*/);
  }

  getProjectWithID(id) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Accept': 'application/json',
        'Authorization': 'Bearer ' + this.authService.token
      })
    };
    return this.http.get<Project>(`http://localhost:8081/projects/${id}`, httpOptions);
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
