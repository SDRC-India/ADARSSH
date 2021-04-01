import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { CmsRoutingModule } from './cms-routing.module';
import { CmsViewComponent } from './cms-view/cms-view.component';
import { CmsSideMenuComponent } from './cms-side-menu/cms-side-menu.component';
import { MatChipsModule, MatFormFieldModule, MatButtonModule, MatIconModule, MatCardModule, MatCheckboxModule, MatInputModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule, MatRadioModule, MatListModule, MatTooltipModule, MatDialogModule, MAT_DATE_LOCALE, MAT_DATE_FORMATS, NativeDateAdapter, DateAdapter, MatSidenavModule } from '@angular/material';
import { ReactiveFormsModule,FormsModule } from '@angular/forms';
import { OptionFilterPipe } from './option-filter.pipe';
import { SafePipe } from './safe.pipe';
import { SearchPipePipe } from './search-pipe.pipe';
import { TableModule } from 'review-lib/public_api';
import { CKEditorModule } from 'ckeditor4-angular';
import { InformationDialogComponent } from './information-dialog/information-dialog.component';
import { NgxEditorModule } from 'ngx-editor';

// export const DateFormat = {
//   parse: {
//     dateInput: 'input',
//   },
//   display: {
//     dateInput: 'DD-MM-YYYY',
//     monthYearLabel: 'MMMM YYYY',
//     dateA11yLabel: 'MM/DD/YYYY',
//     monthYearA11yLabel: 'MMMM YYYY',
//   }
// };

@NgModule({
  entryComponents:[InformationDialogComponent],
  declarations: [CmsViewComponent, CmsSideMenuComponent, OptionFilterPipe, SafePipe, SearchPipePipe, InformationDialogComponent],
  imports: [
    MatChipsModule,
    CommonModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    CmsRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    MatCheckboxModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatRadioModule,
    MatListModule,
    MatTooltipModule,
    MatDialogModule,
    TableModule,
    CKEditorModule,
    MatSidenavModule,
    NgxEditorModule
  ], providers: [DatePipe,
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' }]
})
export class CmsModule { }


