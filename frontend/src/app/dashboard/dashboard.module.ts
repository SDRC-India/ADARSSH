import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { CoverageDashboardComponent } from './coverage-dashboard/coverage-dashboard.component';
import { DashboardService } from './services/dashboard.service';
import { MatCardModule, MatListModule, MatNativeDateModule, MatDatepickerModule, MatRadioModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatTabsModule, MatCheckboxModule, MatSortModule, MatButtonModule, MatIconModule, MatTooltipModule } from '@angular/material';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';
import { AreaFilterPipe } from './filters/area-filter.pipe';
import { BarChartComponent } from './bar-chart/bar-chart.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { LineChartComponent } from './line-chart/line-chart.component';
import { StackedBarChartComponent } from './stacked-bar-chart/stacked-bar-chart.component';
import { SpiderChartComponent } from './spider-chart/spider-chart.component';
import { HorizonatlBarChartComponent } from './horizonatl-bar-chart/horizonatl-bar-chart.component';
import { SnapshotViewComponent } from './snapshot-view/snapshot-view.component';
import { BoxViewComponent } from './box-view/box-view.component';
import { GroupBarChartComponent } from './group-bar-chart/group-bar-chart.component';
import { ThreeDBarChartComponent } from './three-d-bar-chart/three-d-bar-chart.component';
import { CardViewComponent } from './card-view/card-view.component';
import { AggregateLegacyComponent } from './aggregate-legacy/aggregate-legacy.component';
import { TableModule } from 'review-lib/public_api';
import { TableChartComponent } from './table-chart/table-chart.component';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    DashboardRoutingModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatRadioModule,
    MatDatepickerModule,
    MatTooltipModule,
    TableModule
  ],
  declarations: [
    CoverageDashboardComponent,
    AreaFilterPipe,
    BarChartComponent,
    PieChartComponent,
    LineChartComponent,
    StackedBarChartComponent,
    SpiderChartComponent,
    HorizonatlBarChartComponent,
    SnapshotViewComponent,
    BoxViewComponent,
    GroupBarChartComponent,
    ThreeDBarChartComponent,
    CardViewComponent,
    AggregateLegacyComponent,
    TableChartComponent
    
  ],
  providers:[DashboardService]
})
export class DashboardModule { }
