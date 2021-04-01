import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserManagementRoutingModule } from './user-management-routing.module';
import { UserManagementComponent } from './user-management/user-management.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatListModule,MatFormFieldModule, MatInputModule, MatSelectModule,
    MatTabsModule, MatTableModule, MatPaginatorModule, MatCheckboxModule,
    MatSortModule, MatButtonModule, MatIconModule, MatDialogModule, MatCardModule, MatRadioModule, MatNativeDateModule, MatListItem, MAT_CHECKBOX_CLICK_ACTION,DateAdapter, MatAutocompleteModule  } from '@angular/material';
import { MatDatepickerModule} from '@angular/material/datepicker';
import { UserSideMenuComponent } from './user-side-menu/user-side-menu.component';
import { UserManagementService } from './services/user-management.service';
import { EditUserDetailsComponent } from './edit-user-details/edit-user-details.component';
import { AreaFilterPipe } from './filters/area-filter.pipe';
import { FrequencyFilterPipe } from './filters/frequency-filter.pipe';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { DialogComponent } from './dialog/dialog.component';
import { CreateNewUserComponent } from './create-new-user/create-new-user.component';
import { MdbInputDirective } from 'angular-bootstrap-md';
import { RemoveColumnsPipe } from './filters/remove-columns.pipe';
import { SearchTextPipe } from './filters/search-text.pipe';
import {NgxPaginationModule} from 'ngx-pagination';
import { UpdateUserProfileComponent } from './update-user-profile/update-user-profile.component';
import { BulkUserRegistrationComponent } from './bulk-user-registration/bulk-user-registration.component';
import { CommonServiceService } from '../page/analytics/service/api/common-service.service';
import { DateFormat } from '../date-format';
import { CountdownModule } from 'ngx-countdown';
import { FilterOptionPipe } from './filters/filter-option.pipe';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    UserManagementRoutingModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    MatRadioModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatListModule,
    NgxPaginationModule,
    CountdownModule
  ],
  declarations: [
    UserManagementComponent,
    UserSideMenuComponent,
    EditUserDetailsComponent,
    AreaFilterPipe,
    FrequencyFilterPipe,
    ResetPasswordComponent,
    ManageUsersComponent,
    DialogComponent,
    CreateNewUserComponent,
    RemoveColumnsPipe,
    SearchTextPipe,
    UpdateUserProfileComponent,
    BulkUserRegistrationComponent,
    FilterOptionPipe
  ],
  entryComponents: [DialogComponent],
  providers:[UserManagementService,{provide: MAT_CHECKBOX_CLICK_ACTION, useValue: 'check'},
  { provide: DateAdapter, useClass: DateFormat },CommonServiceService]
})
export class UserManagementModule {
  
  constructor(private dateAdapter:DateAdapter<Date>) {
    dateAdapter.setLocale('en-GB'); // DD/MM/YYYY
    }
 }
