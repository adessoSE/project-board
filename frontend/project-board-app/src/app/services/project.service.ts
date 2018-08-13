import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class ProjectService {
  url = 'https://jira.adesso.de/rest/api/2/search?jql=project=STF&issuetype=Staffinganfrage&status=eskaliert';

  constructor(private http: HttpClient) { }

  getProjects() {
    return this.http.get<Project[]>('./assets/projects.json');
  }
}

export interface Project {
  id, type, job, lob, client, location, begin, end, elongation: string;
  tags, skills: string[];
  effort: number;
}
