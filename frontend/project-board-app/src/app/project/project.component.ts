import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { Project, ProjectService } from '../_services/project.service';

import {MAT_DIALOG_DATA} from '@angular/material';
import { Inject } from '@angular/core';

import {FormControl} from '@angular/forms';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import { MatDialogRef} from '@angular/material';
import * as _moment from 'moment';
import 'moment/locale/de';

const moment = _moment;

export const MY_FORMATS = {
  parse: {
    dateInput: 'DD.MM.YYYY',
  },
  display: {
    dateInput: 'DD.MM.YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'DD.MM.YYYY',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ],
})
export class ProjectComponent implements OnInit {
  project: Project;
  date = new FormControl(moment());
  labelsInput = '';
  form: FormGroup;
  submitted = false;
  edit = false;
  navigateOnSubmit = false;

  destroy$ = new Subject<void>();

  constructor(private projectService: ProjectService,
              private alertService: AlertService,
              private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private dialogRef: MatDialogRef<ProjectComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

myFilter = (d: Date): boolean => {
  var currentDate = new Date();
  return ((d.getDate() >= currentDate.getDate()) && (d.getMonth() >= currentDate.getMonth()) && (d.getFullYear() === currentDate.getFullYear())) || (d.getFullYear() > currentDate.getFullYear());
}            

  ngOnInit() {
    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.resetProject();
        if (data.project) {
          this.project = data.project;
          this.edit = true;
        }
        this.form = this.formBuilder.group({
          title: [this.project.title, Validators.required],
          status: [this.project.status, Validators.required],
          description: [this.project.description, Validators.required],
          lob: [this.project.lob, Validators.required],
          issuetype: [this.project.issuetype, Validators.required]
        });

        if (this.project.labels.length > 0) {
          for (let i = 0; i < this.project.labels.length - 1; i++) {
            this.labelsInput += this.project.labels[i] + ', ';
          }
          this.labelsInput += this.project.labels[this.project.labels.length - 1];
        }
      });
  }

  onSubmit() {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.project.title = this.f.title.value;
    this.project.issuetype = this.f.issuetype.value;
    this.project.status = this.f.status.value;
    this.project.lob = this.f.lob.value;
    this.project.description = this.f.description.value;
    this.project.labels = this.stringToArray(this.labelsInput, ',');
    this.project.skills = this.stringToArray(this.project.skills, ',').join(', ');
    if (this.edit) {
      this.projectService.updateProject(this.project)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.alertService.success('Änderungen wurden gespeichert.', true);
        });
      this.dialogRef.close();
    } else {
      this.projectService.createProject(this.project)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.alertService.success('Projekt erfolgreich erstellt.', !this.navigateOnSubmit);
          if (!this.navigateOnSubmit) {
          } else {
            this.submitted = false;
            this.resetProject();
            document.body.scrollTop = 0;
            document.documentElement.scrollTop = 0;
          }
        });
      this.dialogRef.close();
    }
  }

  get f() {
    return this.form.controls;
  }

  stringToArray(string: string, separator: string | RegExp): string[] {
    return string
      .split(separator)
      .map(str => {
        return str
          .replace(/^\s+/, '')
          .replace(/\s+$/, '')
          .replace(/\s{2,}/, ' ')
          .split(' ')
          .map(token => token.charAt(0).toUpperCase() + token.slice(1).toLowerCase())
          .join(' ');
      });
  }

  resetProject() {
    this.project = {
      customer: '',
      description: '',
      effort: '',
      elongation: '',
      freelancer: '',
      id: '',
      issuetype: '',
      job: '',
      lob: '',
      labels: [],
      location: '',
      operationEnd: '',
      operationStart: '',
      other: '',
      skills: '',
      status: '',
      title: '',
      created: null,
      updated: null
    };
    this.form = this.formBuilder.group({
      title: ['', Validators.required],
      status: ['', Validators.required],
      description: ['', Validators.required],
      lob: ['', Validators.required],
      issuetype: ['', Validators.required]
    });
  }
}