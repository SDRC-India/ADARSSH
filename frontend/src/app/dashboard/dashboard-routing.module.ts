import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CoverageDashboardComponent } from './coverage-dashboard/coverage-dashboard.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';
import { SnapshotViewComponent } from './snapshot-view/snapshot-view.component';
import { AggregateLegacyComponent } from './aggregate-legacy/aggregate-legacy.component';

const routes: Routes = [
  {
    path:"coverage-dashboard",
    component: CoverageDashboardComponent,
    pathMatch: "full",
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["Visualization","Data Entry & Visualization"]
        }
  },
  {
    path:"snapshot-view",
    component: SnapshotViewComponent,
    pathMatch: "full",
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["Visualization","Data Entry & Visualization"]
        }
  },
  {
    path:"aggregate-legacy",
    component: AggregateLegacyComponent,
    pathMatch: "full",
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
