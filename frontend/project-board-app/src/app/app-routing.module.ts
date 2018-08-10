import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminUiComponent } from './admin-ui/admin-ui.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { UserUiComponent } from './user-ui/user-ui.component';

const routes: Routes = [
  {
    path: 'admin',
    component: AdminUiComponent,
    pathMatch: 'full'
  },
  {
    path: 'user',
    component: UserUiComponent,
    pathMatch: 'full'
  },
  {
    path: '',
    redirectTo: 'admin',
    pathMatch: 'full'
  },
  {
    path: '**',
    component: PageNotFoundComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
