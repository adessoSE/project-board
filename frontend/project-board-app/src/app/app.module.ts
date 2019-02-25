import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import localeDe from '@angular/common/locales/de';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule, HAMMER_GESTURE_CONFIG, HammerGestureConfig } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OAuthModule } from 'angular-oauth2-oidc';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { AlertComponent } from './_directives/alert/alert.component';
import { AuthGuard } from './_guards/auth.guard';
import { TokenInterceptor } from './_helpers/token.interceptor';
import { AlertService } from './_services/alert.service';
import { AuthenticationService } from './_services/authentication.service';
import { EmployeeService } from './_services/employee.service';
import { ProjectService } from './_services/project.service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowseProjectsComponent } from './browse-projects/browse-projects.component';
import { DatepickerHeaderComponent } from './datepicker-header/datepicker-header.component';
import { EmployeeDialogComponent } from './employee-dialog/employee-dialog.component';
import { ErrorComponent } from './error/error.component';
import { ExecutivesComponent } from './executives/executives.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { MaterialModule } from './material.module';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProfileComponent } from './profile/profile.component';
import { ProjectDetailsComponent } from './project-details/project-details.component';
import { ProjectDialogComponent } from './project-dialog/project-dialog.component';
import { ProjectRequestComponent } from './project-request/project-request.component';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { SearchComponent } from './search/search.component';
import { FaqComponent } from './faq/faq.component';

declare var Hammer: any;

export class MyHammerConfig extends HammerGestureConfig {
  buildHammer(element: HTMLElement): any {
    return new Hammer(element, {
      touchAction: 'pan-y'
    });
  }
}

registerLocaleData(localeDe, 'de');

@NgModule({
  declarations: [
    AppComponent,
    ExecutivesComponent,
    PageNotFoundComponent,
    ErrorComponent,
    BrowseProjectsComponent,
    LoginComponent,
    AlertComponent,
    ProjectDetailsComponent,
    ProjectDialogComponent,
    EmployeeDialogComponent,
    ProjectRequestComponent,
    ProfileComponent,
    LogoutComponent,
    DatepickerHeaderComponent,
    SearchComponent,
    FaqComponent
  ],
  imports: [
    NgxMatSelectSearchModule,
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
  entryComponents: [
    ProjectDialogComponent,
    EmployeeDialogComponent,
    DatepickerHeaderComponent
  ],
  providers: [
    {provide: LOCALE_ID, useValue: 'de'},
    AlertService,
    AuthGuard,
    AuthenticationService,
    EmployeeService,
    {
      provide: HAMMER_GESTURE_CONFIG,
      useClass: MyHammerConfig
    },
    ProjectService,
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
