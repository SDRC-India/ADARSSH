import { Component, OnInit, ViewChild  } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AppService } from '../app.service';
import { Constants } from '../constants';
import { HttpClient } from '@angular/common/http';
import { resetComponentState } from '@angular/core/src/render3/instructions';
declare var $: any;

@Component({
  selector: 'rmncha-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  hide = true;
  credentials: any = {
    username: '',
    password: ''
  }
  nameControl = new FormControl('');
  @ViewChild('resetPass') public resetPassModal: any;
  @ViewChild('side') public checkEmailModal: any;
  newPassword: any;
  confirmPassword: any;
  form: FormGroup;
  app: AppService;
  
  constructor( private appService: AppService,private router: Router,private frmbuilder:FormBuilder, private http: HttpClient) { 
    this.app = appService;
  }
  
  ngOnInit() {
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

  login(){
    this.app.authenticate(this.credentials, () => {
      if(this.app.authenticated == true){
        this.router.navigateByUrl('/');
      }
      else{
        $(".error-message").fadeIn("slow");
        setTimeout(function(){
          $(".error-message").fadeOut("slow");
        }, 5000)
      }
    });
    return false;
  }

}
