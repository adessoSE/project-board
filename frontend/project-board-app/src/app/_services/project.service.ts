import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';

@Injectable()
export class ProjectService {
  url = 'https://jira.adesso.de/rest/api/2/search?jql=project=STF&issuetype=Staffinganfrage&status=eskaliert';

  constructor(private http: HttpClient) { }

  getProjects() {
    return this.http.get<Project[]>('./assets/projects.json');
  }

  getProjectWithID(id) {
    console.log('requested id: ', id);
    return this.http.get('./assets/projects.json').pipe(map((res: Project[]) => res.find((p) => p.id == id)));
  }
}

export interface Project {
  id,
  type,
  job,
  lob,
  client,
  location,
  begin,
  end,
  elongation: string;
  tags,
  skills: string[];
  effort: number;
}
