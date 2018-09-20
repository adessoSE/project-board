import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccessGuard } from './_guards/access.guard';
import { AdminGuard } from './_guards/admin.guard';
import { AlreadyAppliedGuard } from './_guards/already-applied.guard';
import { AuthGuard } from './_guards/auth.guard';
import { ApplicationsResolverService } from './_services/applications-resolver.service';
import { BookmarksResolverService } from './_services/bookmarks-resolver.service';
import { EmployeeResolverService } from './_services/employee-resolver.service';
import { ProjectResolverService } from './_services/project-resolver.service';
import { ProjectsResolverService } from './_services/projects-resolver.service';
import { AdminUiComponent } from './admin-ui/admin-ui.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import { LoginComponent } from './login/login.component';
import { OverviewComponent } from './overview/overview.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProjectRequestComponent } from './project-request/project-request.component';
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
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'admin',
    component: AdminUiComponent,
    resolve: {
      employees: EmployeeResolverService
    },
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'projects/new',
    component: CreateProjectComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'projects/:id/request',
    component: ProjectRequestComponent,
    resolve: {
      project: ProjectResolverService
    },
    canActivate: [AuthGuard, AccessGuard, AlreadyAppliedGuard]
  },
  {
    path: 'projects/:key',
    component: UserUiComponent,
    resolve: {
      projects: ProjectsResolverService,
      appliedProjects: ApplicationsResolverService,
      bookmarks: BookmarksResolverService
    },
    canActivate: [AuthGuard, AccessGuard]
  },
  {
    path: 'projects',
    component: UserUiComponent,
    resolve: {
      projects: ProjectsResolverService,
      appliedProjects: ApplicationsResolverService,
      bookmarks: BookmarksResolverService
    },
    canActivate: [AuthGuard, AccessGuard]
  },
  {
    path: 'overview',
    component: OverviewComponent,
    resolve: {
      bookmarks: BookmarksResolverService,
      applications: ApplicationsResolverService
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
  providers: [ProjectsResolverService, EmployeeResolverService]
})
export class AppRoutingModule {
}
