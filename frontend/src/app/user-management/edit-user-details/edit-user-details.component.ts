import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { UserManagementService } from '../services/user-management.service';
import { HttpClient } from '@angular/common/http';
import { NgxSpinnerService } from 'ngx-spinner';
import { Router, RoutesRecognized } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
import { DatePipe } from '@angular/common';
import { DateAdapter, MAT_DATE_FORMATS } from '@angular/material';
import { DateFormat, APP_DATE_FORMATS } from 'src/app/date-format';
import { formatDate } from "@angular/common";
declare var $: any;
import * as _moment from 'moment';
import {default as _rollupMoment, Moment} from 'moment';
const moment = _rollupMoment || _moment;


@Component({
  selector: 'rmncha-edit-user-details',
  templateUrl: './edit-user-details.component.html',
  styleUrls: ['./edit-user-details.component.scss'],
  providers: [
    {
      provide: DateAdapter, useClass: DateFormat
    },
    {
      provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS
    }
  ]
})
export class EditUserDetailsComponent implements OnInit {
  @ViewChild('fileInput') fileInput;
  private target: any;
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
  selectedtypeDetailsId: number;
  minDate = new Date(1950, 0, 1);
  ifChecked: boolean;
  ifNotChecked: boolean;
  newDate:any;
  date = new FormControl(moment());
  maxDate = new Date(Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]), Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]) - 1, Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]));

  constructor(private datepipe: DatePipe, private userManagementServiceProvider: UserManagementService,
    private http: HttpClient, private router: Router) {
    this.userManagementService = userManagementServiceProvider;
  }

  ngOnInit() {
    this.userManagementService.file = [];
    this.userManagementService.uploadErrorMsg = "";
    this.userManagementService.uploadIdentityProof == undefined;
    this.userManagementService.fetchedUploadIdentityProof = "";
    this.userManagementService.designationID = [];
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.accessLevelNames = [];
    this.userManagementService.devPartnerName = "";
    this.userManagementService.fetchedDesignationId = [];
    this.router.events.pipe(filter((e: any) => e instanceof RoutesRecognized)).subscribe((e: any) => {
      if (this.router.url == "/edit-user" && e.url != '/reset-password') {
        this.userManagementService.resetPasswordDetails = {};
      }
    });
    if (!this.userManagementService.editUserDetails) {
      this.router.navigateByUrl("reset-password");
    }
    this.userManagementService.userLevelIDFetched = [];
    if (this.userManagementService.editUserDetails && this.userManagementService.editUserDetails.roleId == 1) {
      this.userManagementService.userLevelName = this.userManagementService.editUserDetails.areaLevel;
      this.userManagementService.userLevelIDFetched.push(this.userManagementService.editUserDetails.roleId);
    }
    if (this.userManagementService.editUserDetails && this.userManagementService.editUserDetails.roleId == 2) {
      this.selectedStateId = this.userManagementService.editUserDetails.areas.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
      this.userManagementService.userLevelName = this.userManagementService.editUserDetails.areaLevel;
      this.userManagementService.selectedStateName = this.userManagementService.editUserDetails.areas[0].areaName;
      this.userManagementService.userLevelIDFetched.push(this.selectedStateId);
    }
    if (this.userManagementService.editUserDetails && this.userManagementService.editUserDetails.roleId == 3) {
      this.selectedStateId = this.userManagementService.editUserDetails.areas.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
      this.selectedDistrictId = this.userManagementService.editUserDetails.areas.filter(d => d.areaLevel.areaLevelId == 3)[0].areaId;
      this.userManagementService.userLevelName = this.userManagementService.editUserDetails.areaLevel;
      this.userManagementService.selectedStateName = this.userManagementService.editUserDetails.areas[0].areaName;
      this.userManagementService.selectedDistrictName = this.userManagementService.editUserDetails.areas[1].areaName;
      this.userManagementService.userLevelIDFetched.push(this.selectedStateId);
      this.userManagementService.userLevelIDFetched.push(this.selectedDistrictId);
    }
    if (this.userManagementService.editUserDetails && this.userManagementService.editUserDetails.roleId == 4) {
      this.selectedStateId = this.userManagementService.editUserDetails.areas.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
      this.selectedDistrictId = this.userManagementService.editUserDetails.areas.filter(d => d.areaLevel.areaLevelId == 3)[0].areaId;
      this.selectedBlockId = this.userManagementService.editUserDetails.areas.filter(d => d.areaLevel.areaLevelId == 4)[0].areaId;
      this.userManagementService.userLevelName = this.userManagementService.editUserDetails.areaLevel;
      this.userManagementService.selectedStateName = this.userManagementService.editUserDetails.areas[0].areaName;
      this.userManagementService.selectedDistrictName = this.userManagementService.editUserDetails.areas[1].areaName;
      this.userManagementService.selectedBlockName = this.userManagementService.editUserDetails.areas[2].areaName;
      this.userManagementService.userLevelIDFetched.push(this.selectedStateId);
      this.userManagementService.userLevelIDFetched.push(this.selectedDistrictId);
      this.userManagementService.userLevelIDFetched.push(this.selectedBlockId);
    }

    if (this.userManagementService.editUserDetails) {
      this.userManagementService.organizationName = this.userManagementService.editUserDetails.orgName;
      this.userManagementService.designationName = this.userManagementService.editUserDetails.desgnName;
      this.userManagementService.devPartnerName = this.userManagementService.editUserDetails.devPartner;
      // this.userManagementService.accessLevelNames = this.userManagementService.editUserDetails.authorityNames;
      this.userManagementService.idProofTypeName = this.userManagementService.editUserDetails.idProTypeName;
      this.userManagementService.fetchedDesignationId.push(this.userManagementService.editUserDetails.designation);
      this.userManagementService.fetchedEmailId = this.userManagementService.editUserDetails.emailId;
      let momentDate = moment(this.userManagementService.editUserDetails.dob,  "DD/MM/YYYY");
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
      this.userManagementService.editUserDetails.dob  = this.newDate;
      this.userManagementService.dateOfBirth = `${dd}/${mm}/${yy}`;
    }

    if (this.userManagementService.editUserDetails.organization == 5) {
      this.userManagementService.showDevelopmentField = true;
    } else {
      this.userManagementService.showDevelopmentField = false;
    }

    if (this.userManagementService.editUserDetails.devPartner == "Others (specify)") {
      this.userManagementService.showDevelopmentPartnerOthers = true;
    } else {
      this.userManagementService.showDevelopmentPartnerOthers = false;
    }

    if (this.userManagementService.editUserDetails.idProTypeName == "Others (specify)") {
      this.userManagementService.showIDProofOtherBlock = true;
    } else {
      this.userManagementService.showIDProofOtherBlock = false;
    }

    if (this.userManagementService.editUserDetails.idProType != null && (this.userManagementService.editUserDetails.idProofFile != "" && this.userManagementService.editUserDetails.idProofFile != null)) {
      this.userManagementService.showUploadIdProofBlock = true;
      this.userManagementService.fetchedUploadIdentityProof = this.userManagementService.editUserDetails.idProofFile.substring(this.userManagementService.editUserDetails.idProofFile.lastIndexOf('/') + 1);
    }
    if (this.userManagementService.editUserDetails.idProType != null && (this.userManagementService.editUserDetails.idProofFile != null && this.userManagementService.editUserDetails.idProofFile != "")) {
      this.userManagementService.showUploadIdProofBlock = true;
      this.userManagementService.fetchedUploadIdentityProof = this.userManagementService.editUserDetails.idProofFile.substring(this.userManagementService.editUserDetails.idProofFile.lastIndexOf('/') + 1);
    } else {
      this.userManagementService.showUploadIdProofBlock = false;
    }
    if ((window.innerWidth) <= 767) {
      $(".left-list").attr("style", "display: none !important");
      $('.mob-left-list').attr("style", "display: block !important");
    }


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
        for (let j = 0; j < this.userManagementService.editUserDetails.authorities.length; j++) {
          if (this.userManagementService.editUserDetails.authorities[j] == this.userManagementService.accessLevelLists[i].id) {
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
    if ((window.innerWidth) <= 767) {
      $(".left-list").attr("style", "display: none !important");
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }

  uploadClicked() {
    $("#fileUpload").click();
  }

  // emailVerify() {
  //   var emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
  //   if (emailPattern.test(this.userManagementService.editUserDetails.emailId)) {
  //   }
  // }


  // duplicate email verification
  duplicateEmailVerification(email) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/userEmailAvailablity?email="
      + email).subscribe((data) => {
        this.userManagementService.checkDuplicateEmail = Boolean(data);
        if (this.userManagementService.fetchedEmailId == this.userManagementService.editUserDetails.emailId) {
          this.userManagementService.checkDuplicateEmail = false;
        }
      });
  }

/* selection of date */
  onChangeEvent(e): void {
    this.userManagementService.editUserDetails.dob = e.target.value;
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

/* area level selection*/
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


  // selecting organization
  selectedOrganization(organizationsIds, organizationLists) {
    this.userManagementService.organizationName = organizationLists.filter(d => d.organizationId == organizationsIds)[0].organizationName;
    if (this.userManagementService.organizationName == "Development Partners") {
      this.userManagementService.showDevelopmentField = true;
      this.userManagementService.editUserDetails.devPartner = undefined;
    }
    else {
      this.userManagementService.showDevelopmentField = false;
      this.userManagementService.showDevelopmentPartnerOthers = false;
    }
  }

  // selecting development partners
  selectdevelopmentPartners(developmentPartner, developmentPartnerID) {
    this.userManagementService.devPartnerName = developmentPartner.filter(d => d.id == developmentPartnerID)[0].name;
    if (this.userManagementService.devPartnerName == "Others (specify)") {
      this.userManagementService.showDevelopmentPartnerOthers = true;
      this.userManagementService.editUserDetails.othersDevPartner = undefined;
    } else {
      this.userManagementService.showDevelopmentPartnerOthers = false;
    }
  }

  uncheckIDProof(event){
    if (event.isUserInput == true) {
      this.userManagementService.uploadIdentityProof = undefined;
      this.userManagementService.showUploadIdProofBlock = false;
      this.userManagementService.showIDProofOtherBlock = false;
      this.userManagementService.editUserDetails.idProTypeName = undefined;
      this.userManagementService.editUserDetails.idProofFile = undefined;
    }
  }
  selectIDProof(option,event) {
    if (event.isUserInput == true) {
    this.userManagementService.showUploadIdProofBlock = true;
    this.userManagementService.idProofID = option.id;
    this.userManagementService.idProofTypeName = option.name;

    // idProofLists.forEach(element => {
      if (option.name == "Others (specify)") {
        this.userManagementService.showIDProofOtherBlock = true;
        return false;
      }
      else {
        this.userManagementService.showIDProofOtherBlock = false;
        return false;
      }
    // });

    }
  }

  // designation section
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
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/addDesignation?designation="
      + designation, { responseType: 'text' }).subscribe((data) => {
        this.userManagementService.getAllDesignations().subscribe(data => {
          this.userManagementService.getData(data);
          this.userManagementService.designationLists = this.userManagementService.allData;
        });
        $("#addNew").modal("hide");
      }, err => {
        console.log(err);
      });
  }

  // Identification information checking duplicate user name
  checkDuplicateUserName(userName) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/usernameAvailablity?username="
      + userName).subscribe((data) => {
        this.userManagementService.checkDuplicateUser = JSON.stringify(data);
      });
  }


  resetAllField() {
    this.userManagementService.showIDProofOtherBlock = false;
    this.userManagementService.showDevelopmentPartnerOthers = false;
    this.userManagementService.file = [];
    this.userManagementService.fetchedUploadIdentityProof = "";
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.editUserDetails.dob = undefined;
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showIfGenderNotSelected = true;
    this.userManagementService.afterEmailVerified = false;
    this.userManagementService.showUploadIdProofBlock = false;
  }
  redirectToMain() {
    this.router.navigateByUrl("reset-password");
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

  /* file upload */
  onFileChange($event) {
    if ($event.srcElement.files.length == 0) {
      this.userManagementService.uploadIdentityProof = undefined;
      this.userManagementService.file.length = 0;
      this.currentFileUpload = false;
    } else {
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
          this.currentFileUpload = true;
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

  submit() {
    if (this.userManagementService.showUploadIdProofBlock == true && this.userManagementService.uploadIdentityProof == undefined
      && (this.userManagementService.editUserDetails.idProofFile == "" && this.userManagementService.editUserDetails.idProofFile != null)) {
      this.currentFileUpload = true;
      this.userManagementService.showUploadFileError = true;
      this.userManagementService.uploadErrorMsg = "Please upload file";
      return false;
    }
    if (this.userManagementService.showUploadIdProofBlock == true && this.userManagementService.uploadIdentityProof == undefined
      && (this.userManagementService.editUserDetails.idProofFile == null && this.userManagementService.editUserDetails.idProofFile != "")) {
      this.currentFileUpload = true;
      this.userManagementService.showUploadFileError = true;
      this.userManagementService.uploadErrorMsg = "Please upload file";
      return false;
    }
  }
   /* preview form before submitting */
  previewForm() {
    if (this.userManagementService.checkDuplicateEmail == true) {
      $('html,body').animate({
        scrollTop: $('.user-name-duplicate').offset().top - 250
      }, 'slow');
      return false;
    }
    else {
      $("#previewModal").modal("show");
    }
  }
  updateUserDetails(editUserDetails) {
    this.userManagementService.upload().then(e => {
      let filepath = e.body;
      let finalFilePath;
      if (filepath == "") {
        finalFilePath = this.userManagementService.editUserDetails.idProofFile;
      } else {
        finalFilePath = filepath;
      }
      this.userManagementService.editUserDetails['idProofFile'] = finalFilePath;
      this.userManagementService.editUserDetails['dob'] = this.userManagementService.dateOfBirth;
      this.userManagementService.editUserDetails['areaId'] = this.userManagementService.userLevelID.length == 0 ? this.userManagementService.userLevelIDFetched : this.userManagementService.userLevelID;
      this.userManagementService.editUserDetails['authorityIds'] = this.userManagementService.selectedAccessLevelID.length == 0 ? this.userManagementService.editUserDetails.authorities : this.userManagementService.selectedAccessLevelID;
      this.userManagementService.editUserDetails['areaLevel'] = this.userManagementService.editUserDetails.roleId;
      this.userManagementService.editUserDetails['designation'] = this.userManagementService.designationID.length == 0 ? this.userManagementService.fetchedDesignationId : this.userManagementService.designationID;
      this.userManagementService.editUserDetails['authority_control_type'] = "authority";
      this.userManagementService.editUserDetails['id'] = this.userManagementService.editUserDetails.userId;
      this.userManagementService.editUserDetails['desgnName'] = this.userManagementService.designationName;
      this.userManagementService.editUserDetails['orgName'] = this.userManagementService.organizationName;
      this.userManagementService.editUserDetails['devPartner'] = this.userManagementService.editUserDetails.devPartnerId;

      this.userManagementService.dataModel = this.userManagementService.editUserDetails;
      this.http.post(Constants.COLLECTION_SERVICE_URL + "updateUser",
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
  showLists() {
    $(".left-list").attr("style", "display: block !important");
    $('.mob-left-list').attr("style", "display: none !important");
  }
  ngAfterViewInit() {
    $("input, textarea, .select-dropdown").focus(function () {
      $(this).closest(".input-holder").parent().find("> label").css({ "color": "#4285F4" })

    })
    $("input, textarea, .select-dropdown").blur(function () {
      $(this).closest(".input-holder").parent().find("> label").css({ "color": "#333" })
    })
    $('body,html').click(function (e) {
      if ((window.innerWidth) <= 767) {
        if (e.target.className == "mob-left-list") {
          return;
        } else {
          $(".left-list").attr("style", "display: none !important");
          $('.mob-left-list').attr("style", "display: block !important");
        }
      }
    });
  }

}
