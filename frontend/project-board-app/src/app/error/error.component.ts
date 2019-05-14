import {Component, OnInit} from '@angular/core';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {
  message: string;
  supportEmail: string;

  constructor() { }

  ngOnInit(): void {
    this.message = 'Da ist etwas schief gelaufen. Sollte der Fehler nicht zu beheben sein, kontaktiere den project board Support.';
    this.supportEmail = environment.supportEmail;
  }
}
