import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-user-ui',
  templateUrl: './user-ui.component.html',
  styleUrls: ['./user-ui.component.css']
})
export class UserUiComponent implements OnInit {
  projects = [
    {
      title: 'Projekt 1'
    },
    {
      title: 'Projekt 2'
    },
    {
      title: 'Projekt 3'
    },
    {
      title: 'Projekt 4'
    },
    {
      title: 'Projekt 5'
    },
  ];
  selectedProject;

  constructor() { }

  ngOnInit() {
  }
}
