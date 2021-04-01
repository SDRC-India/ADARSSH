import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CmsViewComponent } from './cms-view/cms-view.component';

const routes: Routes = [
  { path: '', component: CmsViewComponent, data: { title: 'CMS' }, }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CmsRoutingModule { }
