import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class EmployeeService {

  constructor(private http: HttpClient) { }

  getEmployees() {
    return this.http.get<Employee[]>('./assets/employees.json');
  }
}

export interface Employee {
  id: number;
  enabled: boolean;
  duration: number;
  name: {
    first: string,
    last: string
  },
  fullName, email: string;
}
