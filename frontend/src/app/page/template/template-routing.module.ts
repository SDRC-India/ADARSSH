import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TemplateDownloadUploadComponent } from './template-download-upload/template-download-upload.component';
import { RoleGuardGuard } from 'src/app/guard/role-guard.guard';

const routes: Routes = [
  {
    path: '',
    component: TemplateDownloadUploadComponent,
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
export class TemplateRoutingModule { }
