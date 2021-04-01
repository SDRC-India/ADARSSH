import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './page/home/home.component';
import { LoginComponent } from './login/login.component';
import { Exception404Component } from './exception404/exception404.component';
import { LoggedinGuard } from './guard/loggedin.guard';
import { AnalyticsHomeComponent } from './page/analytics/analytics-home/analytics-home.component';
import { RoleGuardGuard } from './guard/role-guard.guard';

const routes: Routes = [
{
  path: '',
  component: HomeComponent,
  pathMatch: 'full'
},
{
  path: 'data-upload',
  loadChildren: './page/template/template.module#TemplateModule',
  pathMatch: 'full'
},
{
  path: 'login',
  component: LoginComponent,
  pathMatch: 'full',
  canActivate: [LoggedinGuard]
},
{
  path: 'error',
  component: Exception404Component,
  pathMatch: 'full'
},
{

  path: 'exception',
  component: Exception404Component,
  pathMatch: 'full'
},
{
  path: 'cms',
  loadChildren: './page/cms-new/cms.module#CmsModule',
   canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
 
},
{
   path: 'analytic',
   loadChildren: './page/analytics/analytics.module#AnalyticsModule'
},
{
  path: 'dashboard',
  loadChildren: './dashboard/dashboard.module#DashboardModule',
},
{
  path: 'user',
  loadChildren: './user-management/user-management.module#UserManagementModule',
},
{
  path: 'data-entry',
  loadChildren: './page/data-entry/data-entry.module#DataEntryModule',
  // canActivate: [RoleGuardGuard],
},
{
  path: 'pages',
  loadChildren: './page/static-page/static-page.module#StaticPageModule',
},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    scrollPositionRestoration: 'enabled',useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
