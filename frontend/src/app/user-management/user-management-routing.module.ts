import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserManagementComponent } from './user-management/user-management.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { EditUserDetailsComponent } from './edit-user-details/edit-user-details.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { CreateNewUserComponent } from './create-new-user/create-new-user.component';
import { UpdateUserProfileComponent } from './update-user-profile/update-user-profile.component';
import { BulkUserRegistrationComponent } from './bulk-user-registration/bulk-user-registration.component';

const routes: Routes = [
  {
    path: "user-management",
    component: UserManagementComponent,
    pathMatch: "full",
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
  },
  {
    path: "reset-password",
    component: ResetPasswordComponent,
    pathMatch: "full",
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
  },
  { 
    path: 'edit-user', 
    pathMatch: 'full', 
    component: EditUserDetailsComponent,
    canActivate: [RoleGuardGuard],
    data: { 
      expectedRoles: ["USER_MGMT_ALL_API"]
    }
  },
  {
    path: 'manage-user',
    pathMatch: 'full',
    component: ManageUsersComponent,
    canActivate: [RoleGuardGuard],
    data: { 
      expectedRoles: ["USER_MGMT_ALL_API"]
    }
  },
  {
    path: 'create-new-user',
    pathMatch: 'full',
    component: CreateNewUserComponent,
    // canActivate: [RoleGuardGuard],
    // data: { 
    //   expectedRoles: ["CREATE_USER","UPDATE_USER","RESET_PASSWORD"]
    // }
  },
  {
    path: 'update-user-profile',
    pathMatch: 'full',
    component: UpdateUserProfileComponent,
    // canActivate: [RoleGuardGuard],
    // data: { 
    //   expectedRoles: ["UPDATE_USER"]
    // }
  },
  {
    path: 'bulk-user-registration',
    pathMatch: 'full',
    component: BulkUserRegistrationComponent,
    canActivate: [RoleGuardGuard],
    data: { 
      expectedRoles: ["USER_MGMT_ALL_API"]
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserManagementRoutingModule { }
