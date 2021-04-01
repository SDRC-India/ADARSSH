import { Component, OnInit, HostListener } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import html2canvas from 'html2canvas';
import 'blueimp-canvas-to-blob/js/canvas-to-blob.min.js';
import saveAs from 'save-as';
import { NgxSpinnerService } from 'ngx-spinner';
import * as d3 from "d3";
declare var $: any;

@Component({
  selector: 'rmncha-snapshot-view',
  templateUrl: './snapshot-view.component.html',
  styleUrls: ['./snapshot-view.component.scss']
})
export class SnapshotViewComponent implements OnInit {

  selectedStateId: number;
  selectedDistrictId: number;
  selectedBlockId: number;
  snapShotData: any;
  boxData: any;
  subsectors: any;
  groupIndList: any;
  checkListName: string;
  facilityTypeId: any;
  facilityLevelId: any;
  dashboardService: DashboardService;
  // windowScrolled: boolean;
  sections: any;
  subSectors: any;
  selectedSecId: number;
  indicators: any;
  formId: number;
  indicatorsData: boolean = false;
  tabMode: boolean = false;
  viewportWidth: number;
  sectornName: string;
  selectedChart: boolean = true;
  toggleChart: boolean = false;
  stackedBarRawData: any;
  stackedBarFormattedData: any;
  stackLegends: any;
  chartType: any = 'threeDBar';
  windowScrolled: boolean;
  allValueZero: boolean = true;
  areaLevelId: number;
  constructor(private dashboardServiceprovider: DashboardService
    , private spinner: NgxSpinnerService) {
    this.dashboardService = dashboardServiceprovider;
  }
  //* scroll to top start
  @HostListener("window:scroll", [])
  onWindowScroll() {
    if (window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop > 100) {
      this.windowScrolled = true;
    }
    else if (this.windowScrolled && window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop < 10) {
      this.windowScrolled = false;
    }
  }
  scrollToTop(scrollTargetY, speed, easing) {
    let PI_D2 = Math.PI / 2,
      easingEquations = {
        easeOutSine: function (pos) {
          return Math.sin(pos * (Math.PI / 2));
        },
        easeInOutSine: function (pos) {
          return (-0.5 * (Math.cos(Math.PI * pos) - 1));
        },
        easeInOutQuint: function (pos) {
          if ((pos /= 0.5) < 1) {
            return 0.5 * Math.pow(pos, 5);
          }
          return 0.5 * (Math.pow((pos - 2), 5) + 2);
        }
      };

    (function smoothscroll() {
      let currentScroll = document.documentElement.scrollTop || document.body.scrollTop;
      if (currentScroll > 0) {
        window.requestAnimationFrame(smoothscroll);
        window.scrollTo(20, currentScroll - (currentScroll));
      }
    })();
  }
  //* scroll to top end
  ngOnInit() {
    /* removing offset-md-2 class while window size is less than 995 */
    this.viewportWidth = $(window).width();
    if (this.viewportWidth < 995) {
      $(".chart-right").removeClass("offset-md-2");
      $(".chart-right").removeClass("col-md-10").addClass("col-md-12");
      this.tabMode = true
    } else {
      this.tabMode = false;
    }

    this.dashboardService.snapshotView = {};
    this.dashboardService.userLevelName = undefined;
    this.dashboardService.selectedStateName = undefined;
    this.dashboardService.selectedDistrictName = undefined;
    this.dashboardService.selectedBlockName = undefined;
    this.dashboardService.formFieldsAll = undefined;

    /* getting checklist data */
    this.dashboardService.getCheckListData().subscribe(res => {
      this.dashboardService.checkListDetails = res;
      this.dashboardService.checkLists = Object.keys(this.dashboardService.checkListDetails);
      this.checkListName = this.dashboardService.checkLists[0];
      this.sections = this.dashboardService.checkListDetails[this.checkListName][0];
      this.dashboardService.sectionObj = this.sections;
      this.sectornName = this.sections.sectorName;
      this.dashboardService.checkListObj = this.checkListName;
      this.selectedSecId = this.sections.sectorId;
      this.formId = this.sections.formId;

      this.dashboardService.snapshotView.selectedRoleId = 1;
      // this.dashboardService.snapshotView.timeperiodId = 2;
      // this.dashboardService.snapshotView.timeperiodName = 'Apr 2019';

      this.getSectors(this.checkListName);

    
    })

    /* getting area level list */
    if (!this.dashboardService.formFieldsAll)
      this.dashboardService.getUserRoles().subscribe(data => {
        this.dashboardService.formFieldsAll = data;
        this.dashboardService.userLevelName = this.dashboardService.formFieldsAll[0].areaLevelName;
      })

    /* getting state,district level list */
    if (!this.dashboardService.areaDetails)
      this.dashboardService.getAreaDetails().subscribe(data => {
        this.dashboardService.areaDetails = data;
      })
  
      this.dashboardService.getAllTimePeriod().subscribe(data => {
        this.dashboardService.timeperiodLists = data;
        this.dashboardService.snapshotView.timeperiodName = 
            this.dashboardService.timeperiodLists[0].tpName;
            this.dashboardService.snapshotView.timeperiodId = this.dashboardService.timeperiodLists[0].tpId;
            this.dashboardService.snapshotView.year = this.dashboardService.timeperiodLists[0].year;
      })

  }

  getStackedBar(chartData) {
    this.stackedBarRawData = chartData;
    this.stackedBarFormattedData = this.convertToStack(this.stackedBarRawData);
    return this.stackedBarFormattedData;
  }

  // convert to stacked bar chart data
  convertToStack(stack) {
    let formattedData: any[] = [];
    let stackEl: any = {};
    for (let i = 0; i < stack.length; i++) {
      const el = stack[i];
      for (let j = 0; j < el.length; j++) {
        const sEl = el[j];
        let axis = sEl.axis; 
        let unitY = sEl.unit;
        let denominatorOnPopup = sEl.denominator;
        let numeratorOnPopup = sEl.numerator;
        let tooltipOnPopup = sEl.tooltipValue;
        let value = sEl.value;
        let label = sEl.label; // 12
        let denominator = label + ' denominator';
        let numerator = label + ' numerator';
        let unit = label + ' unit';
        let tooltipValue = label + ' tooltipValue';
        if (!stackEl[axis]) {          //sEl["DH"]
          stackEl[axis] = {};
          stackEl[axis][label] = sEl.value;
          stackEl[axis][denominator] = sEl.denominator;
          stackEl[axis][numerator] = sEl.numerator;
          stackEl[axis][unit] = sEl.unit;
          stackEl[axis][tooltipValue] = sEl.tooltipValue;
        } else {
          stackEl[axis][label] = sEl.value;
          stackEl[axis].axis = axis;
          stackEl[axis].unit = unitY;
          stackEl[axis].denominator = denominatorOnPopup;
          stackEl[axis].numerator = numeratorOnPopup;
          stackEl[axis].tooltipValue = tooltipOnPopup;
          stackEl[axis].value = stackEl[axis].value ? parseFloat(stackEl[axis].value)+parseFloat(value): value;
          stackEl[axis][tooltipValue] = sEl.tooltipValue;
          stackEl[axis][denominator] = sEl.denominator;
          stackEl[axis][numerator] = sEl.numerator;
          stackEl[axis][unit] = sEl.unit;
        }
      }
    }
    for (let k = 0; k < Object.keys(stackEl).length; k++) {
      const axis = Object.keys(stackEl)[k];
      formattedData.push(stackEl[axis]);
    }
    return formattedData;
  }

  // get sectors through checklist
  async  getSectors(checkList) {
    this.checkListName = checkList;
    this.sections = this.dashboardService.checkListDetails[checkList];
    this.formId = this.sections[0].formId;
    this.selectedSecId = this.sections[0].sectorId;
  }


  /* selection for checklist */
  async  selectCheckList(checkList) {
    this.checkListName = checkList;
    this.sections = this.dashboardService.checkListDetails[checkList];
    this.formId = this.sections[0].formId;
    this.selectedSecId = this.sections[0].sectorId;
    this.dashboardService.sectionObj = this.sections[0]
    this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
      this.selectedBlockId ? this.selectedBlockId :
        this.selectedDistrictId ? this.selectedDistrictId :
          this.selectedStateId ? this.selectedStateId :
            1, this.selectedSecId, 2, this.sections[0].formId,
      this.facilityTypeId ? this.facilityTypeId : null,
      this.facilityLevelId ? this.facilityLevelId : null);
  }
  /* selection for sector */
  async selectSectors(sector) {
    this.selectedSecId = sector.sectorId;
    this.formId = sector.formId;
    this.sectornName = sector.sectorName;
    this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
      this.selectedBlockId ? this.selectedBlockId :
        this.selectedDistrictId ? this.selectedDistrictId :
          this.selectedStateId ? this.selectedStateId :
            1, this.selectedSecId, 2, this.formId,
      this.facilityTypeId ? this.facilityTypeId : null,
      this.facilityLevelId ? this.facilityLevelId : null)
      // var elmnt = document.getElementById("myDiv");
      // elmnt.scrollTop = 320;
    
  }
  /* get indicator data */
  async getIndicators(arealevelId, areaId, sector, timePeriodId, formId, facilityTypeId, facilityLevelId) {
    if (formId == 3) {
      window.scrollTo(0, 220);
    } else if (formId == 2 || formId == 4) {
      window.scrollTo(0, 225);
    } else {
      window.scrollTo(0, 200);
    }

    if(this.dashboardService.snapshotView.timeperiodId){
      this.dashboardService.getIndicatorData(arealevelId, areaId, sector, this.dashboardService.snapshotView.timeperiodId, formId,
        facilityTypeId ? facilityTypeId : null, facilityLevelId ? facilityLevelId : null).subscribe(res => {
          this.indicators = res;
        })
    }
        
      this.dashboardService.getAllFacilityTypeAndLevel(this.formId, 'Facility Type').subscribe(data => {
        this.dashboardService.facilityTypeLists = data;
      })

      this.dashboardService.getAllFacilityTypeAndLevel(this.formId, 'Facility Level').subscribe(data => {
        this.dashboardService.facilityLevelLists = data;
      })
  }

  /* area level selection */
  selectUserLevel(areaLevel, event) {
    if (event.isUserInput == true) {
      this.areaLevelId = areaLevel.areaLevelId
      this.dashboardService.userLevelID = [];
      this.dashboardService.selectedStateName = undefined;
      this.selectedStateId = undefined;
      this.dashboardService.selectedDistrictName = undefined;
      this.selectedDistrictId = undefined;
      this.dashboardService.selectedBlockName = undefined;
      this.selectedBlockId = undefined;
      this.dashboardService.userLevelName = areaLevel.areaLevelName;
      this.dashboardService.userLevelID.push(areaLevel.areaLevelId);
      if(this.areaLevelId == 1){
        this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
          this.selectedBlockId ? this.selectedBlockId :
            this.selectedDistrictId ? this.selectedDistrictId :
              this.selectedStateId ? this.selectedStateId :
                1, this.selectedSecId, this.dashboardService.snapshotView.timeperiodId, this.formId,
          this.facilityTypeId ? this.facilityTypeId : null,
          this.facilityLevelId ? this.facilityLevelId : null)
      }
      
    }
  }

  /* state selection */
  async selectStateLevel(selectedStatetId, event) {
    if (event.isUserInput == true) {
      this.dashboardService.userLevelID = [];
      this.selectedStateId = selectedStatetId.areaId;
      this.dashboardService.selectedStateName = selectedStatetId.areaName;
      this.dashboardService.userLevelID.push(this.selectedStateId);
      if(this.areaLevelId == 2){
        this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
          this.selectedBlockId ? this.selectedBlockId :
            this.selectedDistrictId ? this.selectedDistrictId :
              this.selectedStateId ? this.selectedStateId :
                1, this.selectedSecId, this.dashboardService.snapshotView.timeperiodId, this.formId,
          this.facilityTypeId ? this.facilityTypeId : null,
          this.facilityLevelId ? this.facilityLevelId : null)
      }
    }
  }
  /*district selection*/
  async selectDistrictLevel(selectedDistrictId, event) {
    if (event.isUserInput == true) {
      this.selectedDistrictId = selectedDistrictId.areaId;
      this.dashboardService.selectedDistrictName = selectedDistrictId.areaName;
      this.dashboardService.userLevelID.splice(1, 1, this.selectedDistrictId);
      this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
        this.selectedBlockId ? this.selectedBlockId :
          this.selectedDistrictId ? this.selectedDistrictId :
            this.selectedStateId ? this.selectedStateId :
              1, this.selectedSecId, this.dashboardService.snapshotView.timeperiodId, this.formId,
        this.facilityTypeId ? this.facilityTypeId : null,
        this.facilityLevelId ? this.facilityLevelId : null)
    }
  }

  /* timeperiod selection */
  async selectTimePeriod(tp, event) {
    if (event.isUserInput == true) {
      this.dashboardService.snapshotView.timeperiodName = tp.tpName;
      this.dashboardService.snapshotView.timeperiodId = tp.tpId;
      this.dashboardService.snapshotView.year = tp.year;

      this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
        this.selectedBlockId ? this.selectedBlockId :
          this.selectedDistrictId ? this.selectedDistrictId :
            this.selectedStateId ? this.selectedStateId :
              1, this.selectedSecId, this.dashboardService.snapshotView.timeperiodId, this.formId,
        this.facilityTypeId ? this.facilityTypeId : null,
        this.facilityLevelId ? this.facilityLevelId : null)
    }
  }

  /* facility type selection */
  async selectFacilityType(ft, event) {
    if (event.isUserInput == true) {
      this.facilityTypeId = ft ? ft.slugId : null;
      this.dashboardService.facilityTypeName = ft ? ft.name : null;
      this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
        this.selectedBlockId ? this.selectedBlockId :
          this.selectedDistrictId ? this.selectedDistrictId :
            this.selectedStateId ? this.selectedStateId :
              1, this.selectedSecId, this.dashboardService.snapshotView.timeperiodId, this.formId,
        this.facilityTypeId ? this.facilityTypeId : null,
        this.facilityLevelId ? this.facilityLevelId : null)
    }
  }

  /* facility levele selection */
  async  selectFacilityLevel(fl, event) {
    if (event.isUserInput == true) {
      this.facilityLevelId = fl ? fl.slugId : null;
      this.dashboardService.facilityLevelName = fl ? fl.name : null;
      this.getIndicators(this.dashboardService.snapshotView.selectedRoleId,
        this.selectedBlockId ? this.selectedBlockId :
          this.selectedDistrictId ? this.selectedDistrictId :
            this.selectedStateId ? this.selectedStateId :
              1, this.selectedSecId, this.dashboardService.snapshotView.timeperiodId, this.formId,
        this.facilityTypeId ? this.facilityTypeId : null,
        this.facilityLevelId ? this.facilityLevelId : null)
    }
  }

  /* chart name selection */
  selectChart(chart, chartName) {
    chart.selectedChart = chartName;
  }

  /* if all value of pie is zero, converting it into bar chart */
  convertToBarChart(indicator, chart, e) {
    chart.selectedChart = e;
    indicator.chartsAvailable[0] = 'bar';
    this.allValueZero = false;
  }
  /**
   * Download each image on click
   * @param el 
   * @param id 
   * @param indicatorName 
   */
  downloadChartToImage(el, id, indicatorName) {
    // $('.download-chart').css('display', "none");
    this.spinner.show();
    let scr = window.scrollY;
    window.scrollTo(0,0); 
    html2canvas(document.getElementById(id), { backgroundColor: null, logging: false,
      // x:window.scrollX,
      // y:window.scrollY,
      // width: window.innerWidth,
		  // height: window.innerHeight
      // width: document.getElementById(id).clientWidth,
      // height: document.getElementById(id).clientHeight
    }).then((canvas) => {
      canvas.toBlob((blob) => {
        // $('.download-chart').css('display', "block");
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
 * @param areaLevelId
 */
  async downloadAllChartsToImage(areaLevelName, stateName, districtName, blockName, type,
    fileType, checkListName, timePeriod, facilityTypeId, facilityLevelId) {
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
        if (chartSvg != "" && chartSvg != undefined){

          chartSvgs.push(chartSvg);

          let base64Cards: any = {
            "indicatorGroupName": "",
            "svg": "",
            "chartType": "",
            "chartAlign": "",
            "showValue": "",
            "showNName": "",
            "indName": "",
            "typeId": facilityTypeId,
            "levelId": facilityLevelId
          };
          base64Cards.indicatorGroupName = $('#' + cdivIds[countCDiv]).attr('grpid');
          base64Cards.chartType = $('#' + cdivIds[countCDiv]).attr('chartType');
          base64Cards.chartAlign = $('#' + cdivIds[countCDiv]).attr('align');
          base64Cards.showValue = $('#' + cdivIds[countCDiv]).attr('nVal') ? parseInt($('#' + cdivIds[countCDiv]).attr('nVal')) : $('#' + cdivIds[countCDiv]).attr('nVal');
          base64Cards.showNName = $('#' + cdivIds[countCDiv]).attr('showNName');
          base64Cards.indName = $('#' + cdivIds[countCDiv]).attr('indName');
          base64Cards.svg = chartSvg;
          barccards.push(base64Cards);
        }
      });
    }
    this.dashboardService.downloadFullpageSvg(barccards, areaLevelName, stateName, districtName, blockName, type, checkListName, timePeriod).subscribe(res => {
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
  async downloadAllChartsToImageInExcel(areaLevelName, areaLevelId, stateName, districtName,
    blockName, checkListName, sectionName, selectedSecId, timeperiod, facilityTypeId,
    facilityLevelId, type, fileType) {
    this.spinner.show();
    let barccards: any = [];
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
      "sectorId": this.selectedSecId,
      "tpId": this.dashboardService.snapshotView.timeperiodId,
      "formId": this.formId ? this.formId : null,
      "listOfSvgs": [],
      "areaLevelName": areaLevelName ? areaLevelName : null,
      "checklistName": checkListName ? checkListName : null,
      "sectorName": sectionName ? sectionName : null,
      "timeperiod": timeperiod,
      "typeId": facilityTypeId,
      "levelId": facilityLevelId
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
            "showValue": "",
            "showNName": "",
            "indName": ""
          };
          base64Cards.indicatorGroupName = $('#' + cdivIds[countCDiv]).attr('grpid');
          base64Cards.chartType = $('#' + cdivIds[countCDiv]).attr('chartType');
          base64Cards.chartAlign = $('#' + cdivIds[countCDiv]).attr('align');
          base64Cards.showValue = $('#' + cdivIds[countCDiv]).attr('nVal');
          base64Cards.showNName = $('#' + cdivIds[countCDiv]).attr('showNName');
          base64Cards.indName = $('#' + cdivIds[countCDiv]).attr('indName');
          base64Cards.svg = chartSvg;
          // barccards.push(base64Cards);
          paramModel.listOfSvgs.push(base64Cards);
          barccards.push(paramModel);
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
  selectedChartType(data) {
    return JSON.parse(JSON.stringify(data));
  }
  removeLegendReference(data) {
    return JSON.parse(JSON.stringify(data));
  }
  cardData(indicators) {
    // return (cardIndicatorVal);
    return JSON.parse(JSON.stringify(indicators));
  }

  /* getting keys for stack chart */
  getStackKeys(dataArr) {
    let allKeys = Object.keys(dataArr[0]);
    allKeys.forEach(element => {
      let addedKey = element;
      if (addedKey.includes('tooltipValue') || addedKey.includes('denominator') || addedKey.includes('numerator') || addedKey.includes('unit') || addedKey.includes('axis') || addedKey.includes('value')) {
        let index = allKeys.indexOf(addedKey);
        delete allKeys[index];
        allKeys = allKeys.filter(item => (addedKey.includes('tooltipValue') || addedKey.includes('denominator') || addedKey.includes('numerator') || addedKey.includes('unit') || addedKey.includes('axis') || addedKey.includes('value')))
      }
    });
    return allKeys;
  }
  getNullVal(textVal) {
    return textVal
  }

  convertToLineChartFormat(chartData) {
    for (let i = 0; i < chartData.length; i++) {
      const el = chartData[i];
      el.timeperiod = el.axis ? el.axis : el.timeperiod;
    }
    return chartData;
  }

}
