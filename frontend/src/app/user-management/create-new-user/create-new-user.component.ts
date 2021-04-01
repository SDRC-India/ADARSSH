import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { UserManagementService } from '../services/user-management.service';
import { FormGroup } from '@angular/forms';
import { DateAdapter, MAT_DATE_FORMATS } from '@angular/material';
import { DatePipe } from '@angular/common';
import { Constants } from 'src/app/constants';
import { HttpClient } from '@angular/common/http';
import { DateFormat, APP_DATE_FORMATS } from 'src/app/date-format';
declare var $: any;



@Component({
  selector: 'rmncha-create-new-user',
  templateUrl: './create-new-user.component.html',
  styleUrls: ['./create-new-user.component.scss'],
  providers: [
    {
      provide: DateAdapter, useClass: DateFormat
    },
    {
      provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS
    }
  ]
})
export class CreateNewUserComponent implements OnInit {
  @Input()
  interval: number;
  checked: boolean;
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
  disclaimer = false;
  minDate = new Date(1950, 0, 1);
  ifChecked: boolean;
  ifNotChecked: boolean;
  leftTime: number;
  filteredOptions: string[];

  maxDate = new Date(Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]), Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]) - 1, Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]));
  @ViewChild('ref') ref;
  constructor(private datepipe: DatePipe, private userManagementServiceProvider: UserManagementService,
    private http: HttpClient) {
    this.userManagementService = userManagementServiceProvider;
  }

  ngOnInit() {
    this.userManagementService.createNewUserDetails = {};
    this.userManagementService.otpSuccessMsg = "";
    this.userManagementService.afterEmailVerified = false;
    this.userManagementService.enterOTPForValidate = false;
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.checkDuplicateUser = false;
    this.userManagementService.showOTPTickerWrong = false;
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showUploadIdProofBlock = false;
    this.userManagementService.idProofTypeName = undefined; 

    this.userManagementService.file = [];
    this.userManagementService.showIfGenderNotSelected = false;
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
    });

    this.userManagementService.getIDProofDetails().subscribe(data => {
      this.userManagementService.getData(data);
      this.userManagementService.idProofLists = this.userManagementService.allData;
    });

    /* hide show password */
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
  // updateDateToString(event) {

  // }
  uploadClicked() {
    $("#fileUpload").click();
  }

  /* hide generate otp btn */
  hideOTP(email) {
    if (email == undefined || email == "") {
      this.userManagementService.afterEmailVerified = false;
      this.userManagementService.showErrorIfOTPnotVerified = false;
    }
  }
  /*  email format test */
  emailVerify() {
    let emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
    if (emailPattern.test(this.userManagementService.createNewUserDetails.emailId)) {
      this.userManagementService.otpSuccessMsg = "";
      this.userManagementService.createNewUserDetails.enterOTP = "";
      this.userManagementService.afterEmailVerified = true;
      $('#generate-otp').removeAttr('disabled');
      this.userManagementService.showErrorIfOTPnotVerified = true;
      this.userManagementService.enterOTPForValidate = false;
      this.userManagementService.showOTPTickerWrong = false;
    } else {
      this.userManagementService.afterEmailVerified = false;
      this.userManagementService.showErrorIfOTPnotVerified = false;
    }
  }

  /* duplicate email verification */
  duplicateEmailVerification(email) {
    let emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
    if (email != "" && email != undefined) {
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/userEmailAvailablity?email="
        + email).subscribe(response => {
          this.userManagementService.checkDuplicateEmail = Boolean(response);
          if (this.userManagementService.checkDuplicateEmail == false && emailPattern.test(email) && (email != undefined || email != "")) {
            this.userManagementService.afterEmailVerified = true;
          } else {
            this.userManagementService.afterEmailVerified = false;
          }
        });
    } else {
      this.userManagementService.checkDuplicateEmail = false;
      this.userManagementService.enterOTPForValidate = false;
    }
  }

  /* sending otp to email */
  sendOTP() {
    this.userManagementService.enterOTP = undefined;
    this.userManagementService.showOTPTickerWrong = false;
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/getEmailVarificationCode?email="
      + this.userManagementService.createNewUserDetails.emailId, { responseType: 'text' }).subscribe(response => {
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

  timeLeft() {
    this.leftTime = 30;
  }
  onFinished() {
    $('#generate-otp').removeAttr('disabled');
    this.userManagementService.createNewUserDetails.enterOTP = "";
    this.userManagementService.enterOTPForValidate = false;
    this.userManagementService.otpSuccessMsg = "";
    this.userManagementService.otpErrorMsg = "OTP is expired.";
    this.userManagementService.showErrorIfOTPnotVerified = true;
  }

  /* validate otp */
  validateOTP(enteredOTP) {
    if (enteredOTP != undefined && enteredOTP != null) {
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/getEmailOTPAvailability?email="
        + this.userManagementService.createNewUserDetails.emailId + "&varificationCode=" + enteredOTP, { responseType: 'text' }
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
        // ,err => {
        //   this.userManagementService.showOTPTickerWrong= true;
        //   this.userManagementService.showOTPTickerSuccess = false;
        // }
      );
    }
  }

/*select date */
  onChangeEvent(e) {
    this.userManagementService.createNewUserDetails.dob = e.target.value;
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

  /*select area level */
  selectUserLevel(areaLevel,event) {
    if (event.isUserInput == true) {
    this.userManagementService.userLevelID = [];
    this.userManagementService.userLevelName = areaLevel.areaLevelName;
    this.userManagementService.userLevelID.push(areaLevel.areaLevelId);
    }
  } 

/*select state level */
  selectStateLevel(selectedStatetId,event) {
    if (event.isUserInput == true) {
    this.userManagementService.userLevelID = [];
    this.selectedStateId = selectedStatetId.areaId;
    this.userManagementService.selectedStateName = selectedStatetId.areaName;
    this.userManagementService.userLevelID.push(this.selectedStateId);
    }
  }

/*select district level */
  selectDistrictLevel(selectedDistrictId,event) {
    if (event.isUserInput == true) {
    this.selectedDistrictId = selectedDistrictId.areaId;
    this.userManagementService.selectedDistrictName = selectedDistrictId.areaName;
    this.userManagementService.userLevelID.splice(1, 1, this.selectedDistrictId);
    }
  }

  /*select block level */
  selectBlockLevel(selectedBlockId,event) {
    if (event.isUserInput == true) {
    this.selectedBlockId = selectedBlockId.areaId;
    this.userManagementService.selectedBlockName = selectedBlockId.areaName;
    this.userManagementService.userLevelID.splice(2, 2, this.selectedBlockId);
    }
  }

  /*select access level */
  selectAccessLevels() {
    this.userManagementService.selectedAccessLevelID = [];
    this.userManagementService.selectedAccessLevelName = [];
    this.userManagementService.accessLevel.forEach(element => {
      this.userManagementService.selectedAccessLevelID.push(element.id);
      this.userManagementService.selectedAccessLevelName.push(element.authority);
    });
  }


  /*select Organization */
  selectedOrganization(e, organizationsLists) {
    organizationsLists.forEach(element => {
      if (e.organizationName == "Development Partners") {
        this.userManagementService.showDevelopmentField = true;
        return false;
      } else {
        this.userManagementService.showDevelopmentField = false;
        this.userManagementService.showDevelopmentPartnerOthers = false;
        return false;
      }
    });
    this.userManagementService.selectedOrganizationID = this.userManagementService.createNewUserDetails.organization.organizationId;
  }

  /*select development Partners */
  selectdevelopmentPartners(e, developmentPartnerLists) {
    developmentPartnerLists.forEach(element => {
      if (e.name == "Others (specify)") {
        this.userManagementService.showDevelopmentPartnerOthers = true;
        return false;
      } else {
        this.userManagementService.showDevelopmentPartnerOthers = false;
        return false;
      }
    });
    this.userManagementService.developmentPartnerID = this.userManagementService.createNewUserDetails.develelopmentPartners.id;
  }

  /*select IDProof */
  selectIDProof(option, idProofLists, e) {
    if (e.isUserInput == true) {
      this.userManagementService.showUploadIdProofBlock = true;
      idProofLists.forEach(element => {
      if (option.name == "Others (specify)") {
        this.userManagementService.showIDProofOtherBlock = true;
        return false;
      }
      else {
        this.userManagementService.showIDProofOtherBlock = false;
        return false;
      }
    });
    
    this.userManagementService.idProofID = option.id;
    this.userManagementService.idProofTypeName = option.name;
    }
  }

   /*select Designation */
  selectedDesignation() {
    this.userManagementService.designationID = [];
    this.userManagementService.designationID.push(this.userManagementService.createNewUserDetails.designation.id);
  }

  /*open modal for adding new Designation */
  addDesignation() {
    $("#addNew").modal("show");
  }


  // adding new designation
  addNewDesignation(designation) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/addDesignation?designation="
      + designation, { responseType: 'text' }).subscribe(response => {
        // console.log(response);

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

  // Identification information checking duplicate user name
  checkDuplicateUserName(userName) {
    if (userName != undefined && userName != "") {
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/usernameAvailablity?username="
        + userName).subscribe(response => {
          this.userManagementService.checkDuplicateUser = Boolean(response);
        });
    } else {
      this.userManagementService.checkDuplicateUser = false;
    }
  }

  uncheckIDProof(event){
    if (event.isUserInput == true) {
      this.userManagementService.uploadIdentityProof = undefined;
      this.userManagementService.showUploadIdProofBlock = false;
      this.userManagementService.showIDProofOtherBlock = false;
      this.userManagementService.idProofTypeName = undefined;
      this.userManagementService.idProofID = undefined;
      this.userManagementService.uploadIdentityProof = undefined;
    }
  }

  resetAllField() {
    this.disclaimer = false;
    this.userManagementService.file = [];
    this.userManagementService.createNewUserDetails.emailId = "";
    this.userManagementService.createNewUserDetails.dob = undefined;
    this.userManagementService.checkDuplicateUser = false;
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showIfGenderNotSelected = true;
    this.userManagementService.afterEmailVerified = false;
    this.userManagementService.enterOTPForValidate = false;
    this.userManagementService.showUploadIdProofBlock = false;
  }

   /*gender selection*/
  selectGender() {
    this.userManagementService.showIfGenderNotSelected = false;
  }

  /*validate if password matching with confirm password feild or not*/
  confirmPassValidate(p, rep) {
    if (p === rep) {
      this.userManagementService.passwordMatched = true;
      this.userManagementService.passwordDoesNotMatched = false;
    } else {
      this.userManagementService.passwordDoesNotMatched = true;
      this.userManagementService.passwordMatched = false;
    }
  }

  /* file upload*/
  onFileChange($event) {
    if($event.srcElement.files.length == 0){
      this.userManagementService.uploadIdentityProof = undefined;
      this.userManagementService.file.length = 0;
      this.currentFileUpload = false;
    }else
    {if ($event.srcElement.files[0]) {
      if (
        (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'jpg')
        || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'jpeg')
        || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'png')
        || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === 'pdf')
      ) {
        this.userManagementService.uploadIdentityProof = $event.srcElement.files[0].name;
        this.userManagementService.file = $event.srcElement.files[0];
        this.currentFileUpload = true;
        this.userManagementService.showUploadFileError = false;
      } else {
        this.userManagementService.uploadIdentityProof = '';
        this.userManagementService.file = undefined;
        $event.srcElement.value = null;
        this.userManagementService.errorMessage = 'Upload a file in .jpg, .jpeg, .png or in .pdf format';
        $('#errorModal').modal('show');
      }
    }}
  }

  /*disclaimer checked or not */
  disclaimerChecked(checkVal) {
    if (checkVal == false) {
      this.disclaimer = true;
      this.userManagementService.ifDisclaimerNotSelected = false;
    } else if (checkVal == true) {
      this.disclaimer = false;
    }
  }
  selectCheckBox(val) {
    if (val == true) {
      this.userManagementService.ifDisclaimerNotSelected = false;
    }
  }


  /* submitting new user data */
  submitVal() {
    // as per uat feedback making id proof mandatory only if id proof type is selected
    if (this.userManagementService.showUploadIdProofBlock 
      && this.userManagementService.uploadIdentityProof == undefined && this.userManagementService.file.length == 0) {
      this.userManagementService.showUploadFileError = true;
      this.userManagementService.uploadErrorMsg = "Please upload file";
    } else 
    if (this.disclaimer == false) {
      this.userManagementService.ifDisclaimerNotSelected = true;
    } else if (this.userManagementService.checkDuplicateEmail == true) {
      $('html,body').animate({
        scrollTop: $('.user-name-duplicate').offset().top - 250
      }, 'slow');
      return false;
    }
    else {
      this.userManagementService.showUploadFileError = false;
      this.userManagementService.uploadErrorMsg = "";
      this.userManagementService.ifDisclaimerNotSelected = false;
    }
  }


  /* preview form in modal before submitting */
  previewForm() {
    if (this.userManagementService.showErrorIfOTPnotVerified == true) {
      $('html,body').animate({
        scrollTop: $('#showOTPblockError').offset().top - 250
      }, 'slow');
      return false;
    } else if (this.userManagementService.createNewUserDetails.enterOTP == '' || this.userManagementService.createNewUserDetails.enterOTP == undefined) {
      $('html,body').animate({
        scrollTop: $('#enterOPTBlockError').offset().top - 250
      }, 'slow');
      return false;
    }
    else if (this.userManagementService.showOTPTickerWrong == true) {
      $('html,body').animate({
        scrollTop: $('#wrongOPTBlockError').offset().top - 250
      }, 'slow');
      return false;
    }
    else if (this.userManagementService.ifDisclaimerNotSelected == true) {
      $('html,body').animate({
        scrollTop: $('#disclaimer').offset().top - 250
      }, 'slow');
      return false;
    } else if(this.userManagementService.createNewUserDetails.password != this.userManagementService.createNewUserDetails.reEnterPassword){
      $('html,body').animate({
        scrollTop: $('#do-not-matched').offset().top - 250
      }, 'slow');
      return false;
    }
    else {
      $("#previewModal").modal("show");
    }
  }

  submitDetails() {
    this.userManagementService.upload().then(e => {
      let filepath = e.body;
      this.userManagementService.dataModel = {
        'firstName': this.userManagementService.createNewUserDetails.firstName,
        'middleName': this.userManagementService.createNewUserDetails.middleName,
        'lastName': this.userManagementService.createNewUserDetails.lastName,
        'gender': this.userManagementService.createNewUserDetails.gender,
        'dob': this.userManagementService.dateOfBirth,
        'mobNo': this.userManagementService.createNewUserDetails.mobileNumber,
        'email': this.userManagementService.createNewUserDetails.emailId,
        'areaLevel': this.userManagementService.createNewUserDetails.selectedRoleId,
        'areaId': this.userManagementService.userLevelID,
        'orgName': this.userManagementService.createNewUserDetails.organization.organizationName,
        'organization': this.userManagementService.selectedOrganizationID,
        'devPartner': this.userManagementService.developmentPartnerID != undefined ? this.userManagementService.developmentPartnerID.trim() : null,
        'othersDevPartner': this.userManagementService.createNewUserDetails.developmentPartnerOthers != undefined ? this.userManagementService.createNewUserDetails.developmentPartnerOthers.trim() : null,
        'desgnName': this.userManagementService.createNewUserDetails.designation.name,
        'designationIds': this.userManagementService.designationID,
        'userName': this.userManagementService.createNewUserDetails.userName,
        'password': this.userManagementService.createNewUserDetails.password,
        'idProType': this.userManagementService.idProofID != undefined ? this.userManagementService.idProofID : null,
        'othersIdProof': this.userManagementService.createNewUserDetails.idProofother != undefined ? this.userManagementService.createNewUserDetails.idProofother.trim() : null,
        'idProofFile': filepath,
        "authority_control_type": "authority",
        "authorityIds": this.userManagementService.selectedAccessLevelID
      }
      this.http.post(Constants.COLLECTION_SERVICE_URL + "createUser",
        this.userManagementService.dataModel).subscribe(response => {
          this.userManagementService.submitSuccessMsg = response;
          $("#previewModal").modal("hide");
          $("#successModal").modal('show');
        },
          error => {
            this.userManagementService.errorMessage = "Some error found."
            $("#errorModal").modal('show');
          });
    })
  }


}
