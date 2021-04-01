import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpHeaders } from '@angular/common/http';
import { Constants } from 'src/app/constants';
import { Observable } from 'rxjs';
import { RequestModel } from 'src/app/models/request-model';
import { formatDate } from "@angular/common";


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
@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  allData: any;
  formFieldsAll: any;
  typeDetails: any;
  areaDetails: any;
  allTypes: any;
  editUserDetails: any;
  resetPasswordDetails: any = {};
  userDetails: any;
  pendingUsers: any[] = [];
  approveUsers: any[] = [];
  rejectedUsers: any[] = [];
  removeColumnList: any = [];  
  dateOfBirth: any;

  // models for update user profile
  updateUserDetails: any = {};
  isAdmin: string;

// models for user management 
ifDisclaimerNotSelected: boolean = false;
hideBtnsIfRejectedusers: boolean = true;
showpPendingUsersPagination:boolean = true;
showpAcceptedUsersPagination:boolean = false;
showpRejectedUsersPagination: boolean = false;
searchTexts: any;
p: number = 1;
a: number = 1;
r: number = 1;
e: number = 1;
selectForRejectionOrApproval: any;
selectedUserList: any = [];
selectedUserModelList: any = [];
aprroveVariable: boolean;
infoMsg: string;
successApprovedOrreject: any;
rejectionReason: any[] = [];
selectedStateName: string;
selectedDistrictName: string;
selectedBlockName: string;
userLevelName: string;
organizationName: string;
designationName: string;
fetchedDesignationId: any[] = [];
accessLevelNames: any[] = [];
fetchedDevPartnerId: string;
selectedDevPartnerId: string;
idProofTypeName: any;
reasonTexts: any[] = [];
devPartnerName: any;
accessLevelName: string;
fetchedEmailId: string;
adminCreateNewUserDetails = {};
noData: boolean = false;

// models for new user registration
firstName: any;
middleName: any;
lastName: any;
gender: any;
dob: any;
mobileNumber: number;
emailId: string;
otpSuccessMsg: string;
otpErrorMsg: string;
enterOTP: any;
userLevel: any;
userLevelID: number[] = [];
userLevelIDFetched: Number[] = [];
createNewUserDetails: any = {}
stateLevel: any;
districtLevel: any;
blockLevel: any;
organization: any;
selectedOrganizationID: number;
develelopmentPartners: any;
developmentPartnerID: any;
designation: any;
designationAdd: any;
designationID: number[] = [];
accessLevel: any;
userName: any;
password: any;
reEnterPassword: any;
passwordMatched: boolean = false;
passwordDoesNotMatched: boolean =  false;
idProof: any;
idProofother: any;
levelSearch: any;
uploadIdentityProof: any;
developmentPartnerOthers: any;
userlevelDetails: any = {};
showStateField: boolean = false;
showDistrictField: boolean = false;
showBlockField: boolean =  false;
showDevelopmentField: boolean = false;
showDevelopmentPartnerOthers = false;
afterEmailVerified: boolean = false;
enterOTPForValidate: boolean =  false;
showIDProofOtherBlock: boolean =  false;
showIfGenderNotSelected: boolean = false;
showOTPTickerSuccess: boolean = false;
showOTPTickerWrong: boolean = false;
showUploadFileError: boolean =  false;
showErrorIfOTPnotVerified: boolean;
showUploadIdProofBlock: boolean =  false;
fetchedUploadIdentityProof: any;
uploadErrorMsg: string;
checkDuplicateUser: any;
checkDuplicateEmail: boolean;
designationLists: any = [];
organizationLists: any = [];
developmentPartnerLists: any = [];
allLevels: any;
stateLevelLists: any = [];
districtLevelLists: any = [];
blockLevelLists: any = [];
idProofLists: any = [];
idProofID: string;
userLevelLists: any;
accessLevelLists: any;
accessLevelID: any = [];
selectedAccessLevelName:string[]=[];
selectedAccessLevelID: any[] = [];
errorMessage: any;
selectedFileName: any;
emailIDisVerified: string;
statusVerified: any;
disclaimer: any ={};
file: any;
dataModel: any = {};
allLocation: any[] = [];
reasonTextLengthInNumber: number;
reasonTextLength: number = 200;
submitSuccessMsg: any;

genderData = [
  { label: 'Male', value: 'MALE',"checked": true,"id":1 },
  { label: 'Female', value: 'FEMALE',"checked": false,id: 2 }
];

  constructor(private httpClient: HttpClient) { 
 
  }

  // get update data for registered user
  getRegisteredUserData(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'oauth/user');
  }

  getUserRoles() {
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getAreaLevels');
  }
  
  getTypes() {
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'getTypes');
  }
  getTypeDetails(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getTypeDetails');   
  }
  getUsersByRoleIdAreaId(roleId, areaId) {
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'getUserRoleArea?roleId=' + roleId + '&areaId=' + areaId)
  }

  // get pending,approved and rejected users
  getUserDetails() {
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'getAllUsers');
  }

  // get all designations for new user management
  getAllDesignations(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'getAllDesignations');
  }

  // get all Organizations for new user management
  getAllOrganizations(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getAllOrganization');
  }

  // get all Development Partners for new user management
  getAllDevelopmentPartners(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getTypeDetails?typeName=Development Partner');
  } 

  // get all Area Levels for new user management
  getAllAreaLevels(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getAreaLevels');
  }

  // get all Authorities for new user management
  getAllAuthorities(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'getAllAuthorities');
  }

  // get all Area for new user management
  getAreaDetails(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getAllArea');   
  }

  // get ID proof details for new user management
  getIDProofDetails(){
    return this.httpClient.get(Constants.COLLECTION_SERVICE_URL + 'bypass/getTypeDetails?typeName=IdProof');   
  }

// bulk registration services

downloadTemplateFile() {
  return this.httpClient.post(Constants.COLLECTION_SERVICE_URL + 'downloadTemplate', '', {
    responseType: 'blob',
  });
}

uploadBulkFile(file: File): Observable<HttpEvent<{}>> {
  const formdata: FormData = new FormData();
  formdata.append('templateFile', file);
  const req = new HttpRequest('POST', Constants.FILE_UPLOAD_URL + 'uploadTemplate', formdata, {
    reportProgress: true
  });
  return this.httpClient.request(req);
}

// end bulk registration common services



  getKeys(obj){
    return Object.keys(obj)
  }
  getData(data){
    this.removeColumnList = [];
    this.allData = data;
    // this.userDetails = data;
    this.pendingUsers = this.allData.Pending;
    this.approveUsers = this.allData.Approved;
    this.rejectedUsers = this.allData.Rejected;
  }


  // uploading fies
  upload(): Promise<any> {
    let fileObject: any = {};
    return this.uploadIDProofFile(this.file).toPromise();
  }

  uploadIDProofFile(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();
    if(file.name){
      formdata.append("uploadIdProofFile", file);
    }
    
    const req = new HttpRequest("POST", Constants.FILE_UPLOAD_URL + 'bypass/uploadFile', formdata, {
      reportProgress: true,
      responseType: "text",
      headers: new HttpHeaders ({ //zuul/ and chunk header added to avoid multipart processing of large files using zuul api gateway
          'Transfer-Encoding': 'chunked'
        })
      
    });
    return this.httpClient.request(req);
  }

  getElementBySbmId(areaDetails, areaId) {
    let areaJson;
    for (let i = 0; i < Object.keys(areaDetails).length; i++) {
      const key = Object.keys(areaDetails)[i];;
      areaDetails[key].forEach(element => {
        if (element.areaId == areaId) {
          areaJson = element;
        }
      });
    }
    return areaJson;
  }
}
