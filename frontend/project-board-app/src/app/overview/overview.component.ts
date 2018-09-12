import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit {
  user = {
    'id': 'jacobs',
    'enabled': true,
    'duration': 11,
    'name': {
      'first': 'Lottie',
      'last': 'Jacobs'
    },
    'fullName': 'Lottie Jacobs',
    'email': 'lottie.jacobs@adesso.de'
  };

  constructor(private employeeService: EmployeeService,
              private authService: AuthenticationService) { }

  ngOnInit() {
    // TODO: receive data about logged in user from backend
    // this.employeeService.getEmployeeWithId(this.authService.username).subscribe(user => this.user = user);
  }
}
