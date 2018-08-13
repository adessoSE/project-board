import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminUiComponent } from './admin-ui/admin-ui.component';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { EmployeeService } from './services/employee.service';
import { ProjectResolverService } from './services/project-resolver.service';
import { ProjectService } from './services/project.service';
import { UserUiComponent } from './user-ui/user-ui.component';
import { LoginComponent } from './login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    AdminUiComponent,
    PageNotFoundComponent,
    UserUiComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule.forRoot(),
    FontAwesomeModule,
    HttpClientModule
  ],
  providers: [
    EmployeeService,
    ProjectService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
