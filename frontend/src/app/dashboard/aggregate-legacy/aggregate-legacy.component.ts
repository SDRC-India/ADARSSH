import { Component, OnInit, ViewChild } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import { MatInput } from '@angular/material';
import { Constants } from 'src/app/constants';
import { HttpClient } from '@angular/common/http';
import { LoadingBarService } from '@ngx-loading-bar/core';
import { NgxSpinnerService } from "ngx-spinner";
import { Subscription, interval } from 'rxjs';
declare var $: any;

@Component({
  selector: 'rmncha-aggregate-legacy',
  templateUrl: './aggregate-legacy.component.html',
  styleUrls: ['./aggregate-legacy.component.scss']
})
export class AggregateLegacyComponent implements OnInit {
  @ViewChild('input', { read: MatInput }) input: MatInput;
  dashboardService: DashboardService;
  errorMessage: string;
  info: any;
  tableColumns: any;
  tableData: any;
  subscription: Subscription
  visible: boolean = false;
  // private stompClient;

  constructor(private dashboardServiceprovider: DashboardService, private httpClient: HttpClient
  ) {
    this.dashboardService = dashboardServiceprovider;
  }

  ngOnInit() {
    this.dashboardService.snapshotView = {};
    this.dashboardService.getAllTimePeriod(true).subscribe(data => {
      this.dashboardService.timeperiodLists = data;
      this.dashboardService.snapshotView.timeperiodName = this.dashboardService.timeperiodLists[0].tpName;
      this.dashboardService.snapshotView.year = this.dashboardService.timeperiodLists[0].year;
      this.dashboardService.snapshotView.timeperiodId = this.dashboardService.timeperiodLists[0].tpId;
      // this.initializeWebSocketConnection();
      this.getAllLegacyRecordStatus();
    })
    const source = interval(20000);

    // const sec = interval(1000);
    //call api sequence every 20 second
    this.subscription = source.subscribe(val => this.getAllLegacyRecordStatus());
  }

  ngOnDestroy() {
    this.subscription && this.subscription.unsubscribe();
  }

  /* timeperiod selection */
  selectTimePeriod(tp, event) {
    if (event.isUserInput == true) {
      this.dashboardService.snapshotView.timeperiodName = tp.tpName;
      this.dashboardService.snapshotView.year = tp.year
      this.dashboardService.snapshotView.timeperiodId = tp.tpId;

    }
  }

  async aggregate() {
    // this.spinner.show();


    this.dashboardService.aggregateLegacyData(this.dashboardService.snapshotView.timeperiodId,
      this.dashboardService.snapshotView.timeperiodName + ' ' + this.dashboardService.snapshotView.year, 'monthly').subscribe();

    this.info = "Aggregation Scheduled. Please wait..";
    $("#infoModal").modal('show');

    await this.delay(1000);

    this.getAllLegacyRecordStatus();
  }

  getAllLegacyRecordStatus() {
    this.dashboardService.getAllLegacyRecordStatus().subscribe(res => {
      this.tableData = res;

      if (this.tableData) {
        // this.tableColumns = Object.keys(this.tableData[0]);
        this.tableColumns = ['Time Period', 'Start Time', 'End Time', 'Status']
      }

    });
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // aggregate() {
  //   let data = JSON.stringify({
  //     'tpId' : this.dashboardService.snapshotView.timeperiodId,
  //     'tpName':   this.dashboardService.snapshotView.timeperiodName+' '+this.dashboardService.snapshotView.year 
  //   })
  //   this.info = "Aggregation Scheduled. Please wait..";
  //     $("#infoModal").modal('show');

  //     this.stompClient.send("/app/bypass/legacyAggregate" , {},data , 'monthly');

  //   // this.dashboardService.aggregateLegacyData(this.dashboardService.snapshotView.timeperiodId,
  //   //   this.dashboardService.snapshotView.timeperiodName+' '+this.dashboardService.snapshotView.year , 'monthly').subscribe();
  // }

  // initializeWebSocketConnection(){
  //   let ws = new SockJS('http://localhost:8080/'+Constants.DASHBOARD_SERVICE_URL);
  //   this.stompClient = Stomp.over(ws);
  //   let that = this;
  //   this.stompClient.connect({}, function() {
  //     that.stompClient.subscribe("/bypass/topic/getData", (message) => {
  //       if(message.body) {
  //         console.log(message.body);
  //       }
  //     });
  //   });
  // }
}
