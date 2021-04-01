import { Component, OnInit } from '@angular/core';
import { StaticServiceService } from '../services/static-service.service';
import { HttpClient } from '@angular/common/http'; 
import { Constants } from 'src/app/constants';
declare var $: any;

@Component({
  selector: 'rmncha-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
  staticService: StaticServiceService;
  constructor(private staticServiceprovider: StaticServiceService,private http: HttpClient) {
    this.staticService = staticServiceprovider;
   }

  ngOnInit() {
    
  }

  //  email format test
  emailVerify() {
    let emailPattern = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
    if (emailPattern.test(this.staticService.emailId)) {
      this.staticService.afterEmailVerified = true;
      this.staticService.showErrorIfOTPnotVerified = true;
      this.staticService.enterOTPForValidate = false;
    } else {
      this.staticService.afterEmailVerified = false;
      this.staticService.showErrorIfOTPnotVerified = false;
    }
  }

  sendOTP(email) {
    if(email != undefined || email != ""){
      this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/sendOtp?userName="
      + this.staticService.emailId, { responseType: 'json' }).subscribe(respone => {
        this.staticService.otpSendMsg = true;
        this.staticService.otpErrMsg = false;
        this.staticService.otpErrorMsg = "";
        this.staticService.enterOTPForValidate = true;
        this.staticService.showErrorIfOTPnotVerified = false;
        this.staticService.otpSuccessMsg = respone;
        this.staticService.showOTPTickerWrong = false;
        this.staticService.enterOTP = undefined;
      }, err => {
        this.staticService.otpSendMsg = false;
        this.staticService.otpErrMsg = true;
        this.staticService.otpSuccessMsg = "";
        this.staticService.afterEmailVerified = false;
        this.staticService.otpErrorMsg = err.error;
      });
    }else{
      this.staticService.otpErrMsg = true;
      this.staticService.otpSuccessMsg = "Please provide Email ID.";
    }
  }

  // validate otp
 validateOTP(enteredOTP) {
  if (enteredOTP != undefined && enteredOTP != null) {
    this.http.get(Constants.COLLECTION_SERVICE_URL + "bypass/validateOtp?userName="
      + this.staticService.emailId + "&otp=" + enteredOTP, {observe: 'response'},
    ).subscribe((response) => {
      if (response.status == 200) {
        this.staticService.showOTPTickerSuccess = true;
        this.staticService.successOtpValidate = response.body;
        setTimeout(() => {
          this.staticService.showOTPTickerSuccess = false;
          $('#emailverifiedmsgDiv').fadeIn('fast').delay(4000).fadeOut('fast');
        }, 4000);
        this.staticService.afterEmailVerified = false;
        // this.app.enterOTPForValidate = false;
        this.staticService.showOTPTickerWrong = false;
        this.staticService.showPassSection = true;
        this.staticService.otpSendMsg = false;
        this.staticService.enterOTPForValidate = false;
      }
    }
      ,error => {
        this.staticService.wrongOtp = error.error;
        this.staticService.showOTPTickerWrong = true;
        this.staticService.showOTPTickerSuccess = false;
        this.staticService.showPassSection = false;
        this.staticService.otpSendMsg = true;
      }
    );
  }
}

reset(){
  this.staticService.emailId = undefined;
  this.staticService.enterOTP = undefined;
  this.staticService.enterNewPass = undefined;
  this.staticService.reEnterPass = undefined;
  this.staticService.enterOTPForValidate = false;
  this.staticService.showPassSection = false;
  this.staticService.otpErrMsg = false;
  this.staticService.otpErrorMsg = "";
}
   // hide otp section
   hideOtpSection(){
    this.staticService.enterOTPForValidate = false;
  }
  savePass(){
    this.staticService.ForgotPasswordModel  = {
      'emailId': this.staticService.emailId,
      'otp': this.staticService.enterOTP,
      'newPassword': this.staticService.enterNewPass,
      'confirmPassword': this.staticService.reEnterPass
    }
    this.http.post(Constants.COLLECTION_SERVICE_URL + "bypass/forgotPassword",
    this.staticService.ForgotPasswordModel,{responseType: 'json'} ).subscribe(response => {
      this.staticService.successMsg = response;
      $("#modalRegisterForm").modal("hide");
      this.reset();
      $("#successModal").modal('show');
    },
      error => {
        this.staticService.errorMessage = error.error;
        $("#modalRegisterForm").modal("hide");
        $("#errorModal").modal('show');
      });
  }

}
