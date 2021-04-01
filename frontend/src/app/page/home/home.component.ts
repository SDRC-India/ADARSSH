import { Component, OnInit } from '@angular/core';
import { StaticServiceService } from '../static-page/services/static-service.service';
import { Constants } from 'src/app/constants';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'rmncha-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  homeData:any={};
  apigateway= Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL;
  constructor(private staticService: StaticServiceService, private spinner: NgxSpinnerService) { }

  ngOnInit() {
    let data ={
      mainMenu: "Home",
      subMenu: ""
    }

    this.staticService.getCMSRequestData(data).subscribe(data=>{
      this.homeData=data;
    })
  }

}
