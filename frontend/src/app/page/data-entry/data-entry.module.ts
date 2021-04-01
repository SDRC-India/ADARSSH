import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DataEntryRoutingModule } from './data-entry-routing.module';
import { DataEntryComponent } from './data-entry/data-entry.component';

@NgModule({
  imports: [
    CommonModule,
    DataEntryRoutingModule
  ],
  declarations: [DataEntryComponent]
})
export class DataEntryModule { }
