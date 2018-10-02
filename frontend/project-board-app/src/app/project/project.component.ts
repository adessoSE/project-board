import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Project, ProjectService } from '../_services/project.service';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit {
  project: Project = {
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

  labelsInput = '';
  form: FormGroup;
  submitted = false;
  edit = false;

  constructor(private projectService: ProjectService,
              private formBuilder: FormBuilder,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.data.subscribe(data => {
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
          this.labelsInput += this.project.labels[i] + ' ';
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
    this.project.labels = this.labelsInput.split(' ');
    if (this.project.created === null) {
      this.project.created = new Date();
    }
    this.project.updated = new Date();

    if (this.edit) {
      this.projectService.updateProject(this.project).subscribe();
    } else {
      this.projectService.createProject(this.project).subscribe();
    }
  }

  get f() {
    return this.form.controls;
  }
}
