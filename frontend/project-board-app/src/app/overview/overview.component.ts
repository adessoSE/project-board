import { Component, OnInit } from '@angular/core';
import { EmployeeService } from '../_services/employee.service';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit {
  user = {
    'id': 1,
    'enabled': true,
    'duration': 11,
    'name': {
      'first': 'Lottie',
      'last': 'Jacobs'
    },
    'fullName': 'Lottie Jacobs',
    'email': 'lottie.jacobs@adesso.de'
  };

  constructor(private employeeService: EmployeeService) { }

  ngOnInit() {
    // TODO: receive data about logged in user from backend
    // this.employeeService.getUserData().subscribe(user => this.user = user);
  }
}
