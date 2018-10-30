import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { Subject } from 'rxjs';
import { first, takeUntil } from 'rxjs/operators';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;

  destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private oAuthService: OAuthService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService) {
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    if (this.oAuthService.hasValidAccessToken()) {
      this.router.navigate(['/browse']);
      this.alertService.info('Du bist bereits eingeloggt! Wenn du den Benutzer wechseln möchtest, logge dich erst aus.');
    }
    // else if (this.authenticationService.token && !this.oAuthService.hasValidAccessToken()){
    //   this.alertService.error('Deine Sitzung ist abgelaufen.');
    // }

    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  // convenience getter for easy access to form fields
  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authenticationService.login(this.f.username.value, this.f.password.value)
      .pipe(takeUntil(this.destroy$))
      .pipe(first())
      .subscribe(
        () => this.router.navigate([this.returnUrl]),
        error => {
          if (error.status === 401) {
            this.alertService.error('Benutzername oder Passwort sind falsch');
          } else {
            this.alertService.error('Fehler beim Login');
          }
          this.loading = false;
        });
  }
}
