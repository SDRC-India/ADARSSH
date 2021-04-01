import { Component, OnInit } from '@angular/core';
import { Constants } from 'src/app/constants';
import { StaticServiceService } from '../services/static-service.service';

@Component({
  selector: 'rmncha-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss']
})
export class VideoComponent implements OnInit {

  videoData:any[]=[];
  apigateway= Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL;
  p: number = 1;
  searchTexts;

  constructor(private staticService: StaticServiceService) { }

  ngOnInit() {
    let data = {
      mainMenu: "Resources",
      subMenu: "Video"
    }

    this.staticService.getCMSRequestData(data).subscribe(data => {
      this.videoData = data['Video'];
    })
  
  }

}
