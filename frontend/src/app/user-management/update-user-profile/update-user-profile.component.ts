import { Component, OnInit } from '@angular/core';
import { UserManagementService } from '../services/user-management.service';
import { FormGroup, FormControl } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Constants } from 'src/app/constants';
import { UserService } from 'src/app/services/user/user.service';
import { Router, RoutesRecognized } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AppService } from 'src/app/app.service';
import { DateAdapter, MAT_DATE_FORMATS } from '@angular/material';
import { DateFormat, APP_DATE_FORMATS } from 'src/app/date-format';
declare var $: any;
import * as _moment from 'moment';
import {default as _rollupMoment, Moment} from 'moment';
const moment = _rollupMoment || _moment;

@Component({
  selector: 'rmncha-update-user-profile',
  templateUrl: './update-user-profile.component.html',
  styleUrls: ['./update-user-profile.component.scss'],
  providers: [
    {
      provide: DateAdapter, useClass: DateFormat
    },
    {
      provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS
    }
  ]
})
export class UpdateUserProfileComponent implements OnInit {

  selectedRoleId: number;
  selectedStateId: number;
  selectedDistrictId: number;
  selectedBlockId: number;
  userManagementService: UserManagementService;
  myform: FormGroup;
  value: any;
  accessLevelSearch: any;
  selected = -1;
  selectedYears: any[];
  years: any[];
  currentFileUpload: boolean = false;
  progress: { percentage: number } = { percentage: 0 };
  fileName: string;
  finalUpload: any;
  checkBox = {};
  app: any;
  blankDesignationFieldError: string;
  showBlankFeildError: boolean = false;
  leftTime: number;
  newDate:any;
  date = new FormControl(moment());
  minDate = new Date(1990, 0, 1);
  ifChecked: boolean;
  ifNotChecked: boolean;
  userService: UserService;
  filteredOptions: string[];

  maxDate = new Date(Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]), Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]) - 1, Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]));

  constructor(private datepipe: DatePipe, private userManagementServiceProvider: UserManagementService,
    private http: HttpClient, private userServiceProvider: UserService, private router: Router, private appService: AppService) {
    this.userManagementService = userManagementServiceProvider;
    this.userService = userServiceProvider;
    this.app = appService;
  }

  ngOnInit() {
    this.userManagementService.file = [];
    this.userManagementService.updateUserDetails = {};
    this.userManagementService.otpSuccessMsg = "";
    this.currentFileUpload = false;
    this.userManagementService.uploadIdentityProof == undefined;
    this.userManagementService.afterEmailVerified = false;
    this.userManagementService.enterOTPForValidate = false;
    this.userManagementService.showOTPTickerWrong = false;
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.accessLevelNames = [];
    this.userManagementService.isAdmin = "user";
    this.userManagementService.idProofTypeName = undefined; 
    
    this.userManagementService.getRegisteredUserData().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.updateUserDetails = data;
      this.userManagementService.userLevelIDFetched = [];
      if (this.userManagementService.updateUserDetails && this.userManagementService.updateUserDetails.sessionMap.roleId == 1) {
        this.userManagementService.userLevelName = this.userManagementService.updateUserDetails.sessionMap.areaLevel;
        this.userManagementService.userLevelIDFetched.push(this.userManagementService.updateUserDetails.sessionMap.roleId);
      }
      if (this.userManagementService.updateUserDetails && this.userManagementService.updateUserDetails.sessionMap.roleId == 2) {
        this.selectedStateId = this.userManagementService.updateUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
        this.userManagementService.userLevelName = this.userManagementService.updateUserDetails.sessionMap.areaLevel;
        this.userManagementService.selectedStateName = this.userManagementService.updateUserDetails.sessionMap.area[0].areaName;
        this.userManagementService.userLevelIDFetched.push(this.selectedStateId);
      }
      if (this.userManagementService.updateUserDetails && this.userManagementService.updateUserDetails.sessionMap.roleId == 3) {
        this.selectedStateId = this.userManagementService.updateUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
        this.selectedDistrictId = this.userManagementService.updateUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 3)[0].areaId;
        this.userManagementService.userLevelName = this.userManagementService.updateUserDetails.sessionMap.areaLevel;
        this.userManagementService.selectedStateName = this.userManagementService.updateUserDetails.sessionMap.area[0].areaName;
        this.userManagementService.selectedDistrictName = this.userManagementService.updateUserDetails.sessionMap.area[1].areaName;
        this.userManagementService.userLevelIDFetched.push(this.selectedStateId);
        this.userManagementService.userLevelIDFetched.push(this.selectedDistrictId);
      }
      if (this.userManagementService.updateUserDetails && this.userManagementService.updateUserDetails.sessionMap.roleId == 4) {
        this.selectedStateId = this.userManagementService.updateUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
        this.selectedDistrictId = this.userManagementService.updateUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 3)[0].areaId;
        this.selectedBlockId = this.userManagementService.updateUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 4)[0].areaId;
        this.userManagementService.userLevelName = this.userManagementService.updateUserDetails.sessionMap.areaLevel;
        this.userManagementService.selectedStateName = this.userManagementService.updateUserDetails.sessionMap.area[0].areaName;
        this.userManagementService.selectedDistrictName = this.userManagementService.updateUserDetails.sessionMap.area[1].areaName;
        this.userManagementService.selectedBlockName = this.userManagementService.updateUserDetails.sessionMap.area[2].areaName;
        this.userManagementService.userLevelIDFetched.push(this.selectedStateId);
        this.userManagementService.userLevelIDFetched.push(this.selectedDistrictId);
        this.userManagementService.userLevelIDFetched.push(this.selectedBlockId);
      }
      if (this.userManagementService.updateUserDetails.sessionMap.organization == 5) {
        this.userManagementService.showDevelopmentField = true;
      } else {
        this.userManagementService.showDevelopmentField = false;
      }

      // for other dev partner
      if (this.userManagementService.updateUserDetails.sessionMap.devPartner == "Others (specify)") {
        this.userManagementService.showDevelopmentPartnerOthers = true;
      } else {
        this.userManagementService.showDevelopmentPartnerOthers = false;
      }
      if (this.userManagementService.updateUserDetails.sessionMap.idProTypeName == "Others (specify)") {
        this.userManagementService.showIDProofOtherBlock = true;
      } else {
        this.userManagementService.showIDProofOtherBlock = false;
      }
      if (this.userManagementService.updateUserDetails.sessionMap.idProType != null && (this.userManagementService.updateUserDetails.sessionMap.idProofFile != "" && this.userManagementService.updateUserDetails.sessionMap.idProofFile != null)) {
        this.userManagementService.showUploadIdProofBlock = true;
        this.userManagementService.fetchedUploadIdentityProof = this.userManagementService.updateUserDetails.sessionMap.idProofFile.substring(this.userManagementService.updateUserDetails.sessionMap.idProofFile.lastIndexOf('/') + 1);
      }
      if (this.userManagementService.updateUserDetails.sessionMap.idProType != null && (this.userManagementService.updateUserDetails.sessionMap.idProofFile != null && this.userManagementService.updateUserDetails.sessionMap.idProofFile != "")) {
        this.userManagementService.showUploadIdProofBlock = true;
        this.userManagementService.fetchedUploadIdentityProof = this.userManagementService.updateUserDetails.sessionMap.idProofFile.substring(this.userManagementService.updateUserDetails.sessionMap.idProofFile.lastIndexOf('/') + 1);
      } else {
        this.userManagementService.showUploadIdProofBlock = false;
      }
      if (this.userManagementService.updateUserDetails) {
        console.log(this.userManagementService.fetchedUploadIdentityProof);

        this.userManagementService.fetchedEmailId = this.userManagementService.updateUserDetails.emailId;
        if (this.userManagementService.fetchedEmailId != "" || this.userManagementService.fetchedEmailId != undefined) {
          this.userManagementService.checkDuplicateEmail = false;
        }
        this.userManagementService.organizationName = this.userManagementService.updateUserDetails.sessionMap.orgName;
        this.userManagementService.designationName = this.userManagementService.updateUserDetails.designationNames[0];
        // this.userManagementService.accessLevelNames = this.userManagementService.updateUserDetails.authorities;
        this.userManagementService.idProofTypeName = this.userManagementService.updateUserDetails.sessionMap.idProTypeName;
        this.userManagementService.fetchedDesignationId.push(this.userManagementService.updateUserDetails.designationIds[0])
        this.userManagementService.fetchedDevPartnerId = this.userManagementService.updateUserDetails.sessionMap.devPartnerId;
        this.userManagementService.devPartnerName = this.userManagementService.updateUserDetails.sessionMap.devPartner;
        this.userManagementService.updateUserDetails.authorities.forEach(element => {
          if (element == "USER_MGMT_ALL_API") {
            this.userManagementService.isAdmin = "Admin";
            return false;
          }
        });
        // date
        let momentDate = moment(this.userManagementService.updateUserDetails.sessionMap.dob,  "DD/MM/YYYY");
        this.newDate = new Date(momentDate as any);
        let dd: number | string = this.newDate.getDate();
        if (dd < 10) {
          dd = '0' + dd;
        }
        let mm: number | string = this.newDate.getMonth() + 1;
        if (mm < 10) {
          mm = '0' + mm;
        }
        const yy: number = this.newDate.getFullYear();
        this.userManagementService.updateUserDetails.sessionMap.dob = this.newDate
        this.userManagementService.dateOfBirth = `${dd}/${mm}/${yy}`;
      }

    });
    if (!this.userManagementService.formFieldsAll)
      this.userManagementService.getUserRoles().subscribe(data => {
        this.userManagementService.formFieldsAll = data;
      })
    if (!this.userManagementService.areaDetails)
      this.userManagementService.getAreaDetails().subscribe(data => {
        this.userManagementService.areaDetails = data;
      })

    this.userManagementService.getAllDesignations().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.designationLists = this.userManagementService.allData;
      this.filteredOptions = this.userManagementService.designationLists;
    });
    this.userManagementService.getAllOrganizations().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.organizationLists = this.userManagementService.allData;
    });

    this.userManagementService.getAllDevelopmentPartners().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.developmentPartnerLists = this.userManagementService.allData;
    });

    this.userManagementService.getAllAuthorities().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.accessLevelLists = this.userManagementService.allData;
      for (let i = 0; i < this.userManagementService.accessLevelLists.length; i++) {
        for (let j = 0; j < this.userManagementService.updateUserDetails.authorities.length; j++) {
          if (this.userManagementService.updateUserDetails.sessionMap.authorityIds[j] == this.userManagementService.accessLevelLists[i].id) {
            this.userManagementService.accessLevelNames.push(this.userManagementService.accessLevelLists[i].authority)
          }
        }
      }
    });

    this.userManagementService.getIDProofDetails().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.idProofLists = this.userManagementService.allData;
    });

    $(".toggle-password").click(function () {
      $(this).toggleClass("fa-eye fa-eye-slash");
      let input = $($(this).attr("toggle"));
      if (input.attr("type") == "password") {
        input.attr("type", "text");
      } else {
        input.attr("type", "password");
      }
    });
    
  }
  uploadClicked() {
    $("#fileUpload").click();
  }

  //  email format test
  emailVerify() {
    let emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
    if (emailPattern.test(this.userManagementService.updateUserDetails.emailId)) {
      if (this.userManagementService.fetchedEmailId != this.userManagementService.updateUserDetails.emailId) {
        this.userManagementService.afterEmailVerified = true;
        $('#generate-otp').removeAttr('disabled');
        this.userManagementService.showErrorIfOTPnotVerified = true;
        this.userManagementService.enterOTP = "";
        this.userManagementService.enterOTPForValidate = false;
        this.userManagementService.otpSuccessMsg = "";
        this.userManagementService.otpSuccessMsg = "";
        this.userManagementService.showOTPTickerWrong = false;
      } else {
        this.userManagementService.checkDuplicateEmail = false;
        this.userManagementService.afterEmailVerified = false;
        this.userManagementService.showErrorIfOTPnotVerified = false;
      }
    } else {
      this.userManagementService.afterEmailVerified = false;
      this.userManagementService.showErrorIfOTPnotVerified = false;
    }
  }

  // duplicate email verification
  duplicateEmailVerification(email) {
    let emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
    if (email != "" && email != undefined) {
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/userEmailAvailablity?email="
        + email).subscribe((data) => {
          this.userManagementService.checkDuplicateEmail = Boolean(data);
          if (this.userManagementService.checkDuplicateEmail == true && emailPattern.test(email) && (this.userManagementService.fetchedEmailId === this.userManagementService.updateUserDetails.emailId)) {
            this.userManagementService.afterEmailVerified = false;
            this.userManagementService.checkDuplicateEmail = false;
          } else if (this.userManagementService.checkDuplicateEmail == false) {
            this.userManagementService.afterEmailVerified = true;
          } else {
            this.userManagementService.afterEmailVerified = false;
          }
          // if (this.userManagementService.checkDuplicateEmail == false) {
          //   this.userManagementService.afterEmailVerified = true;
          // } else{
          //   this.userManagementService.afterEmailVerified = false;
          // }
        });
    } else {
      this.userManagementService.checkDuplicateEmail = false;
      this.userManagementService.enterOTPForValidate = false;
    }
  }

  // sending otp to email
  sendOTP() {
    if (this.userManagementService.updateUserDetails.emailId != ""
      || this.accessLevelSearch.userManagementService.updateUserDetails.emailId != undefined) {
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/getEmailVarificationCode?email="
        + this.userManagementService.updateUserDetails.emailId, { responseType: 'text' }).subscribe((data) => {
          this.userManagementService.enterOTPForValidate = true;
          this.userManagementService.showErrorIfOTPnotVerified = false;
          this.userManagementService.otpSuccessMsg = "OTP sent to your email";
          this.userManagementService.otpErrorMsg = "";
          $('#generate-otp').attr('disabled',true);
          this.timeLeft();
        }, err => {
          this.userManagementService.otpSuccessMsg = "";
          this.userManagementService.otpErrorMsg = "OTP did not send, please verify your email ID or try after some time.";
        });
    }
  }

  timeLeft() {
    this.leftTime = 30;
  }
  onFinished() {
    $('#generate-otp').removeAttr('disabled',true);
    this.userManagementService.enterOTP = "";
    this.userManagementService.enterOTPForValidate = false;
    this.userManagementService.otpSuccessMsg = "";
    this.userManagementService.otpErrorMsg = "OTP is expired.";
    this.userManagementService.showErrorIfOTPnotVerified = true;
  }

  // validate otp
  validateOTP(enteredOTP) {
    if (enteredOTP != undefined && enteredOTP != null) {
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/getEmailOTPAvailability?email="
        + this.userManagementService.updateUserDetails.emailId + "&varificationCode=" + enteredOTP, { responseType: 'text' }
      ).subscribe((data) => {
        if (data == "OTP verified") {
          this.userManagementService.showOTPTickerSuccess = true;
          setTimeout(() => {
            this.userManagementService.showOTPTickerSuccess = false;
            $('#emailverifiedmsgDiv').fadeIn('fast').delay(7000).fadeOut('fast');
          }, 4000);
          this.userManagementService.afterEmailVerified = false;
          this.userManagementService.enterOTPForValidate = false;
          this.userManagementService.showOTPTickerWrong = false;
        }
        else if (data == "Invalid OTP! Please enter valid OTP") {
          this.userManagementService.showOTPTickerWrong = true;
          this.userManagementService.showOTPTickerSuccess = false;
        }
      }
      );
    }
  }

  onChangeEvent(e): void {
    this.userManagementService.dob = e.target.value;
    let newDate = new Date(e.target.value)

    let dd: number | string = newDate.getDate();
    if (dd < 10) {
      dd = '0' + dd;
    }
    let mm: number | string = newDate.getMonth() + 1;
    if (mm < 10) {
      mm = '0' + mm;
    }
    const yy: number = newDate.getFullYear();
    this.userManagementService.dateOfBirth = `${dd}/${mm}/${yy}`;
  }

  selectUserLevel(areaLevel,event) {
   if (event.isUserInput == true) {
    this.userManagementService.userLevelID = [];
    this.userManagementService.userLevelName = areaLevel.areaLevelName;
    this.userManagementService.userLevelID.push(areaLevel.areaLevelId);
    }
  }

  // selecting state levels
  selectStateLevel(selectedStatetId,event) {
    if (event.isUserInput == true) {
    this.userManagementService.userLevelID = [];
    this.selectedStateId = selectedStatetId.areaId;
    this.userManagementService.selectedStateName = selectedStatetId.areaName;
    this.userManagementService.userLevelID.push(this.selectedStateId);
    }
  }
  // selecting district levels
  selectDistrictLevel(selectedDistrictId,event) {
    if (event.isUserInput == true) {
    this.selectedDistrictId = selectedDistrictId.areaId;
    this.userManagementService.selectedDistrictName = selectedDistrictId.areaName;
    this.userManagementService.userLevelID.splice(1, 1, this.selectedDistrictId);
    }
  }

  // selecting block levels
  selectBlockLevel(selectedBlockId,event) {
    if (event.isUserInput == true) {
    this.selectedBlockId = selectedBlockId.areaId;
    this.userManagementService.selectedBlockName = selectedBlockId.areaName;
    this.userManagementService.userLevelID.splice(2, 2, this.selectedBlockId);
    }
  }

  // selecting access levels
  selectAccessLevels(accessLevelNames, accessLevelLists) {
    this.userManagementService.selectedAccessLevelID = [];
    this.userManagementService.selectedAccessLevelName = [];
    this.userManagementService.accessLevelNames = [];
    accessLevelNames.forEach(element => {
      this.userManagementService.selectedAccessLevelID.push(element);
      this.userManagementService.accessLevelNames.push(accessLevelLists.filter(d => d.id == element)[0].authority);
    });
  }
  selectedOrganization(organizationsId, organizationLists) {
    this.userManagementService.organizationName = organizationLists.filter(d => d.organizationId == organizationsId)[0].organizationName;
    if (this.userManagementService.organizationName == "Development Partners") {
      this.userManagementService.showDevelopmentField = true;
    }
    else {
      this.userManagementService.showDevelopmentField = false;
      this.userManagementService.showDevelopmentPartnerOthers = false;
    }
  }

  // selecting development partners
  selectdevelopmentPartners(developmentPartnerList, developmentPartnerID) {
    this.userManagementService.devPartnerName = developmentPartnerList.filter(d => d.id == developmentPartnerID)[0].name;
    this.userManagementService.selectedDevPartnerId = developmentPartnerList.filter(d => d.id == developmentPartnerID)[0].id;
    if (this.userManagementService.devPartnerName == "Others (specify)") {
      this.userManagementService.showDevelopmentPartnerOthers = true;
      this.userManagementService.editUserDetails.othersDevPartner = undefined;
    } else {
      this.userManagementService.showDevelopmentPartnerOthers = false;
    }
  }

  selectIDProof(idProof, event) {
    if (event.isUserInput == true) {
      this.userManagementService.showUploadIdProofBlock = true;
      this.userManagementService.idProofTypeName =idProof.name;
      this.userManagementService.updateUserDetails.sessionMap.idProofFile = "";
      this.userManagementService.showUploadIdProofBlock = true;
      // this.userManagementService.fetchedUploadIdentityProof = "";
      if (this.userManagementService.idProofTypeName == "Others (specify)") {
        this.userManagementService.showIDProofOtherBlock = true;
        this.userManagementService.updateUserDetails.sessionMap.othersIdProof = undefined;
        return false;
      } else {
        this.userManagementService.updateUserDetails.sessionMap.othersIdProof = undefined;
        this.userManagementService.showIDProofOtherBlock = false;
      }
   }
}
uncheckIDProof(event){
  if (event.isUserInput == true) {
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showUploadIdProofBlock = false;
    this.userManagementService.showIDProofOtherBlock = false;
    this.userManagementService.updateUserDetails.sessionMap.idProTypeName = undefined;
    this.userManagementService.updateUserDetails.sessionMap.idProofFile = undefined;
  }
}
  selectedDesignation(designationId, designationLists) {
    this.userManagementService.designationID = [];
    this.userManagementService.designationName = designationLists.filter(d => d.id == designationId)[0].name;
    this.userManagementService.designationID.push(designationId);
  }
  addDesignation() {
    $("#addNew").modal("show");
  }


  resetDesignationField(model) {
    model = undefined;
  }
  // adding new designation
  addNewDesignation(designation) {
    if (designation == undefined) {
      this.showBlankFeildError = true;
      this.blankDesignationFieldError = "Pleas enter designation";
    } else {
      this.showBlankFeildError = false;
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/addDesignation?designation="
        + designation, { responseType: 'text' }).subscribe((data) => {
          this.userManagementService.getAllDesignations().subscribe(data => {
            this.userManagementService.getData(data);
            this.userManagementService.designationLists = this.userManagementService.allData;

            this.filteredOptions = this.userManagementService.designationLists;
          });
          $("#addNew").modal("hide");
        }, err => {
          console.log(err);
        });
    }
  }

  // Identification information checking duplicate user name
  checkDuplicateUserName(userName) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/usernameAvailablity?username="
      + userName).subscribe((data) => {
        this.userManagementService.checkDuplicateUser = JSON.stringify(data);
      });
  }
  resetAllField() {
    this.userManagementService.showDevelopmentPartnerOthers = false;
    this.userManagementService.showIDProofOtherBlock = false;
    this.userManagementService.file = [];
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.updateUserDetails.sessionMap.dob = null;
    this.userManagementService.fetchedUploadIdentityProof = "";
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showIfGenderNotSelected = true;
    this.userManagementService.afterEmailVerified = false;
    this.userManagementService.enterOTPForValidate = false;
  }
  success() {
    this.appService.logout();
    this.app.userName = "";
  }
  selectGender() {
    this.userManagementService.showIfGenderNotSelected = true;
  }
  confirmPassValidate(p, rep) {
    if (p === rep) {
      this.userManagementService.passwordMatched = true;
      this.userManagementService.passwordDoesNotMatched = false;
    } else {
      this.userManagementService.passwordDoesNotMatched = true;
      this.userManagementService.passwordMatched = false;
    }
  }
  onFileChange($event) {
    if($event.srcElement.files.length == 0){
      this.userManagementService.uploadIdentityProof = undefined;
      this.userManagementService.file.length = 0;
      this.currentFileUpload = false;
    }else{
      if ($event.srcElement.files[0]) {
        if (
          (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'jpg')
          || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'jpeg')
          || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'png')
          || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'pdf')
        ) {
          this.userManagementService.fetchedUploadIdentityProof = "";
          this.userManagementService.uploadIdentityProof = $event.srcElement.files[0].name;
          this.userManagementService.file = $event.srcElement.files[0];
          this.currentFileUpload = false;
          this.userManagementService.showUploadFileError = false;
        } else {
          this.userManagementService.uploadIdentityProof = '';
          this.userManagementService.file = undefined;
          $event.srcElement.value = null;
          this.userManagementService.errorMessage = 'Upload a file in .jpg, .jpeg, .png or in .pdf format';
          $('#errorModal').modal('show');
        }
      }
    } 

  }


  // download id proof file
  downloadImageFile(filePath) {
    window.location.href = Constants.COLLECTION_SERVICE_URL + 'downloadFromFilePath?filePath=' + filePath;
  }

  //  submitting value
  submit() {
    // as per uat feedback making id proof optional
    if( (this.userManagementService.showUploadIdProofBlock 
       && (this.userManagementService.uploadIdentityProof == undefined 
      && ( this.userManagementService.fetchedUploadIdentityProof == undefined
       || this.userManagementService.fetchedUploadIdentityProof == "" ) ))) {
        this.currentFileUpload = true;
        return false;
    } else 
    if (this.userManagementService.checkDuplicateEmail == true) {
      $('html,body').animate({
        scrollTop: $('.user-name-duplicate').offset().top - 250
      }, 'slow');
      return false;
    }
  }

  previewForm() {
    if (this.userManagementService.afterEmailVerified == true &&
      this.userManagementService.showErrorIfOTPnotVerified == true) {
      $('html,body').animate({
        scrollTop: $('#showOTPblockError').offset().top - 250
      }, 'slow');
      return false;
    } else if (this.userManagementService.enterOTPForValidate == true && this.userManagementService.enterOTP == '') {
      $('html,body').animate({
        scrollTop: $('#enterOPTBlockError').offset().top - 250
      }, 'slow');
      return false;
    }else if (this.userManagementService.showOTPTickerWrong == true) {
      $('html,body').animate({
        scrollTop: $('#wrongOPTBlockError').offset().top - 250
      }, 'slow');
      return false;
    }else {
      $("#previewModal").modal("show");
    }
  }

  submitDetails() {
    this.userManagementService.upload().then(e => {
      let filepath = e.body;
      let finalFilePath;
      if (filepath == "") {
        finalFilePath = this.userManagementService.updateUserDetails.sessionMap.idProofFile;
      } else {
        finalFilePath = filepath;
      }
      this.userManagementService.updateUserDetails['dob'] = this.userManagementService.dateOfBirth;
      this.userManagementService.updateUserDetails['firstName'] = this.userManagementService.updateUserDetails.sessionMap.firstName;
      this.userManagementService.updateUserDetails['gender'] = this.userManagementService.updateUserDetails.sessionMap.gender;
      this.userManagementService.updateUserDetails['idProType'] = this.userManagementService.updateUserDetails.sessionMap.idProType;
      this.userManagementService.updateUserDetails['lastName'] = this.userManagementService.updateUserDetails.sessionMap.lastName;
      this.userManagementService.updateUserDetails['middleName'] = this.userManagementService.updateUserDetails.sessionMap.middleName;
      this.userManagementService.updateUserDetails['mobNo'] = this.userManagementService.updateUserDetails.sessionMap.mobNo;
      this.userManagementService.updateUserDetails['organization'] = this.userManagementService.updateUserDetails.sessionMap.organization;
      this.userManagementService.updateUserDetails['othersDevPartner'] = this.userManagementService.updateUserDetails.sessionMap.othersDevPartner;
      this.userManagementService.updateUserDetails['othersIdProof'] = this.userManagementService.updateUserDetails.sessionMap.othersIdProof;
      this.userManagementService.updateUserDetails['roleId'] = this.userManagementService.updateUserDetails.sessionMap.roleId;
      this.userManagementService.updateUserDetails['idProofFile'] = finalFilePath;
      this.userManagementService.updateUserDetails['areaId'] = this.userManagementService.userLevelID.length == 0 ? this.userManagementService.userLevelIDFetched : this.userManagementService.userLevelID;
      this.userManagementService.updateUserDetails['authorityIds'] = this.userManagementService.selectedAccessLevelID.length == 0 ? this.userManagementService.updateUserDetails.sessionMap.authorityIds : this.userManagementService.selectedAccessLevelID;
      this.userManagementService.updateUserDetails['areaLevel'] = this.userManagementService.updateUserDetails.sessionMap.roleId;
      this.userManagementService.updateUserDetails['designation'] = this.userManagementService.designationID.length == 0 ? this.userManagementService.fetchedDesignationId : this.userManagementService.designationID;
      this.userManagementService.updateUserDetails['authority_control_type'] = "authority";
      this.userManagementService.updateUserDetails['id'] = this.userManagementService.updateUserDetails.userId;
      this.userManagementService.updateUserDetails['designationNames'] = this.userManagementService.designationName;
      this.userManagementService.updateUserDetails['orgName'] = this.userManagementService.organizationName;
      this.userManagementService.updateUserDetails['devPartner'] = this.userManagementService.selectedDevPartnerId == undefined ? this.userManagementService.fetchedDevPartnerId : this.userManagementService.selectedDevPartnerId;
      this.userManagementService.updateUserDetails["isAdmin"] = this.userManagementService.isAdmin;

      this.userManagementService.dataModel = this.userManagementService.updateUserDetails;
      this.http.post(Constants.COLLECTION_SERVICE_URL + "updateUser",
        this.userManagementService.dataModel).subscribe(response => {
          this.userManagementService.submitSuccessMsg = response;
          $("#previewModal").modal("hide");
          $("#successModal").modal('show');
        },
          error => {
            $("#previewModal").modal("hide");
            this.userManagementService.errorMessage = "Some error found."
            $("#errorModal").modal('show');
          });
    })
  }


}
