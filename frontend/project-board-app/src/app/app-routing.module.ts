import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccessGuard } from './_guards/access.guard';
import { AuthGuard } from './_guards/auth.guard';
import { IsBossGuard } from './_guards/is-boss.guard';
import { ApplicationsResolverService } from './_resolver/applications-resolver.service';
import { BookmarksResolverService } from './_resolver/bookmarks-resolver.service';
import { EmployeeResolverService } from './_resolver/employee-resolver.service';
import { IsBossResolverService } from './_resolver/is-boss-resolver.service';
import { ProjectsResolverService } from './_resolver/projects-resolver.service';
import { UserResolverService } from './_resolver/user-resolver.service';
import { BrowseProjectsComponent } from './browse-projects/browse-projects.component';
import { ErrorComponent } from './error/error.component';
import { ExecutivesComponent } from './executives/executives.component';
import { LogoutComponent } from './logout/logout.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProfileComponent } from './profile/profile.component';

const routes: Routes = [
  {
    path: 'logout',
    component: LogoutComponent
  },
  {
    path: 'employees/:id',
    component: ExecutivesComponent,
    resolve: {
      employees: EmployeeResolverService
    },
    canActivate: [
      AuthGuard,
      IsBossGuard
    ]
  },
  {
    path: 'employees',
    component: ExecutivesComponent,
    resolve: {
      employees: EmployeeResolverService
    },
    canActivate: [
      AuthGuard,
      IsBossGuard
    ]
  },
  {
    path: 'browse/:id',
    component: BrowseProjectsComponent,
    resolve: {
      projects: ProjectsResolverService,
      applications: ApplicationsResolverService,
      bookmarks: BookmarksResolverService,
      isUserBoss: IsBossResolverService
    },
    canActivate: [
      AuthGuard,
      AccessGuard
    ]
  },
  {
    path: 'browse',
    component: BrowseProjectsComponent,
    resolve: {
      projects: ProjectsResolverService,
      applications: ApplicationsResolverService,
      bookmarks: BookmarksResolverService,
      isUserBoss: IsBossResolverService
    },
    canActivate: [
      AuthGuard,
      AccessGuard
    ]
  },
  {
    path: 'profile',
    component: ProfileComponent,
    resolve: {
      user: UserResolverService,
      bookmarks: BookmarksResolverService,
      applications: ApplicationsResolverService
    },
    canActivate: [
      AuthGuard
    ]
  },
  {
    path: 'notFound',
    component: PageNotFoundComponent
  },
  {
    path: 'error',
    component: ErrorComponent
  },
  {
    path: '',
    redirectTo: 'browse',
    pathMatch: 'full'
  },
  {
    path: '**',
    component: BrowseProjectsComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {
}
