import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import html2canvas from 'html2canvas';
import 'blueimp-canvas-to-blob/js/canvas-to-blob.min.js'; import saveAs from 'save-as';
import { NgxSpinnerService } from 'ngx-spinner';
import * as d3 from 'd3v4';
declare var $: any;

@Component({
  selector: 'rmncha-coverage-dashboard',
  templateUrl: './coverage-dashboard.component.html',
  styleUrls: ['./coverage-dashboard.component.scss']
})
export class CoverageDashboardComponent implements OnInit {

  selectedStateId: number;
  selectedDistrictId: number;
  selectedBlockId: number;
  coverageData: any;
  isDataAvailable: any;
  dashboardService: DashboardService;
  constructor(private dashboardServiceprovider: DashboardService, private spinner: NgxSpinnerService) {
    this.dashboardService = dashboardServiceprovider;
  }

  ngOnInit() {
    // window.onresize = function(){ location.reload(); }
    this.dashboardService.coverageDashboard = {};
    this.dashboardService.userLevelName = undefined;
    this.dashboardService.selectedStateName = undefined;
    this.dashboardService.selectedDistrictName = undefined;
    this.dashboardService.selectedBlockName = undefined;
    this.dashboardService.formFieldsAll = undefined;

    /* getting area level lists */
    if (!this.dashboardService.formFieldsAll)
      this.dashboardService.getUserRoles().subscribe(data => {
        this.dashboardService.formFieldsAll = data;
        this.dashboardService.coverageDashboard.selectedRoleId = 1;
        this.dashboardService.userLevelName = this.dashboardService.formFieldsAll[0].areaLevelName;

        // if (this.dashboardService.formFieldsAll) { //remove block level
        //   this.dashboardService.formFieldsAll.splice(3, 1);
        // }
      })
      /* getting state,district level lists */
    if (!this.dashboardService.areaDetails)
      this.dashboardService.getAreaDetails().subscribe(data => {
        this.dashboardService.areaDetails = data;
      })
      this.getCoverageData(1, 1);
      this.dashboardService.isCoveargeDataAvailable().subscribe(res => {
        if(res == true){
          this.isDataAvailable = true;
       
        }else{
          this.isDataAvailable = false;
        }
      })
   
  }

  /* api call for coverage data */
  async  getCoverageData(levelId, areaId) {
    this.dashboardService.getCoverageData(levelId, areaId).subscribe(res => {
      this.coverageData = res;
    });
  }

  counter(refEl, value) {
    // var that = d3.select(this);
    let i = d3.interpolateNumber(+refEl.text(), Math.abs(value));
    return function (t) {
      refEl.text(i(t).toFixed(1));
    };
  };
  /**
  * Download each image on click
  * @param el 
  * @param id 
  * @param indicatorName 
  */
  // downloadChartToImage(el, id, indicatorName) {
  //   $('.download-chart').css('display', "none");
  //   html2canvas(document.getElementById(id)).then((canvas) => {
  //     canvas.toBlob((blob) => {
  //       $('.download-chart').css('display', "block");
  //       saveAs(blob, indicatorName + ".jpg");
  //     }, err => {
  //       $('.download-chart').css('display', "block");
  //     });
  //   });
  // }
  downloadChartToImage(el, id, indicatorName) {
    this.spinner.show();
    let scr = window.scrollY;
    window.scrollTo(0,0); 
    html2canvas(document.getElementById(id)).then((canvas) => {
      canvas.toBlob((blob) => {
        saveAs(blob, indicatorName + ".jpg");
      });
      window.scrollTo(0, scr);
      this.spinner.hide();
    });
  }
  /**
   * Download full page content as pdf / excel
   * @param districtName 
   * @param blockName 
   * @param stateName
   * @param areaLevelname
   */
  
  async downloadAllChartsToImage(areaLevelName, stateName, districtName, blockName, type, fileType) {
    this.spinner.show();
    let barccards: any = [];
    let chartSvgs = [];
    let cdivIds = $(".chart-div:visible").map(function () {
      if (this.id) {
        return this.id;
      }
    }).get();

    d3.selectAll("svg").attr("version", 1.1).attr("xmlns", "http://www.w3.org/2000/svg");
    $('.download-chart').css('display', "none");
    for (let countCDiv = 0; countCDiv < cdivIds.length; countCDiv++) {
      $('#' + cdivIds[countCDiv]).children().each(function (index, el) {

        let chartSvg = $(this).children().html();
        if (chartSvg != "")
          chartSvgs.push(chartSvg);

        let base64Cards: any = {
          "indicatorGroupName": "",
          "svg": "",
          "chartType": "",
          "chartAlign": "",
          "nVal": "",
          "showNName": "",
          "indName": ""
        };
        base64Cards.indicatorGroupName = $('#' + cdivIds[countCDiv]).attr('grpid');
        base64Cards.chartType = $('#' + cdivIds[countCDiv]).attr('chartType');
        base64Cards.chartAlign = $('#' + cdivIds[countCDiv]).attr('align');
        base64Cards.nVal = $('#' + cdivIds[countCDiv]).attr('nVal');
        base64Cards.showNName = $('#' + cdivIds[countCDiv]).attr('showNName');
        base64Cards.indName = $('#' + cdivIds[countCDiv]).attr('indName');
        base64Cards.svg = chartSvg;
        barccards.push(base64Cards);

      });
    }
    this.dashboardService.downloadFullpageSvg(barccards, areaLevelName, stateName, districtName, blockName, type, '', '').subscribe(res => {
      saveAs(res, (stateName != undefined && districtName != undefined && blockName != undefined) ?
        stateName + "_" + districtName + "_" + blockName + "_" + new Date().getTime().toString() + ".pdf" :
        (stateName != undefined && districtName == undefined && blockName == undefined) ?
          stateName + "_" + new Date().getTime().toString() + ".pdf" :
          (stateName != undefined && districtName != undefined && blockName == undefined) ?
            stateName + "_" + districtName + "_" + new Date().getTime().toString() + ".pdf" :
            this.dashboardService.userLevelName + "_" + new Date().getTime().toString() + ".pdf");
      $('.download-chart').css('display', "block");
      this.spinner.hide();
    }, err => {
      $('.download-chart').css('display', "block");
      this.spinner.hide();
    });

  }
  async downloadAllChartsToImageInExcel(areaLevelName, areaLevelId, stateName, districtName, blockName, timeperiod, type, fileType) {
    this.spinner.show();
    // let barccards: any = [];
    let chartSvgs = [];
    let cdivIds = $(".chart-div:visible").map(function () {
      if (this.id) {
        return this.id;
      }
    }).get();
    $('.download-chart').css('display', "none");
    let paramModel: any = {
      "areaLevelId": areaLevelId ? areaLevelId : null,
      "stateName": stateName ? stateName : null,
      "districtName": districtName ? districtName : null,
      "blockName": blockName ? blockName : null,
      "dashboardType": type ? type : null,
      "districtId": this.selectedDistrictId ? this.selectedDistrictId : null,
      "blockId": this.selectedBlockId ? this.selectedBlockId : null,
      "stateId": this.selectedStateId ? this.selectedStateId : null,
      "sectorId": null,
      "tpId": 2,
      "formId": null,
      "listOfSvgs": [],
      "areaLevelName": areaLevelName ? areaLevelName : null,
      "checklistName": null,
      "sectorName": null,
      "timeperiod": timeperiod ? timeperiod : null
    };
    // for (let i = 0; i < divIds.length; i++) {
    d3.selectAll("svg").attr("version", 1.1).attr("xmlns", "http://www.w3.org/2000/svg");
    $('.download-chart').css('display', "none");
    for (let countCDiv = 0; countCDiv < cdivIds.length; countCDiv++) {
      $('#' + cdivIds[countCDiv]).children().each(function (index, el) {

        let chartSvg = $(this).children().html();
        if (chartSvg != "" && chartSvg != undefined){
          chartSvgs.push(chartSvg);

          let base64Cards: any = {
            "indicatorGroupName": "",
            "svg": "",
            "chartType": "",
            "chartAlign": "",
            "nVal": "",
            "showNName": "",
            "indName": ""
          };
          base64Cards.indicatorGroupName = $('#' + cdivIds[countCDiv]).attr('grpid');
          base64Cards.chartType = $('#' + cdivIds[countCDiv]).attr('chartType');
          base64Cards.chartAlign = $('#' + cdivIds[countCDiv]).attr('align');
          base64Cards.nVal = $('#' + cdivIds[countCDiv]).attr('nVal');
          base64Cards.showNName = $('#' + cdivIds[countCDiv]).attr('showNName');
          base64Cards.indName = $('#' + cdivIds[countCDiv]).attr('indName');
          base64Cards.svg = chartSvg;
          // barccards.push(base64Cards);
          paramModel.listOfSvgs.push(base64Cards);
          //  barccards.push(paramModel);
        }
       

      });
    }
    if (fileType === 'Excel') {
      this.dashboardService.downloadFullpageExcel(paramModel).subscribe(res => {
        saveAs(res, (stateName != undefined && districtName != undefined && blockName != undefined) ?
          stateName + "_" + districtName + "_" + blockName + "_" + new Date().getTime().toString() + ".xlsx" :
          (stateName != undefined && districtName == undefined && blockName == undefined) ?
            stateName + "_" + new Date().getTime().toString() + ".xlsx" :
            (stateName != undefined && districtName != undefined && blockName == undefined) ?
              stateName + "_" + districtName + "_" + new Date().getTime().toString() + ".xlsx" :
              this.dashboardService.userLevelName + "_" + new Date().getTime().toString() + ".xlsx");
        $('.download-chart').css('display', "block");
        this.spinner.hide();
      }, err => {
        $('.download-chart').css('display', "block");
        this.spinner.hide();
      });
    }
    // }
  }
  getKpiIndexList(kpiListLength) {
    let indexArray = [];
    for (let i = 0; i < Math.ceil(kpiListLength / 2); i++) {
      indexArray.push(i)
    }
    return indexArray;
  }

  /* selection for area level lists */
  async selectUserLevel(areaLevel, event) {
    if (event.isUserInput == true) {
    this.dashboardService.userLevelID = [];
    this.dashboardService.selectedStateName = undefined;
    this.dashboardService.selectedDistrictName = undefined;
    this.dashboardService.selectedBlockName = undefined;
    this.dashboardService.userLevelName = areaLevel.areaLevelName;
    this.dashboardService.userLevelID.push(areaLevel.areaLevelId);
    }
  }

/* selection for state level lists */
async selectStateLevel(selectedStatetId, event) {
    if (event.isUserInput == true) {
      this.dashboardService.userLevelID = [];
      this.selectedStateId = selectedStatetId.areaId;
      this.dashboardService.selectedStateName = selectedStatetId.areaName;
      this.dashboardService.userLevelID.push(this.selectedStateId);
    }
  }
/* selection for district level lists */
async selectDistrictLevel(selectedDistrictId, event) {
    if (event.isUserInput == true) {
    this.selectedDistrictId = selectedDistrictId.areaId;
    this.dashboardService.selectedDistrictName = selectedDistrictId.areaName;
    this.dashboardService.userLevelID.splice(1, 1, this.selectedDistrictId);
    }
  }

  /* selection for block level lists */
  async selectBlockLevel(selectedBlockId, event) {
    if (event.isUserInput == true) {
    this.selectedBlockId = selectedBlockId.areaId;
    this.dashboardService.selectedBlockName = selectedBlockId.areaName;
    this.dashboardService.userLevelID.splice(2, 2, this.selectedBlockId);
    }
  }

  /* chart name selection */
  selectChart(chart, chartName) {
    chart.selectedChart = chartName;
  }
  isAvailable(arr, el) {
    if (arr.indexOf(el) !== -1) {
      return true;
    } else {
      return false;
    }
  }
  removeReference(data) {
    return JSON.parse(JSON.stringify(data));
  }

  /* data for card view chart */
  cardData(indicators) {
    return JSON.parse(JSON.stringify(indicators));
  }
  getStackKeys(dataArr) {
    let allKeys = Object.keys(dataArr[0]);
    allKeys.splice(allKeys.indexOf("axis"), 1);
    return allKeys;
  }
/* convert to line chart */
  convertToLineChartFormat(chartData) {
    for (let i = 0; i < chartData.length; i++) {
      const el = chartData[i];
      el.timeperiod = el.axis ? el.axis : el.timeperiod;
    }
    return chartData;
  }

}
