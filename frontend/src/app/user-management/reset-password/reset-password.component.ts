import { Component, OnInit } from '@angular/core';
import { FormGroup, NgForm } from '@angular/forms';
import { UserManagementService } from '../services/user-management.service';
import { HttpClient } from '@angular/common/http';
import { Router, RoutesRecognized } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { filter } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
declare var $: any;

@Component({
  selector: 'rmncha-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  form: FormGroup;
  formFields: any;
  formFieldsAll: any;
  payLoad = '';
  areaDetails: any;  

  newPassword: string;
  confirmPassword: string;
  userId: any;
  validationMsg: any;
  user: any;
  disableUserId: number;
 
  userManagementService: UserManagementService;

  constructor(private http: HttpClient, private userManagementProvider: UserManagementService, private router: Router, private spinner: NgxSpinnerService,) {
    this.userManagementService = userManagementProvider;
   }

  ngOnInit() {
    this.router.events
    .pipe(filter((e: any) => e instanceof RoutesRecognized))
    .subscribe((e: any) => {
        if(this.router.url =="/reset-password" && e.url != '/edit-user' ){
          this.userManagementService.resetPasswordDetails ={};
        }
    });
    this.userManagementService.e = 1;
    if(!this.userManagementService.formFieldsAll)
      this.userManagementService.getUserRoles().subscribe(data=>{
        this.userManagementService.formFieldsAll = data;      
      }) 
    if(!this.userManagementService.areaDetails)   
      this.userManagementService.getAreaDetails().subscribe(data=>{
        this.userManagementService.areaDetails = data;     
      })      
    // if(!this.userManagementService.typeDetails) 
    //   this.userManagementService.getTypeDetails().subscribe(data=>{
    //       this.userManagementService.typeDetails = data;      
    //   })                
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
    if(this.userManagementService.resetPasswordDetails.selectedRoleId)
    this.getUsers();   
  }

 getUsers(){
    let areaId =  this.userManagementService.resetPasswordDetails.selectedBlockId ? this.userManagementService.resetPasswordDetails.selectedBlockId: this.userManagementService.resetPasswordDetails.selectedDistrictId ? this.userManagementService.resetPasswordDetails.selectedDistrictId: this.userManagementService.resetPasswordDetails.selectedStateId ? this.userManagementService.resetPasswordDetails.selectedStateId: 1;
    let area = this.userManagementService.getElementBySbmId(this.userManagementService.areaDetails, areaId);
    if(this.userManagementService.resetPasswordDetails.selectedRoleId == 1){
      this.userManagementService.getUsersByRoleIdAreaId(this.userManagementService.resetPasswordDetails.selectedRoleId, '').subscribe(res => {
        this.userManagementService.resetPasswordDetails.allUser  = res;
      })
    }else{
      this.userManagementService.getUsersByRoleIdAreaId(this.userManagementService.resetPasswordDetails.selectedRoleId, area.areaId).subscribe(res => {
        this.userManagementService.resetPasswordDetails.allUser  = res;
      })
    }
    
 }
 resetModal(user){
   $("#resetPassModal").modal('show');
  this.user = user;
 }
 resetBox(user){
  this.newPassword = "";
  this.confirmPassword = "";
 }
 submitModal(form:NgForm){   
  let passDetails = {
    'userId' : this.user.userId,
    'newPassword': this.newPassword
  };

 if(this.newPassword === this.confirmPassword) {
  this.spinner.show();
    this.http.post(Constants.COLLECTION_SERVICE_URL + 'resetPassword', passDetails, { responseType: 'text' }).subscribe((data)=>{  
        $("#resetPassModal").modal('hide');
        $("#successMatch").modal('show');
        this.newPassword = "";
        this.confirmPassword = "";
        this.userManagementService.resetPasswordDetails.allUser = undefined;      
    }, err=>{
      $("#oldPassNotMatch").modal('show');
      this.validationMsg ="Error occurred";
    });
  }
}
editUserDetails(data){
  this.userManagementService.editUserDetails = data;
  this.router.navigateByUrl("edit-user");
}
enableUser(id){
  this.http.get(Constants.COLLECTION_SERVICE_URL + 'enableUser?userId='+id).subscribe((data)=>{
    $("#successModal").modal('show'); 
    this.validationMsg = data;
  }, err=>{ 
    this.userManagementService.errorMessage = "Some error found !";
    $("#errorModal").modal('show');      
  }); 
}
disableUser(id){
  this.disableUserId = id;
  $("#disableUserModal").modal('show');
}
disableUserDetails(id){
  this.http.get(Constants.COLLECTION_SERVICE_URL +'disableUser?userId='+id).subscribe((data)=>{   
   $("#disableUserModal").modal('hide');
   $("#successModal").modal('show'); 
     this.validationMsg = data;        
   }, err=>{      
     $("#disableUserModal").modal('hide');     
   }); 
}
userStatus(){
  $("#successModal").modal('hide'); 
  this.getUsers();
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
