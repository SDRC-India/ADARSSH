import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Constants } from 'src/app/constants';
import { Cookie } from 'ng2-cookies';
declare var $: any;

@Component({
  selector: 'rmncha-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  router:Router;
  app: any;
  userLevel: string;
  userName: string;
  homeData: any;
  viewportWidth: number
  userManagementLink: any = {data: { expectedRole: 'NATIONAL'}};
  constructor(router:Router, private appService: AppService,private http: HttpClient) {
    this.router = router;
    this.app = appService;
  }

  ngOnInit() {
    this.viewportWidth = $(window).width();
    if (this.viewportWidth < 560) {
            $(".head-info").removeClass("col-md-3");
    }
  }
  openNav(){
    document.getElementById("mySidenav").style.width = "250px";
  }
  closeNav() {
    document.getElementById("mySidenav").style.width = "0";
  }
  setSubmenu(event) {
    if (!$(event.target).closest('.menu').hasClass("active")) {
      $(".menu.active").find(".submenu-list").slideUp();
      $(".menu.active").removeClass("active")
      $(event.target).closest('.menu').find('.submenu-list').slideDown();
      $(event.target).closest('.menu').addClass("active")
    }
    else {
      $(event.target).closest('.menu').removeClass("active")
      $(event.target).closest('.menu').find('.submenu-list').slideUp();
    }
  }
  logout(){
    this.appService.logout();
    this.app.userName = "";
  }

}
