import { MatCheckboxModule } from "@angular/material/checkbox";
import { CommonServiceService } from "./service/api/common-service.service";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { AnalyticsRoutingModule } from "./analytics-routing.module";
import { AnalyticsHomeComponent } from "./analytics-home/analytics-home.component";
import { DescriptiveStatisticsComponent } from "./descriptive-statistics/descriptive-statistics.component";
import { MatStepperModule } from "@angular/material/stepper";

import { OutlayerComponent } from "./outlayer/outlayer.component";
import { MissingValuesComponent } from "./missing-values/missing-values.component";
import { RegrationComponent } from "./regration/regration.component";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatRadioModule } from "@angular/material/radio";
import { MatTabsModule } from "@angular/material/tabs";
import { MatCardModule } from "@angular/material/card";
import { ColumnPipePipe } from './pipes/column-pipe.pipe';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';

@NgModule({
  imports: [
    CommonModule,
    AnalyticsRoutingModule,
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
    NgxMatSelectSearchModule
  ],
  declarations: [
    AnalyticsHomeComponent,
    DescriptiveStatisticsComponent,
    OutlayerComponent,
    MissingValuesComponent,
    RegrationComponent,
    ColumnPipePipe
  ],
  providers: [CommonServiceService]
})
export class AnalyticsModule {}
