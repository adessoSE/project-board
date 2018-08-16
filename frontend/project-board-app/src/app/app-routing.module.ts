import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './_guards/auth.guard';
import { AdminUiComponent } from './admin-ui/admin-ui.component';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { EmployeeResolverService } from './_services/employee-resolver.service';
import { ProjectResolverService } from './_services/project-resolver.service';
import { UserUiComponent } from './user-ui/user-ui.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'admin/:id',
    component: AdminUiComponent,
    resolve: {
      employees: EmployeeResolverService
    },
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    component: AdminUiComponent,
    resolve: {
      employees: EmployeeResolverService
    },
    canActivate: [AuthGuard]
  },
  {
    path: 'projects/:id',
    component: UserUiComponent,
    resolve: {
      projects: ProjectResolverService
    },
    canActivate: [AuthGuard]
  },
  {
    path: 'projects',
    component: UserUiComponent,
    resolve: {
      projects: ProjectResolverService
    },
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: 'projects',
    pathMatch: 'full'
  },
  {
    path: '**',
    component: PageNotFoundComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [ProjectResolverService, EmployeeResolverService]
})
export class AppRoutingModule {
}
