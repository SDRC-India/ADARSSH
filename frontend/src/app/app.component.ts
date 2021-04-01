import { Component, OnInit } from "@angular/core";
import { LoadingBarService } from "@ngx-loading-bar/core";
import { NgxSpinnerService } from "ngx-spinner";
import { Router } from "@angular/router";
import { MessagingService } from "./shared/messaging.service";
import { ToastrService } from "ngx-toastr";
import { v4 as uuid } from 'uuid';
import * as status from 'http-status-codes';
declare var $:any;
@Component({
  selector: "rmncha-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"]
})
export class AppComponent implements OnInit {
  message: any;

  constructor(public loader: LoadingBarService, private spinner: NgxSpinnerService, private router: Router,
    private messagingService: MessagingService, private toastr: ToastrService) {

    this.loader.progress$.subscribe(data=>{
      if(data>0)
      {
        this.spinner.show();
      }
      else
      {
        this.spinner.hide();
      }
    });
  }
  ngOnInit() {
    const userId = uuid();
    this.messagingService.requestPermission(userId);
    this.messagingService.receiveMessage();
    this.messagingService.currentMessage.subscribe(data => {
      if (data != null) {
        if(data.data.statusCode == status.OK)
        this.toastr.success(data.notification.body, data.notification.title);
        else if(data.data.statusCode == status.NOT_MODIFIED || data.data.statusCode == status.ACCEPTED)
        this.toastr.info(data.notification.body, data.notification.title);
        else if(data.data.statusCode == status.CONFLICT)
        this.toastr.warning(data.notification.body, data.notification.title);
        else
        this.toastr.error(data.notification.body, data.notification.title);
      }
    });
  }
  ngAfterViewChecked() {
    if ($(window).width() <= 992) {
      $(".collapse").removeClass("show");
      $(".navbar-nav .nav-item").not('.dropdown').click(function () {
        $(".collapse").removeClass("show");
      })
    }
    //$(".main-content").css("min-height", $(window).height() - 170);

    /** close modal on browser back button press */
    $(window).on('popstate', function () {
      $('.modal').modal('hide');
      $(".modal-backdrop").remove();
      $(".in").remove();
    });
  }
}
