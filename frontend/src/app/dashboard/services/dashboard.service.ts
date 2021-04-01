import { Injectable } from '@angular/core';
import { Constants } from 'src/app/constants';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {


  coverageDashboard: any = {};
  snapshotView: any = {};
  allcheckLists: any;
  checkLists:any;
  timeperiodLists: any;
  checkListDetails: any;  
  formFieldsAll: any;
  areaDetails: any;
  userLevelID: number[] = [];
  selectedStateName: string;
  selectedDistrictName: string;
  selectedBlockName: string;
  userLevelName: string;
  selectedTimePeriodName: string;
  checkListObj:any;
  sectionObj: any;
  selectedDistrictId: number;
  selectedBlockId: number;
  facilityTypeName: string;
  facilityLevelName: string;
  facilityTypeLists: any;
  facilityLevelLists: any;
  constructor(private httpClient: HttpClient) { }

  getAllTimePeriod(isAggregate?) {
    if(isAggregate){
      return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'getTimePeriod?isAggregate='+isAggregate);
     } else{
        return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'getTimePeriod');
      }
  }

  getAllFacilityTypeAndLevel(formdId, typeName) {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'getTypeDetailsByForm?formId='+formdId +'&typeName='+typeName);
  }

  getCoverageData(levelId, areaId) {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'getCoverageData?areaLevel=' + levelId + '&areaId=' + areaId + '&dashboardType=COVERAGE');
  }

  isCoveargeDataAvailable() {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'isCoveargeDataAvailable');
  }
  getIndicatorData(levelId, areaId, sectorId, timperiodId,formId,typeId, flevelId) {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'getCoverageData?areaLevel=' + levelId + '&areaId=' + areaId + '&sectorId=' +sectorId + '&tpId='+timperiodId + 
    '&formId=' +formId+ '&dashboardType=SNAPSHOT' +(typeId?'&typeId=' +typeId:'' )+ (flevelId?'&levelId=' +flevelId:''));
    // return this.httpClient.get('assets/district-assessment-dashboard-data.json');
  }

  // checklist data for snapshot view
  getCheckListData() {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'getAllChecklistSectors');
  }
  getUserRoles() {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'bypass/getAreaLevels');
  }

  // get all Area for new user management
  getAreaDetails() {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'bypass/getAllArea');
  }

  // stacked Bar json
   getStackedBarDetails() {
    return this.httpClient.get("assets/stackedBarChart2.json");
  }
  getStackedBarLegends() {
    return this.httpClient.get("assets/legendsForStackedBar.json");
  }
  /*** Download full page as pdf*/
  
  downloadFullpageSvg(listOfSvgs,areaLevelname,stateName, districtName, blockName,type, checkListName, timePeriod) {
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 
      'downloadChartDataPDF?districtName=' + (districtName?districtName:null)
       + '&blockName=' + (blockName?blockName:null) + '&stateName='+(stateName?stateName:null)
       + '&areaLevel=' +(areaLevelname?areaLevelname:null ) + '&dashboardType=' +type + '&checkListName=' +checkListName
       + '&timePeriod=' +timePeriod
       , (listOfSvgs?listOfSvgs:null), {
      responseType: "blob"
    }
    );
  }

  /*** Download full page as excel*/
  downloadFullpageExcel(listOfSvgs) {
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'downloadChartDataExcel',
    (listOfSvgs?listOfSvgs:null), {responseType: "blob"}
    );
  }

  aggregateLegacyData(tp,tpName, periodicity) {
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL 
      + 'legacyAggregate?tp='+tp+'&tpName='+tpName +'&periodicity='+periodicity);
  }

  getAllLegacyRecordStatus(){
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL 
      + 'getAllLegacyRecordStatus');
  }
}



