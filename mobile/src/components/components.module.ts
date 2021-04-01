import { NgModule } from '@angular/core';
import { WebFormComponent } from './web/web.form';
import { CommonModule } from '@angular/common';
import { IonicModule } from 'ionic-angular';
import { RemoveExtraKeysPipe } from '../pipes/remove-extra-keys';
import { MyDatePickerModule } from 'mydatepicker';
import { AmazingTimePickerModule } from 'amazing-time-picker';
import { MatDatepickerModule, MatFormFieldModule, MatNativeDateModule, MatInputModule } from '@angular/material';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import { SortItemPipe } from '../pipes/sort-item/sort-item';
// import {MomentDateAdapter} from '@angular/material-moment-adapter';

export const MY_FORMATS = {
    parse: {
      dateInput: 'MM/YYYY',
    },
    display: {
      dateInput: 'MM/YYYY',
      monthYearLabel: 'MMM YYYY',
      dateA11yLabel: 'LL',
      monthYearA11yLabel: 'MMMM YYYY',
    },
  };


@NgModule({
	declarations: [
        WebFormComponent,
        RemoveExtraKeysPipe,
        SortItemPipe
    ],
	imports: [
        CommonModule,
        IonicModule,
        MyDatePickerModule,
        AmazingTimePickerModule,
        MatDatepickerModule,
        MatFormFieldModule,
        MatNativeDateModule,
        MatInputModule
    ],
	exports: [
        WebFormComponent],
        // providers: [
        //     // `MomentDateAdapter` can be automatically provided by importing `MomentDateModule` in your
        //     // application's root module. We provide it at the component level here, due to limitations of
        //     // our example generation script.
        //     {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},

        //     {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
        //   ]


})

export class ComponentsModule {}

