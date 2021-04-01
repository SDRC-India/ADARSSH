import { Component, OnInit } from '@angular/core';
import { StaticServiceService } from '../services/static-service.service';
import { NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Constants } from 'src/app/constants';
declare var $: any;

@Component({
  selector: 'rmncha-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  staticService: StaticServiceService;
  passwordNotMatched="The passwords do not match."
  constructor(private staticServiceprovider: StaticServiceService,private http: HttpClient) {
    this.staticService = staticServiceprovider;
   }

  ngOnInit() {
    this.staticService.getRegisteredUserData().subscribe(data =>{
      this.staticService.getData(data);
      this.staticService.userName = this.staticService.userDetails.user_name;
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
  reset(){
    this.staticService.oldPassword = undefined;
    this.staticService.newPassword = undefined;
    this.staticService.reEnterPassword = undefined;
  }
  savePass(){   
    let passDetails = {
      'userName' : this.staticService.userDetails.user_name,
      'oldPassword': this.staticService.oldPassword,
      'newPassword' : this.staticService.newPassword,
      'confirmPassword': this.staticService.reEnterPassword,
    };
  
   if(this.staticService.newPassword === this.staticService.reEnterPassword) {
      this.http.post(Constants.COLLECTION_SERVICE_URL + 'changePassword',
       passDetails, { responseType: 'json' }).subscribe(response=>{  
         this.staticService.successMsg = response;
        $("#previewModal").modal("hide");
        $("#successModal").modal('show');
      },
        error => {
          this.staticService.errorMessage = error.error.message;
          $("#errorModal").modal('show');
        });
    }
  }

}
