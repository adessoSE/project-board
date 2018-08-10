import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminUiComponent } from './admin-ui/admin-ui.component';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { EmployeeService } from './employee.service';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { UserUiComponent } from './user-ui/user-ui.component';

@NgModule({
  declarations: [
    AppComponent,
    AdminUiComponent,
    PageNotFoundComponent,
    UserUiComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule.forRoot(),
    FontAwesomeModule,
    HttpClientModule
  ],
  providers: [
    EmployeeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
