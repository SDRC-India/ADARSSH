import { Component, OnInit, ViewChild } from '@angular/core';
import { StaticServiceService } from '../services/static-service.service';
import { Constants } from 'src/app/constants';
import { HttpClient } from '@angular/common/http';
import { AppService } from 'src/app/app.service';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
declare var $: any;
import * as _moment from 'moment';
// tslint:disable-next-line:no-duplicate-imports
import {default as _rollupMoment, Moment} from 'moment';
import { FormControl } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { DatePipe } from '@angular/common';
import saveAs from "save-as";
import { MatInput } from '@angular/material';
const moment = _rollupMoment || _moment;

export const MY_FORMATS = {
  parse: {
    dateInput: 'LL',
  },
  display: {
    dateInput: 'MMMM YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
@Component({
  selector: 'rmncha-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ],
})
export class ReportComponent implements OnInit {
  @ViewChild('input', { read: MatInput}) input: MatInput;
  timePeriodFrom: any;
  timePeriodTo: any;
  ctrlValueToMonth: any;
  checkListModel: any;
  checkList: any[] = [];
  facilityTypeModel: any;
  states: any = [];
  districts: any[] = [];
  facilityTypes: any[] = [];
  facilityModel: any;
  facilityLevel: any;
  facilities: any[] = [];
  areaLists: any[] = [];
  stateModel: any;
  districtModel: any;
  blockModel: any;
  blocks: any[] = [];
  allData: any;
  facilityName: boolean = true;
  model: any = {};
  downloadSuccess: any;
  errorMessage: string;
  info: any;
  sDate: any;
  eDate: any;
  app: any;
  facilityTypeId: any;
  showEmptyFromDateError: boolean=false;
  staticService: StaticServiceService;
  newDate:any;
  date = new FormControl(moment());
  dateTo = new FormControl(moment());
  minDate = new Date(1950, 0, 1);
  maxDate = new Date(Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]), Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]) - 1, Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]));
  maxDateTo = new Date(Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]), Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]) - 1, Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]));
  constructor(private datepipe: DatePipe,private staticServicePorvider: StaticServiceService, private http: HttpClient,private appService: AppService) {
    this.app = appService;
    this.staticService = staticServicePorvider;
   }

  ngOnInit() {
    this.staticService.getReportData().subscribe(data => {
      this.allData = data;
      this.checkList = this.allData.enginesForm;
      this.areaLists = this.allData.area;
      this.facilityTypes = this.allData.facilityType;
    })

    this.staticService.getRegisteredUserData().subscribe(data => {
      this.staticService.getData(data);
      this.staticService.getUserDetails = data;
      if(!this.app.checkUserAuthorization(['USER_MGMT_ALL_API'])){
        if(this.staticService.getUserDetails.sessionMap.areaLevel == "STATE"){
          this.stateModel = this.staticService.getUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
        }else if(this.staticService.getUserDetails.sessionMap.areaLevel == "DISTRICT"){
          this.stateModel = this.staticService.getUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
          this.districtModel = this.staticService.getUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 3)[0].areaId;
        }else if(this.staticService.getUserDetails.sessionMap.areaLevel == "BLOCK"){
          this.stateModel = this.staticService.getUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 2)[0].areaId;
          this.districtModel = this.staticService.getUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 3)[0].areaId;
          this.blockModel = this.staticService.getUserDetails.sessionMap.area.filter(d => d.areaLevel.areaLevelId == 4)[0].areaId;
        }
      }
    })
  }

  selectFacility(facilities) {
    // if (facilities == 'DH' || facilities == 'MC') {
    //   this.facilityName = false;
    // } else {
    //   this.facilityName = true;
    // }
    if(facilities=='All'){
      this.facilityTypeId = undefined;
    }else{
      this.facilityTypeId = facilities.slugId;
    }
  }

// from month selection
  chosenYearHandler(normalizedYear: Moment) {
    const ctrlValueFrom = normalizedYear;
    ctrlValueFrom.year(normalizedYear.year());
    this.date.setValue(ctrlValueFrom);
    this.newDate = undefined;
    this.input.value = undefined;
    this.timePeriodFrom = undefined;
    this.timePeriodTo = undefined;
    (<HTMLInputElement> document.getElementById("toTimePeriod")).disabled = true;
  }

  chosenMonthHandler(normalizedMonth: Moment, datepicker: MatDatepicker<Moment>) {
    this.newDate = new Date(normalizedMonth as any);
    const ctrlValueFromMonth = normalizedMonth;
    ctrlValueFromMonth.month(normalizedMonth.month());
    this.date.setValue(ctrlValueFromMonth);
    // this.newDate = new Date(ctrlValueFromMonth);
    let mm: number | string = this.newDate.getMonth() + 1;
    if (mm < 10) {
      mm = '0' + mm;
    }
    const yy: number = this.newDate.getFullYear();
    this.sDate = `${mm}/${yy}`;
    this.timePeriodTo = '';
    this.dateTo.setValue('');
    this.ctrlValueToMonth = undefined;
    datepicker.close();
  }
 // end from month selection

//  to month selection start
  chosenYearHandlerTo(normalizedYearTo: Moment) {
    const ctrlValueTo = this.dateTo.value;
    ctrlValueTo.year(normalizedYearTo.year());
    this.dateTo.setValue(ctrlValueTo);
  }

  chosenMonthHandlerTo(normalizedMonthTo: Moment, datepickerTo: MatDatepicker<Moment>) {
    this.ctrlValueToMonth = normalizedMonthTo;
    this.ctrlValueToMonth.month(normalizedMonthTo.month());
    this.dateTo.setValue(this.ctrlValueToMonth);
    let newDateOfTo = new Date(this.ctrlValueToMonth);
    let mm: number | string = newDateOfTo.getMonth() + 1;
    if (mm < 10) {
      mm = '0' + mm;
    }
    const yy: number = newDateOfTo.getFullYear();
    this.eDate = `${mm}/${yy}`;
    datepickerTo.close();
  }
  // end end date selection



// download raw data report
  downloadReport() {
    // if(this.facilityTypeId == undefined){
    //   this.facilityTypeId = -1;
    // }
    if (this.stateModel == 'all' && this.timePeriodTo !='' && this.timePeriodTo != undefined && this.timePeriodFrom !='' && this.timePeriodFrom != undefined) {
      this.http.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + "api/report/getRawDataReportFinal?formId=" + this.checkListModel.formId
        + ( this.facilityTypeId ? '&facilityTypeId=' + this.facilityTypeId : '') + '&sDate=' + this.sDate +
        '&eDate=' + this.eDate, { responseType: "blob" }).subscribe((response:any) => {
          if(!response.size){
            this.info = "Data Not Available";
            $("#infoModal").modal('show');
          }else{
            saveAs(response,this.checkListModel.name+".xlsx")
            this.info = '';
            $("#infoModal").modal('hide');
          }
        },
          error => {
            this.errorMessage = "Some error found.";
            $("#errorModal").modal('show');
          });
    }
    else if (this.districtModel == 'all' && this.timePeriodTo !='' && this.timePeriodTo != undefined && this.timePeriodFrom !='' && this.timePeriodFrom != undefined) {
      this.http.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + "api/report/getRawDataReportFinal?formId=" + this.checkListModel.formId
        +  ( this.facilityTypeId ? '&facilityTypeId=' + this.facilityTypeId : '') + '&sDate=' + this.sDate +
        '&eDate=' + this.eDate + '&stateId=' + this.stateModel, { responseType: 'blob' }).subscribe(response => {
          if(!response.size){
            this.info = "Data Not Available";
            $("#infoModal").modal('show');
          }else{
            saveAs(response,this.checkListModel.name+".xlsx")
            this.info = '';
            $("#infoModal").modal('hide');
          }
        },
          error => {
            this.errorMessage = "Some error found.";
            $("#errorModal").modal('show');
          });
    }
    else if (this.blockModel == 'all' && this.timePeriodTo !='' && this.timePeriodTo != undefined && this.timePeriodFrom !='' && this.timePeriodFrom != undefined) {
      this.http.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + "api/report/getRawDataReportFinal?formId=" + this.checkListModel.formId
        +  ( this.facilityTypeId ? '&facilityTypeId=' + this.facilityTypeId : '') + '&sDate=' + this.sDate +
        '&eDate=' + this.eDate + '&stateId=' + this.stateModel + '&districtId=' + this.districtModel, { responseType: 'blob' }).subscribe(response => {
          if(!response.size){
            this.info = "Data Not Available";
            $("#infoModal").modal('show');
          }else{
            saveAs(response,this.checkListModel.name+".xlsx")
            this.info = '';
            $("#infoModal").modal('hide');
          }
        },
          error => {
            this.errorMessage = "Some error found.";
            $("#errorModal").modal('show');
          });
    }else{
      if(this.timePeriodTo !='' && this.timePeriodTo != undefined && this.timePeriodFrom !='' && this.timePeriodFrom != undefined){
        this.http.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + "api/report/getRawDataReportFinal?formId=" + this.checkListModel.formId
        +  ( this.facilityTypeId ? '&facilityTypeId=' + this.facilityTypeId : '') + '&sDate=' + this.sDate +
        '&eDate=' + this.eDate + '&stateId=' + this.stateModel + '&districtId=' + this.districtModel + '&blockId=' + this.blockModel, { responseType: 'blob' }).subscribe(response => {
          if(!response.size){
            this.info = "Data Not Available";
            $("#infoModal").modal('show');
          }else{
            saveAs(response,this.checkListModel.name+".xlsx")
            this.info = '';
            $("#infoModal").modal('hide');
          }
        },
          error => {
            this.errorMessage = "Some error found.";
            $("#errorModal").modal('show');
          });
      }else{
        $("#errorModal").modal('hide');
        this.showEmptyFromDateError = true;
      }
    }

  }


}
