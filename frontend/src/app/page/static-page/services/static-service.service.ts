import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Constants } from 'src/app/constants';
import { Observable } from 'rxjs';
declare var $;

@Injectable({
  providedIn: 'root'
})
export class StaticServiceService {

  userName: string;
  oldPassword: string;
  newPassword: string;
  reEnterPassword: string;
  passwordMatched: boolean = false;
  passwordDoesNotMatched: boolean = false;
  userDetails: any;
  errorMessage: string;
  successMsg: any;
  dob: any;
  getUserDetails: any;
  emailId: any;
  afterEmailVerified: boolean = false;
  enterOTPForValidate: boolean = false;
  showIDProofOtherBlock: boolean = false;
  showIfGenderNotSelected: boolean = false;
  successOtpValidate: any;
  wrongOtp: string;
  showOTPTickerSuccess: boolean = false;
  showOTPTickerWrong: boolean = false;
  showPassSection: boolean = false;
  showErrorIfOTPnotVerified: boolean;
  otpSuccessMsg: any;
  otpErrorMsg: any;
  enterOTP: any;
  enterNewPass: any;
  reEnterPass: any;
  otpSendMsg: boolean = false;
  otpErrMsg: boolean = false;
  ForgotPasswordModel: any = {};
  formId: number;
  uniqueId: string;
  isRejected: Boolean;
  p: number = 1;


  // submission models
  reportForms: any;
  reviewDetails: any;
  seletedSubmissionDetails: any;
  submissionId: number;
  rejectDetails: any;
  viewMoreClicked: boolean = false;
  viewSupervisorDetails: boolean = false;
  validationMsg: any;
  selectedSupervisorSubmissions: any;
  supervisorFormId: any;

  constructor(private http: HttpClient) { }

  getReportData() {
    return this.http.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'api/report/getReportData');
  }
  getRegisteredUserData() {
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'oauth/user');
  }

  /**
   * Get qualitative form questions  
   */
  getQualitativeFormQuestions() {
    return this.http.get('assets/qualitativeForm.json');   
  }
  /**
   * Get qualitative form datas for supervisor
   */
  getQualitativeFormData() {
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/getQualitativeData');
  }  
  /**
   * Get qualitative supervisor form datas on DDM view
   */
  getQualitativeSupervisorDataOnDDM() {
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/getQualitativesDatas');
  }  

  /**
   * Get DDM uploaded file view in DDM login
   */
  getQualitativeDDMDataView() {
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/getDDMQualitativeData');
  } 
  /**
   * Get all report forms
   */
  getAllReportForms() {
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'getReportForms');
  }
  /**
   * Get all Submission data
   * @param formId 
   */
  getAllSubmissionDatas(formId) {
    //return this.httpClient.get('assets/json/review.json');
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/getAllFormDataHead?formId=' + formId );
  }
  /**
   * Get all review forms
   */
  getAllReviewForms() {
    return this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/getAllReviewForms');
  }
  /**
   * Set view more data for each submission
   * @param details 
   */
  setviewMoreData(details) {
    this.seletedSubmissionDetails = details;
    this.formId = this.seletedSubmissionDetails.tableRow.formId;
    this.submissionId = this.seletedSubmissionDetails.tableRow.extraKeys.submissionId;
    this.uniqueId = this.seletedSubmissionDetails.tableRow.uniqueId;
    this.viewMoreClicked = true;
    $("#submissionDetailsModal").modal('show');
  }
  /**
   * Get view more data for each submission
   * 
   */
  getviewMoreData() {
    // return this.seletedSubmissionDetails;
     return this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/reviewViewMoreData?formId=' +this.formId+ '&submissionId='+ this.submissionId);
  }
  /**
   * Destroy the open modal data on button close
   */

   // api call for user manual page
   getCMSRequestDataInTable(data): Observable<any> {
    return this.http.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'anynomus/getCMSRequestDataInTable', data)
  }
  getCMSRequestData(data):Observable<any>{
    return this.http.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL  +'anynomus/getCMSRequestData',data)
  }
  // getCMSRequestDataInTable(){
  //   return this.http.get('assets/userManual.json')
  // }
  destroyModalData() {
    this.viewMoreClicked = false;
    this.viewSupervisorDetails = false;
    $("#submissionDetailsModal").modal('hide');
    $("#supervisorDetails").modal('hide');
  }
  /**
   * List submission id/ids for rejection
   * @param details 
   */
  submissionIdsForReject(details) {
    $("#submissionDetailsModal").modal('hide');
    $("#confirmRejectModal").modal('show');
    this.isRejected = true;
    this.rejectDetails = details;
  }
  submissionIdsForApprove(details) {
    $("#submissionDetailsModal").modal('hide');
    $("#confirmApproveModal").modal('show');
    this.isRejected = false;
    this.rejectDetails = details;
  }
  /**
   * Reject the selected submission 
   * @param formId 
   * @param submissionId 
   * @param rejectMessage 
   * @param isRejcted
   */
  rejectSubmission(formId, submissionId, rejectionMessage, isRejcted, form) {
    if (formId) {
      let rejectDetails = {
        "formId": formId,
        "submissionList": submissionId,
        "message": rejectionMessage,
        "isRejected": isRejcted
      }
      this.http.post(Constants.COLLECTION_SERVICE_URL + 'api/rejectMultipleSubmission', rejectDetails).subscribe(data => {
        this.validationMsg = data;
        $("#confirmRejectModal").modal('hide');
        form.resetForm();
        $("#successMatch").modal('show');
      }, err => {
        console.log(err);
        form.resetForm();
        $("#confirmRejectModal").modal('hide');
      });
    }
  }
  // for approve
  rejectSubmissionApprove(formId, submissionId, rejectionMessage, isRejcted) {
    if (formId) {
      let approveDetails = {
        "formId": formId,
        "submissionList": submissionId,
        "message": rejectionMessage,
        "isRejected": isRejcted
      }
      this.http.post(Constants.COLLECTION_SERVICE_URL + 'api/rejectMultipleSubmission', approveDetails).subscribe(data => {
        this.validationMsg = data;
        $("#confirmApproveModal").modal('hide');
        $("#successMatch").modal('show');
      }, err => {
        console.log(err);
        $("#confirmApproveModal").modal('hide');
      });
    }
  }
  /**
   * Set supervisor detailed submissions on each CF submission
   * @param data 
   */
  /**
   * Get supervisor detailed submissions on each CF submission
   */
  getSuperviorDetails() {
    return this.selectedSupervisorSubmissions;
  }
  setSupervisorDetails(data) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + 'api/getSubmissionData?submissionId=' + data.extraKeys.submissionId + '&formId=' + data.formId).subscribe(res => {
      let supervisorData: any = res;
      this.supervisorFormId = Object.keys(supervisorData.reviewDataMap)[0];
      if (supervisorData.reviewDataMap) {
        this.selectedSupervisorSubmissions = supervisorData.reviewDataMap[this.supervisorFormId];
        this.viewSupervisorDetails = true;
        if (this.selectedSupervisorSubmissions)
          $("#supervisorDetails").modal('show');
      } else {
        this.viewSupervisorDetails = true;
        $("#supervisorDetails").modal('show');
      }
    });
  }
  /**
   * Download the report excel
   * @param data 
   */
  download(data) {
    if (data) {
      //data can be string of parameters or array/object
      data = typeof data == 'string' ? data : $.param(data);
      //split params into form inputs
      let inputs = '';
      let url = Constants.COLLECTION_SERVICE_URL + 'downloadReport';
      $.each(data.split('&'), function () {
        let pair = this.split('=');
        inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
      });
      //send request
      $('<form action="' + url + '" method="post">' + inputs + '</form>')
        .appendTo('body').submit().remove();
    };
  }

// pdf download
  downloadFile(data) {
    return this.http.get( Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'anynomus/downloadFile?fileName='+data, { responseType: "blob" })
  }
  getData(data) {
    this.userDetails = data;
  }
}
