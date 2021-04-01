import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { MobileFormComponent } from './mobile.form';
import { ComponentsModule } from '../../components/components.module';
import { ObjIteratePipe } from '../../pipes/obj-iterate.pipe';
import { SortItemMobilePipe } from '../../pipes/sort-item-mobile/sort-item-mobile';

@NgModule({
  declarations: [
    MobileFormComponent,
    ObjIteratePipe,
    SortItemMobilePipe
  ],
  imports: [
    IonicPageModule.forChild(MobileFormComponent),
    ComponentsModule
  ]
})
export class FromPageModule {}
