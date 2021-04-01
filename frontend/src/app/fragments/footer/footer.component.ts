import { AppCommonServieService } from './../../services/app-common-servie.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'rmncha-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  constantObject:Constants

  router:Router;
  constructor(router:Router,private constants:AppCommonServieService) { 
    this.router = router;
  }

  ngOnInit() {
    this.constantObject=this.constants.getConstantsObject();
  }

}
