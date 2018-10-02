import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import localeDe from '@angular/common/locales/de';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OAuthModule } from 'angular-oauth2-oidc';
import { AlertComponent } from './_directives/alert/alert.component';
import { AuthGuard } from './_guards/auth.guard';
import { TokenInterceptor } from './_helpers/token.interceptor';
import { YesOrNoPipe } from './_pipes/yes-or-no.pipe';
import { AlertService } from './_services/alert.service';
import { AuthenticationService } from './_services/authentication.service';
import { EmployeeService } from './_services/employee.service';
import { ProjectService } from './_services/project.service';
import { AdminUiComponent } from './admin-ui/admin-ui.component';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { EmployeeManagementComponent } from './employee-management/employee-management.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { OverviewComponent } from './overview/overview.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProjectDetailsComponent } from './project-details/project-details.component';
import { ProjectRequestComponent } from './project-request/project-request.component';
import { ProjectComponent } from './project/project.component';
import { UserUiComponent } from './user-ui/user-ui.component';

registerLocaleData(localeDe, 'de');

@NgModule({
  declarations: [
    AppComponent,
    AdminUiComponent,
    PageNotFoundComponent,
    UserUiComponent,
    LoginComponent,
    AlertComponent,
    YesOrNoPipe,
    ProjectDetailsComponent,
    EmployeeManagementComponent,
    ProjectRequestComponent,
    OverviewComponent,
    ProjectComponent,
    LogoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FontAwesomeModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    OAuthModule.forRoot()
  ],
  providers: [
    {provide: LOCALE_ID, useValue: 'de'},
    AlertService,
    AuthGuard,
    AuthenticationService,
    EmployeeService,
    ProjectService,
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
