import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import localeDe from '@angular/common/locales/de';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OAuthModule } from 'angular-oauth2-oidc';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { AlertComponent } from './_directives/alert/alert.component';
import { AuthGuard } from './_guards/auth.guard';
import { TokenInterceptor } from './_helpers/token.interceptor';
import { YesOrNoPipe } from './_pipes/yes-or-no.pipe';
import { AlertService } from './_services/alert.service';
import { AuthenticationService } from './_services/authentication.service';
import { EmployeeService } from './_services/employee.service';
import { ProjectService } from './_services/project.service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowseProjectsComponent } from './browse-projects/browse-projects.component';
import { EmployeeManagementComponent } from './employee-management/employee-management.component';
import { ExecutivesComponent } from './executives/executives.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { OverviewComponent } from './overview/overview.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProjectDetailsComponent } from './project-details/project-details.component';
import { ProjectRequestComponent } from './project-request/project-request.component';
import { ProjectComponent } from './project/project.component';
import { MaterialModule } from './material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

registerLocaleData(localeDe, 'de');

@NgModule({
  declarations: [
    AppComponent,
    ExecutivesComponent,
    PageNotFoundComponent,
    BrowseProjectsComponent,
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
    OAuthModule.forRoot(),
    InfiniteScrollModule,
    MaterialModule,
    BrowserAnimationsModule
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
