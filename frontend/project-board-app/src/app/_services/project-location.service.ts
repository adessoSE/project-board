import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Project } from './project.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectLocation {
  constructor(private http: HttpClient) { }

  getProjectsInRange(referenceLocation: string, range: number){
      return this.http.get<Project[]>(`${environment.resourceServer}/projects/${range}/${referenceLocation}`); //richtigen Endpoint hinzufügen (mit und ohne string übergabe)
  }
}