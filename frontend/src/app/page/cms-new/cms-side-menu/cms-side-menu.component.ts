import { Component, OnInit, Input, EventEmitter, OnChanges, Output } from '@angular/core';
import { Router } from '@angular/router';
import {CmsServiceService} from '../services/cms-service.service';
declare var $:any;
@Component({
  selector: 'hmis-cms-side-menu',
  templateUrl: './cms-side-menu.component.html',
  styleUrls: ['./cms-side-menu.component.scss']
})
export class CmsSideMenuComponent implements OnInit,OnChanges {
  cmsServices: CmsServiceService;
  router:Router;
  
  @Input()
  leftmenu:any[]=[];
  selectedMenu;
  leftsubmenu:any;
  selectedSection:any;
  mainSelectedSection:any;
  @Output()
  selectedSectionEmitter:EventEmitter<any> = new EventEmitter<any>();
  constructor(router: Router, private cmsService: CmsServiceService) {
    this.router = router;
    this.cmsServices = cmsService;
   }

  ngOnInit() {
  //   if (this.leftmenu.length && this.selectedSection && this.mainSelectedSection)
  //   {
  //     this.mainSelectedSection = this.leftmenu[this.leftmenu.findIndex(d => d.leftmenuName === this.mainSelectedSection.leftmenuName)]
  //     this.selectedSection = this.mainSelectedSection.leftsubmenu[this.mainSelectedSection.leftmenu.findIndex(d=>d.id === this.selectedSection.id)]
  //  this.selectedSectionEmitter.emit(this.selectedSection)
  //   }
  //   else
  //   {
    this.mainSelectedSection = this.leftmenu[0];
      this.selectedSection = this.leftmenu[0].leftsubmenu[0]
      this.selectedSectionEmitter.emit(this.selectedSection)

    // }
  }

  ngOnChanges(changes){
    if (this.leftmenu.length && this.selectedSection) {
      this.mainSelectedSection = this.leftmenu[this.leftmenu.findIndex(d => d.leftmenuName === this.mainSelectedSection.leftmenuName)]
      this.selectedSection = this.mainSelectedSection.leftsubmenu[this.mainSelectedSection.leftsubmenu.findIndex(d => d.leftmenuId === this.selectedSection.leftmenuId)]
      this.selectedSectionEmitter.emit(this.selectedSection)
    }
    else {
      this.mainSelectedSection = this.leftmenu[0];
      this.selectedSection = this.leftmenu[0].leftsubmenu[0]
      this.selectedSectionEmitter.emit(this.selectedSection)

    }
  
  }

  getPageContentData(menu,section){
    if (menu.leftmenuId != this.selectedSection.leftmenuId)
    {
      this.mainSelectedSection = section;
    this.selectedSection=menu;
    this.selectedSectionEmitter.emit(menu);
    }
  }

}
