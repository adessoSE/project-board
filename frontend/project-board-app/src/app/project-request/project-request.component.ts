import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';
import { EmployeeService } from '../_services/employee.service';
import { Project } from '../_services/project.service';

@Component({
  selector: 'app-project-request',
  templateUrl: './project-request.component.html',
  styleUrls: ['./project-request.component.scss']
})
export class ProjectRequestComponent implements OnInit {
  project: Project;
  comment: string;

  destroy$ = new Subject<void>();

  constructor(private router: Router,
              private route: ActivatedRoute,
              private employeeService: EmployeeService,
              private authService: AuthenticationService,
              private alertService: AlertService) { }

  ngOnInit(): void {
    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe((data: { project: Project }) => {
        this.project = data.project;
      });
  }

  requestProject(): void {
    this.employeeService.applyForProject(this.authService.username, this.project.id, this.comment)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
          this.alertService.success(`Du hast das Projekt mit dem Schlüssel "${this.project.id}" erfolgreich angefragt.`, true);
          this.router.navigate([`/browse/${this.project.id}`]);
        },
        () => {
          this.alertService.error('Es gab einen Fehler. Das Projekt konnte nicht angefragt werden.');
        }
      );
  }
}
