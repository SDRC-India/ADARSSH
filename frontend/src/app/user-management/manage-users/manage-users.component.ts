import { HttpClient } from '@angular/common/http';
import { Component, Directive, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MatTableDataSource } from '@angular/material';
import { Constants } from 'src/app/constants';
import { DialogComponent } from '../dialog/dialog.component';
import { UserManagementService } from '../services/user-management.service';
declare var $: any;
// export interface UserData {
//   select: boolean;
//   uName: string;
//   name: string;
//   emailId: string;
//   dob: string;
//   location: string;
//   designation: string;
//   mobileNumber: string;
//   aadhaarNo: string;
//   aadhaarCard: string;
//   panNo: string;
//   panCard: string;
//   submittedOn: string;
// }
Directive({
  selector: '[appHideMe]',
  exportAs: 'appHideMe',

})
export interface UserData {
  areaLevel: string;
  areas: any[];
  authorities: any;
  authorityIds: any;
  desgnName: any;
  designation: number;
  devPartner: string;
  dob: any;
  emailId: any;
  firstName: string;
  gender: string;
  idProType: string;
  idProofFile: any;
  lastName: string;
  middleName: string;
  mobNo: string;
  name: string;
  orgName: string;
  organization: number;
  othersDevPartner: any;
  othersIdProof: any;
  roleIds: any;
  roles: any;
  userId: string;
}
/**
 * @title Data table with sorting, pagination, and filtering.
 */
@Component({
  selector: 'rmncha-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.scss']
})
export class ManageUsersComponent implements OnInit {
  displayedColumnsForPendingUsers: string[] = ['Select', 'User Name', 'Name', 'Gender',
    'Email ID', 'Date of Birth', 'Location', 'Designation', 'Organization', 'Mobile Number', 'ID Proof', 'Submitted On',
  ];
  displayedColumnsForApprovedUsers: string[] = ['User Name', 'Name', 'Gender',
    'Email ID', 'Date of Birth', 'Location', 'Designation', 'Organization', 'Mobile Number', 'ID Proof', 'Submitted On', 'Approved On'];
  displayedColumnsForRejectedUsers: string[] = ['User Name', 'Name', 'Gender',
    'Email ID', 'Date of Birth', 'Location', 'Designation', 'Organization', 'Mobile Number', 'ID Proof', 'Submitted On', 'Rejected On', 'Rejection Reason'];
  columnsToDisplayForPendingUsers: string[] = this.displayedColumnsForPendingUsers.slice();
  columnsToDisplayForApprovedUsers: string[] = this.displayedColumnsForApprovedUsers.slice();
  columnsToDisplayForRejectedUsers: string[] = this.displayedColumnsForRejectedUsers.slice();
  pendingUsersDetails: MatTableDataSource<UserData>;
  approvedUsersDetails: MatTableDataSource<UserData>;
  rejectedUsersDetails: MatTableDataSource<UserData>;
  // data: UserData[] = ELEMENT_DATA;
  // selection = new SelectionModel<UserData>(true, []);
  dialogRef: MatDialogRef<DialogComponent>;
  userManagementService: UserManagementService;

  ifChecked: boolean;
  ifNotChecked: boolean;

  constructor(private dialog: MatDialog,
    private userManagementServiceProvider: UserManagementService, private http: HttpClient) {
    this.userManagementService = userManagementServiceProvider;
  }

  ngOnInit() {
    this.userManagementService.p = 1;
    this.userManagementService.a = 1;
    this.userManagementService.r = 1;
    this.userManagementService.noData = false;
    this.userManagementService.getUserDetails().subscribe(data => {
      this.userManagementService.getData(data);
      if ((this.userManagementService.pendingUsers.length == 0
        && this.userManagementService.approveUsers.length == 0
        && this.userManagementService.rejectedUsers.length == 0) ||
        (this.userManagementService.pendingUsers.length == 0
          && this.userManagementService.approveUsers.length > 0
          && this.userManagementService.rejectedUsers.length > 0) ||
        (this.userManagementService.pendingUsers.length == 0
          && this.userManagementService.approveUsers.length > 0
          && this.userManagementService.rejectedUsers.length == 0) ||
        (this.userManagementService.pendingUsers.length == 0
          && this.userManagementService.approveUsers.length == 0
          && this.userManagementService.rejectedUsers.length > 0)) {
        this.userManagementService.noData = true;
      } else {
        this.userManagementService.noData = false;
      }
    })
  }
  reasonText(model) {
    this.userManagementService.reasonTexts = model;
  }

  // open modal for approval of selected user
  approveUser(approve) {
    this.userManagementService.selectedUserList = [];
    this.userManagementService.selectedUserModelList = [];
    for (let i = 0; i < this.userManagementService.pendingUsers.length; i++) {
      if (this.userManagementService.pendingUsers[i].checked) {
        this.userManagementService.selectedUserList.push(this.userManagementService.pendingUsers[i].userId);
        this.userManagementService.selectedUserModelList.push(this.userManagementService.pendingUsers[i]);
      }
    }
    if (this.userManagementService.selectedUserList.length == 0) {
      this.userManagementService.errorMessage = "Please select atleast one user ID";
      $("#errorModal").modal('show');
    }
    else {
      this.userManagementService.aprroveVariable = approve;
      if (approve) {
        this.userManagementService.infoMsg = "Please confirm if you want to approve the selected user(s)";
        $("#infoModal").modal("show");
      }
      else {
        $("#rejectionInfoMessage").modal("show");
        this.userManagementService.infoMsg = "Please confirm if you want to reject the selected user(s)"
      }
    }
  }



  approveSelected(approve) {
    this.userManagementService.rejectionReason = [];
    $("#infoModal").modal("hide");
    $("#rejectionInfoMessage").modal("hide");
    this.userManagementService.selectedUserModelList.forEach(element => {
      this.userManagementService.rejectionReason.push(element.userId + '-' + element.rejectReason)
    });
    this.http.get(Constants.COLLECTION_SERVICE_URL + "rejectAndApproveUser?userIds=" + this.userManagementService.selectedUserList + "&isApprove=" + approve
      + '&reason=' + this.userManagementService.rejectionReason,
      { responseType: 'text' }).subscribe(response => {
        this.userManagementService.successApprovedOrreject = response;
        if (this.userManagementService.successApprovedOrreject) {
          this.userManagementService.getUserDetails().subscribe(data => {
            this.userManagementService.getData(data);
            if (this.userManagementService.pendingUsers.length == 0) {
              this.userManagementService.noData = true;
              this.userManagementService.showpPendingUsersPagination = false;
              this.userManagementService.showpAcceptedUsersPagination = false;
              this.userManagementService.showpRejectedUsersPagination = false;
            }
            if (this.userManagementService.approveUsers.length == 0) {
              this.userManagementService.noData = true;
              this.userManagementService.showpPendingUsersPagination = false;
              this.userManagementService.showpAcceptedUsersPagination = false;
              this.userManagementService.showpRejectedUsersPagination = false;
            }
            if (this.userManagementService.rejectedUsers.length == 0) {
              this.userManagementService.noData = true;
              this.userManagementService.showpPendingUsersPagination = false;
              this.userManagementService.showpAcceptedUsersPagination = false;
              this.userManagementService.showpRejectedUsersPagination = false;
            }
          });
          $("#successModal").modal("show");
        }
      },
        error => {
          this.userManagementService.errorMessage = "Some error found."
          $("#errorModal").modal('show');
        });
  }
  refreshData() {
    this.userManagementService.selectedUserList = [];
    this.userManagementService.selectedUserModelList = [];
    this.userManagementService.getUserDetails().subscribe(data => {
      this.userManagementService.getData(data);
    });
  }


  //  selected tab
  tabChanged(e) {
    this.userManagementService.selectedUserModelList = [];
    if (e.index == 0) {
      if (this.userManagementService.pendingUsers.length == 0) {
        this.userManagementService.noData = true;
      } else {
        this.userManagementService.noData = false;
      }
      this.userManagementService.p = 1;
      this.userManagementService.showpPendingUsersPagination = true;
      this.userManagementService.showpAcceptedUsersPagination = false;
      this.userManagementService.showpRejectedUsersPagination = false;
      this.userManagementService.hideBtnsIfRejectedusers = true;
    } else if (e.index == 1) {
      if (this.userManagementService.approveUsers.length == 0) {
        this.userManagementService.noData = true;
      } else {
        this.userManagementService.noData = false;
      }
      this.userManagementService.a = 1;
      this.userManagementService.showpAcceptedUsersPagination = true;
      this.userManagementService.showpPendingUsersPagination = false;
      this.userManagementService.showpRejectedUsersPagination = false;
      this.userManagementService.hideBtnsIfRejectedusers = false;
    } else if (e.index == 2) {
      if (this.userManagementService.rejectedUsers.length == 0) {
        this.userManagementService.noData = true;
      } else {
        this.userManagementService.noData = false;
      }
      this.userManagementService.r = 1;
      this.userManagementService.showpPendingUsersPagination = false;
      this.userManagementService.showpAcceptedUsersPagination = false;
      this.userManagementService.showpRejectedUsersPagination = true;
      this.userManagementService.hideBtnsIfRejectedusers = false;
    } else {
      this.userManagementService.noData = false;
      this.userManagementService.hideBtnsIfRejectedusers = true;
    }
  }


  // download id proof file
  downloadImageFile(filePath) {
    window.location.href = Constants.COLLECTION_SERVICE_URL + 'downloadFromFilePath?filePath=' + filePath;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  // isAllSelected() {
  //   const numSelected = this.selection.selected.length;
  //   const numRows = this.dataSource.data.length;
  //   return numSelected === numRows;
  // }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  // masterToggle() {
  //   this.isAllSelected() ?
  //     this.selection.clear() :
  //     this.dataSource.data.forEach(row => this.selection.select(row));
  // }
  selectUserForRejectiOrApproval(e, selectUser) {
    if (e == true) {
    }
  }
  convertText(model) {
    this.userManagementService.reasonTextLengthInNumber = parseInt(model);
  }
  showLists() {
    $(".left-list").attr("style", "display: block !important");
    $('.mob-left-list').attr("style", "display: none !important");
  }

}


