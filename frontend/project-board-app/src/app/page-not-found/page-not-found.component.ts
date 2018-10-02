import { Component, OnInit } from '@angular/core';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.scss']
})
export class PageNotFoundComponent implements OnInit {
  faTimes = faTimes;
  message: string;

  constructor() { }

  ngOnInit() {
    this.message = 'Die angeforderte Ressource ist nicht verfügbar.';
  }
}
