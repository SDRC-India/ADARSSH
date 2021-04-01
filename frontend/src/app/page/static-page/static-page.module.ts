import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StaticPageRoutingModule } from './static-page-routing.module';
import { AboutUsComponent } from './about-us/about-us.component';
import { ReportComponent } from './report/report.component';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { MatCheckboxModule, MatCardModule, MatTabsModule, MatRadioModule, MatInputModule, MatSelectModule, MatFormFieldModule, MatStepperModule, DateAdapter, MAT_DATE_FORMATS } from '@angular/material';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AreaFilterPipe } from '../pipes/area-filter.pipe';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { MatDatepickerModule} from '@angular/material/datepicker';
import { FacilityFilterPipe } from '../pipes/facility-filter.pipe';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ReviewSubmissionsComponent } from './review-submissions/review-submissions.component';
import { SubmissionDetailsComponent } from './submission-details/submission-details.component';
import { TableDataFilterPipe } from './filters/table-data-filter.pipe';
import { RemoveArrayPipe } from './filters/remove-array.pipe';
import { TableModule } from 'review-lib/public_api';
// import { TableModule } from 'lib-table2/public_api';
import { ShowHidePasswordModule } from 'ngx-show-hide-password';
import { TableAreasFilterPipe } from './filters/table-areas-filter.pipe';
import { NgxPaginationModule } from 'ngx-pagination';
import { MomentDateAdapter, MAT_MOMENT_DATE_FORMATS } from '@angular/material-moment-adapter';
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
import { DisclaimerComponent } from './disclaimer/disclaimer.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { SitemapComponent } from './sitemap/sitemap.component';
import { UserManualComponent } from './user-manual/user-manual.component';
import { PhotoComponent } from './photo/photo.component';
import { VideoComponent } from './video/video.component';
import { NgxImageGalleryModule } from 'ngx-image-gallery';
import { SearchTextPipe } from './search-text.pipe';
import { VideoUrlPipe } from './video-url.pipe';
import { ToolsComponent } from './tools/tools.component';

@NgModule({
  imports: [
    CommonModule,
    StaticPageRoutingModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatRadioModule,
    MatTabsModule,
    MatCardModule,
    MatCheckboxModule,
    NgxMatSelectSearchModule,
    MatDatepickerModule,
    TableModule,
    NgxPaginationModule,
    ShowHidePasswordModule,
    NgxImageGalleryModule
  ],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter},
    {provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS},
],
  declarations: [AboutUsComponent,ReportComponent,AreaFilterPipe, ChangePasswordComponent, FacilityFilterPipe, ForgotPasswordComponent, ReviewSubmissionsComponent, SubmissionDetailsComponent, TableDataFilterPipe, RemoveArrayPipe, TableAreasFilterPipe, TermsOfUseComponent, DisclaimerComponent, PrivacyPolicyComponent, SitemapComponent, UserManualComponent, PhotoComponent, VideoComponent, SearchTextPipe, VideoUrlPipe, ToolsComponent]
})
export class StaticPageModule { }
