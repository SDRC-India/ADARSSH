import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TemplateRoutingModule } from './template-routing.module';
import { TemplateDownloadUploadComponent } from './template-download-upload/template-download-upload.component';
import { MatStepperModule, MatFormFieldModule, MatSelectModule, MatInputModule, MatRadioModule, MatTabsModule, MatCardModule, MatCheckboxModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonServiceService } from '../analytics/service/api/common-service.service';

@NgModule({
  imports: [
    CommonModule,
    TemplateRoutingModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatRadioModule,
    MatTabsModule,
    MatCardModule,
    MatCheckboxModule,
  ],
  declarations: [TemplateDownloadUploadComponent],
  providers: [CommonServiceService]
})
export class TemplateModule { }
