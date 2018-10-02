import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccessGuard } from './_guards/access.guard';
import { AdminGuard } from './_guards/admin.guard';
import { AlreadyAppliedGuard } from './_guards/already-applied.guard';
import { AuthGuard } from './_guards/auth.guard';
import { EditableGuard } from './_guards/editable.guard';
import { ApplicationsResolverService } from './_services/applications-resolver.service';
import { BookmarksResolverService } from './_services/bookmarks-resolver.service';
import { CreatedProjectsResolverService } from './_services/created-projects-resolver.service';
import { EmployeeResolverService } from './_services/employee-resolver.service';
import { ProjectResolverService } from './_services/project-resolver.service';
import { ProjectsResolverService } from './_services/projects-resolver.service';
import { UserResolverService } from './_services/user-resolver.service';
import { AdminUiComponent } from './admin-ui/admin-ui.component';
import { LogoutComponent } from './logout/logout.component';
import { OverviewComponent } from './overview/overview.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProjectRequestComponent } from './project-request/project-request.component';
import { ProjectComponent } from './project/project.component';
import { UserUiComponent } from './user-ui/user-ui.component';

const routes: Routes = [
  {
    path: 'logout',
    component: LogoutComponent
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
    component: ProjectComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'projects/:id/edit',
    component: ProjectComponent,
    resolve: {
      project: ProjectResolverService
    },
    canActivate: [AuthGuard, EditableGuard]
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
    path: 'projects/:id',
    component: UserUiComponent,
    resolve: {
      projects: ProjectsResolverService,
      applications: ApplicationsResolverService,
      bookmarks: BookmarksResolverService
    },
    canActivate: [AuthGuard, AccessGuard]
  },
  {
    path: 'projects',
    component: UserUiComponent,
    resolve: {
      projects: ProjectsResolverService,
      applications: ApplicationsResolverService,
      bookmarks: BookmarksResolverService
    },
    canActivate: [AuthGuard, AccessGuard]
  },
  {
    path: 'overview',
    component: OverviewComponent,
    resolve: {
      user: UserResolverService,
      bookmarks: BookmarksResolverService,
      applications: ApplicationsResolverService,
      projects: CreatedProjectsResolverService
    },
    canActivate: [AuthGuard]
  },
  {
    path: 'notFound',
    component: PageNotFoundComponent
  },
  {
    path: '',
    redirectTo: 'projects',
    pathMatch: 'full'
  },
  {
    path: '**',
    component: UserUiComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [ProjectsResolverService, EmployeeResolverService]
})
export class AppRoutingModule {
}
