import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AboutUsComponent } from './about-us/about-us.component';
import { ReportComponent } from './report/report.component';
import { RoleGuardGuard } from 'src/app/guard/role-guard.guard';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { SubmissionDetailsComponent } from './submission-details/submission-details.component';
import { ReviewSubmissionsComponent } from './review-submissions/review-submissions.component';
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
import { DisclaimerComponent } from './disclaimer/disclaimer.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { SitemapComponent } from './sitemap/sitemap.component';
import { UserManualComponent } from './user-manual/user-manual.component';
import { PhotoComponent } from './photo/photo.component';
import { VideoComponent } from './video/video.component';
import { ToolsComponent } from './tools/tools.component';

const routes: Routes = [
  {
    path: 'aboutus',
    component: AboutUsComponent,
    pathMatch: 'full'
  },
  {
    path: 'report',
    component: ReportComponent,
    pathMatch: 'full',
    // canActivate: [RoleGuardGuard]
  },
  {
    path: 'user-manual',
    component: UserManualComponent,
    pathMatch: 'full',
    // canActivate: [RoleGuardGuard]
  }, {
    path: 'photo',
    component: PhotoComponent,
    pathMatch: 'full',
  },{
    path: 'video',
    component: VideoComponent,
    pathMatch: 'full',
  },{
    path: 'tools',
    component: ToolsComponent,
    pathMatch: 'full',
  },
  {
    path: 'submission-management',
    component: SubmissionDetailsComponent,
    pathMatch: 'full',
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["Submission Management"]
        }
  },
  {
    path: 'review-submission',
    component: ReviewSubmissionsComponent,
    pathMatch: 'full',
    canActivate: [RoleGuardGuard],
    data: { 
      expectedRoles: ["Submission Management"]
    }
  },
  {
    path: 'change-password',
    component: ChangePasswordComponent,
    pathMatch: 'full',
    // canActivate: [RoleGuardGuard],
    // data: { 
    //   expectedRoles: ["RESET_PASSWORD"]
    // }
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    pathMatch: 'full',
    // canActivate: [RoleGuardGuard],
    // data: { 
    //   expectedRoles: ["RESET_PASSWORD"]
    // }
  },
  {
    path: 'terms-of-use',
    component: TermsOfUseComponent,
    pathMatch: 'full',
  },
  {
    path: 'disclaimer',
    component: DisclaimerComponent,
    pathMatch: 'full',
  },
  {
    path: 'privacy-policy',
    component: PrivacyPolicyComponent,
    pathMatch: 'full',
  },
  {
    path: 'sitemap',
    component: SitemapComponent,
    pathMatch: 'full',
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StaticPageRoutingModule { }
