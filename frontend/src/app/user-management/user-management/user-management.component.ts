import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { UserManagementService } from '../services/user-management.service';
import { HttpClient } from '@angular/common/http';
import { Constants } from 'src/app/constants';
import { DatePipe } from '@angular/common';
import { DateAdapter, MAT_DATE_FORMATS } from '@angular/material';
import { APP_DATE_FORMATS, DateFormat } from 'src/app/date-format';
declare var $ : any;

@Component({
  selector: 'rmncha-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
  providers: [
    {
        provide: DateAdapter, useClass: DateFormat
    },
    {
        provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS
    }
    ]
})
export class UserManagementComponent implements OnInit {
  myControl = new FormControl();
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

  filteredOptions: string[];
  
  minDate = new Date(1950, 0, 1);
  ifChecked: boolean;
  ifNotChecked: boolean;
  maxDate = new Date(Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]), Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]) - 1, Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]));

  constructor(private datepipe: DatePipe, private userManagementServiceProvider: UserManagementService,
    private http: HttpClient) {
    this.userManagementService = userManagementServiceProvider;
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.userManagementService.designationLists.filter(option => option.name.toLowerCase().includes(filterValue));
  }

  ngOnInit() {    
    this.userManagementService.idProofID = undefined;
    this.userManagementService.createNewUserDetails = {};
    this.userManagementService.dob = undefined;
    this.userManagementService.file = [];
    this.userManagementService.checkDuplicateEmail = false;
    this.userManagementService.showUploadIdProofBlock = false;
    this.userManagementService.checkDuplicateUser = false;
    this.userManagementService.showIDProofOtherBlock = false;
    if(!this.userManagementService.formFieldsAll)
      this.userManagementService.getUserRoles().subscribe(data=>{
        this.userManagementService.formFieldsAll = data;      
      });

      if(!this.userManagementService.areaDetails)   
      this.userManagementService.getAreaDetails().subscribe(data=>{
        this.userManagementService.areaDetails = data;     
      }) ;
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

      
      
  
      $(".toggle-password").click(function () {
        $(this).toggleClass("fa-eye fa-eye-slash");
        let input = $($(this).attr("toggle"));
        if (input.attr("type") == "password") {
          input.attr("type", "text");
        } else {
          input.attr("type", "password");
        }
      });
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }
 
  uploadClicked() {
      $("#fileUpload").click();
  }
  emailVerify() {
    this.userManagementService.checkDuplicateEmail = false;
    let emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
    if (emailPattern.test(this.userManagementService.emailId)) {
      this.userManagementService.afterEmailVerified = true;
      this.userManagementService.showErrorIfOTPnotVerified = true;
      this.userManagementService.enterOTPForValidate = false;
    } else {
      this.userManagementService.afterEmailVerified = false;
      this.userManagementService.showErrorIfOTPnotVerified = false;
    }
  }
// duplicate email verification
duplicateEmailVerification(email) {
  if (email != "" && email != undefined) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/userEmailAvailablity?email="
      + email).subscribe(response => {
        this.userManagementService.checkDuplicateEmail = Boolean(response);
        if (this.userManagementService.checkDuplicateEmail == false && (email != undefined || email != "")) {
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
  selectUserLevel(e) {
    this.userManagementService.userLevelID = [];
    if (e == 2) {
      this.userManagementService.userLevelName = this.userManagementService.formFieldsAll.filter(d => d.areaLevelId == e)[0].areaLevelName;
      return false;
    } else if (e == 3) {
      this.userManagementService.userLevelName = this.userManagementService.formFieldsAll.filter(d => d.areaLevelId == e)[0].areaLevelName;
      return false;
    } else if (e == 4) {
      this.userManagementService.userLevelName = this.userManagementService.formFieldsAll.filter(d => d.areaLevelId == e)[0].areaLevelName;
      return false;
    } else {
      this.userManagementService.userLevelName = this.userManagementService.formFieldsAll.filter(d => d.areaLevelId == e)[0].areaLevelName;
      return false;
    }
    }
 
    
  selectStateLevel(selectedStateId) {
    this.userManagementService.userLevelID = [];
    this.selectedStateId = this.userManagementService.areaDetails.STATE.filter(d=>d.areaId==selectedStateId)[0].areaId;
    this.userManagementService.selectedStateName = this.userManagementService.areaDetails.STATE.filter(d=>d.areaId==selectedStateId)[0].areaName;
    this.userManagementService.userLevelID.push(this.selectedStateId);
  }
  selectDistrictLevel(selectedDistrictId) {
    this.selectedDistrictId = this.userManagementService.areaDetails.DISTRICT.filter(d=>d.areaId==selectedDistrictId)[0].areaId;
    this.userManagementService.selectedDistrictName = this.userManagementService.areaDetails.DISTRICT.filter(d=>d.areaId==selectedDistrictId)[0].areaName;
    this.userManagementService.userLevelID.splice(1, 1, this.selectedDistrictId);
  }
  selectBlockLevel(selectedBlockId) {
    this.selectedBlockId =  this.userManagementService.areaDetails.BLOCK.filter(d=>d.areaId==selectedBlockId)[0].areaId;
    this.userManagementService.selectedBlockName = this.userManagementService.areaDetails.BLOCK.filter(d=>d.areaId==selectedBlockId)[0].areaName;
    this.userManagementService.userLevelID.splice(2, 2, this.selectedBlockId);
  }


selectAccessLevels(){
  this.userManagementService.selectedAccessLevelID = [];
  this.userManagementService.selectedAccessLevelName=[];
  this.userManagementService.accessLevel.forEach(element => {
    this.userManagementService.selectedAccessLevelID.push(element.id);
    this.userManagementService.selectedAccessLevelName.push(element.authority);
  });
}

selectedOrganization(e,organizationsLists) {
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
  this.userManagementService.selectedOrganizationID = this.userManagementService.organization.organizationId;
}

selectdevelopmentPartners(e,developmentPartnerLists) {
  developmentPartnerLists.forEach(element => {
    if(e.name == "Others (specify)"){
      this.userManagementService.showDevelopmentPartnerOthers = true;
      return false;
    }else{
      this.userManagementService.showDevelopmentPartnerOthers = false;
      return false;
    }
 });
  this.userManagementService.developmentPartnerID = this.userManagementService.develelopmentPartners.id;
}
uncheckIDProof(event){
  if (event.isUserInput == true) {
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showUploadIdProofBlock = false;
    this.userManagementService.showIDProofOtherBlock = false;
    this.userManagementService.idProofTypeName = undefined;
    this.userManagementService.idProofID = undefined;
  }
}
selectIDProof(e,event) {
  if (event.isUserInput == true) {
  this.userManagementService.showUploadIdProofBlock = true;
  this.userManagementService.idProofID = e.id;
  this.userManagementService.idProofTypeName = e.name
  
  // idProofLists.forEach(element => {
    if (e.name == "Others (specify)") {
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

selectedDesignation() {
  this.userManagementService.designationID = [];
  this.userManagementService.designationID.push(this.userManagementService.designation.id);
  }
  addDesignation() {
    $("#addNew").modal("show");
  }

  // adding new designation
  addNewDesignation(designation) {
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

  // Identification information checking duplicate user name
  checkDuplicateUserName(userName) {
    if(userName != undefined && userName != ""){
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/usernameAvailablity?username="
      + userName).subscribe((data) => {
        this.userManagementService.checkDuplicateUser = Boolean(data);
      });
    } else {
      this.userManagementService.checkDuplicateUser = false;
    }
  }
  

  resetAllField() {
    this.userManagementService.showIDProofOtherBlock = false;
    this.userManagementService.showDevelopmentPartnerOthers = false;
    this.userManagementService.checkDuplicateUser = false;
    this.userManagementService.file = [];
    this.userManagementService.dob = undefined;
    this.userManagementService.uploadIdentityProof = undefined;
    this.userManagementService.showIfGenderNotSelected = true;
    this.userManagementService.afterEmailVerified = false;
    this.userManagementService.showUploadIdProofBlock = false;
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
          (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() ===  'jpg')
          || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() ===  'jpeg')
          || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() ===  'png')
          || (($event.srcElement.files[0].name.split(".")[($event.srcElement.files[0].name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() ===  'pdf')
        ) {
          this.userManagementService.uploadIdentityProof = $event.srcElement.files[0].name;
          this.userManagementService.file = $event.srcElement.files[0];
          this.currentFileUpload = false;
          this.userManagementService.showUploadFileError = false;
        } else {
          this.userManagementService.uploadIdentityProof = undefined;
          this.userManagementService.file = [];
          this.currentFileUpload = true;
          $event.srcElement.value = null;
          this.userManagementService.errorMessage = 'Upload a file in .jpg, .jpeg, .png or in .pdf format';
          $('#errorModal').modal('show');
        }
      }
    }
  }

   //  submitting value
   submitVal() {
    if (this.userManagementService.uploadIdentityProof == undefined && this.userManagementService.file.length == 0) {
      this.currentFileUpload = true;
    }
    else {
      this.currentFileUpload = false;
    }
  }
  
  previewForm() {
    if(this.userManagementService.uploadIdentityProof == undefined && 
      this.userManagementService.showUploadIdProofBlock == true){
      this.currentFileUpload = true;
    }else if (this.userManagementService.checkDuplicateEmail == true) {
      $('html,body').animate({
        scrollTop: $('.user-name-duplicate').offset().top - 250
      }, 'slow');
      return false;
    }else if(this.userManagementService.password != this.userManagementService.reEnterPassword){
      $('html,body').animate({
        scrollTop: $('#do-not-matched').offset().top - 250
      }, 'slow');
      return false;
    }else{
      $("#previewModal").modal("show");
    }
  }
  submitDetails() {
    this.userManagementService.upload().then(e => {
      let filepath = e.body;
      this.userManagementService.dataModel = {
        'firstName': this.userManagementService.firstName,
        'middleName': this.userManagementService.middleName,
        'lastName': this.userManagementService.lastName,
        'gender': this.userManagementService.gender != undefined ? this.userManagementService.gender : null,
        'dob': this.userManagementService.dateOfBirth ? this.userManagementService.dateOfBirth: null,
        'mobNo': this.userManagementService.mobileNumber != undefined ? this.userManagementService.mobileNumber : null,
        'email': this.userManagementService.emailId != undefined ? this.userManagementService.emailId.trim() : null,
        'areaLevel': this.userManagementService.createNewUserDetails.selectedRoleId,
        'areaId': this.userManagementService.userLevelID,
        'orgName': this.userManagementService.organization.organizationName,
        'organization': this.userManagementService.selectedOrganizationID,
        'devPartner': this.userManagementService.developmentPartnerID != undefined ? this.userManagementService.developmentPartnerID.trim() : null,
        'othersDevPartner': this.userManagementService.developmentPartnerOthers!= undefined ? this.userManagementService.developmentPartnerOthers.trim() : null,
        'desgnName': this.userManagementService.designation.name,
        'designationIds' : this.userManagementService.designationID,
        'userName': this.userManagementService.userName,
        'password': this.userManagementService.password,
        'idProType': this.userManagementService.idProofID != undefined? this.userManagementService.idProofID: null,
        'othersIdProof': this.userManagementService.idProofother != undefined ? this.userManagementService.idProofother.trim() : null,
        'idProofFile': filepath,
        "authority_control_type":"authority",
        "authorityIds":this.userManagementService.selectedAccessLevelID
      }
      this.http.post(Constants.COLLECTION_SERVICE_URL+ "createUser",
        this.userManagementService.dataModel).subscribe(response => {
          this.userManagementService.submitSuccessMsg = "Registration successful.";
          $("#previewModal").modal("hide");
          $("#successModal").modal('show');
        },
          error => {
            this.userManagementService.errorMessage = "Some error found."
            $("#errorModal").modal('show');
          });
    })
  }
  showLists(){    
    $(".left-list").attr("style", "display: block !important"); 
    $('.mob-left-list').attr("style", "display: none !important");
    
  }
  ngAfterViewInit(){
    $("input, textarea, .select-dropdown").focus(function() {
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#4285F4"})
      
    })
    $("input, textarea, .select-dropdown").blur(function(){
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#333"})
    })
    $('body,html').click(function(e){   
      if((window.innerWidth)<= 767){
      if(e.target.className == "mob-left-list"){
        return;
      } else{ 
          $(".left-list").attr("style", "display: none !important"); 
          $('.mob-left-list').attr("style", "display: block !important");  
      }
     }
    });   
  }

}
