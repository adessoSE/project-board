import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminUiComponent } from './admin-ui/admin-ui.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { EmployeeResolverService } from './services/employee-resolver.service';
import { ProjectResolverService } from './services/project-resolver.service';
import { UserUiComponent } from './user-ui/user-ui.component';

const routes: Routes = [
  {
    path: 'admin/:id',
    component: AdminUiComponent,
    resolve: {
      employees: EmployeeResolverService
    }
  },
  {
    path: 'admin',
    component: AdminUiComponent,
    resolve: {
      employees: EmployeeResolverService
    }
  },
  {
    path: 'projects/:id',
    component: UserUiComponent,
    resolve: {
      projects: ProjectResolverService
    }
  },
  {
    path: 'projects',
    component: UserUiComponent,
    resolve: {
      projects: ProjectResolverService
    }
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
