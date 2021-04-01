import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DataEntryComponent } from './data-entry/data-entry.component';

const routes: Routes = [
{
  path: "",
  component: DataEntryComponent
}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DataEntryRoutingModule { }
