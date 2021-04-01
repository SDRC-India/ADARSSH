import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Constants } from 'src/app/constants';
import { StaticServiceService } from '../services/static-service.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TableDataFilterPipe } from '../filters/table-data-filter.pipe';
declare var $:  any;
import saveAs from "save-as";

@Component({
  selector: 'rmncha-review-submissions',
  templateUrl: './review-submissions.component.html',
  styleUrls: ['./review-submissions.component.scss']
})
export class ReviewSubmissionsComponent implements OnInit {

  staticService: StaticServiceService;
  errMessage: string;
  tableColumns: any;
  tableData: any;
  selectedFormId: number;
  selectedFormName: string;
  remarksforReject: any;
  submmittedTab: number;
  isRejectedTab: boolean = false;
  ispageLoad: boolean = true;
  errorMessage: string;
  info: any;

  constructor(private staticServiceProvider: StaticServiceService, private router: Router, private http: HttpClient) {
    this.staticService = staticServiceProvider
  }

  ngOnInit() {
    // this.reportService.reviewDetails = details;
    this.staticService.p = 1;
    this.staticService.getAllReviewForms().subscribe(data => {
      this.staticService.reviewDetails = data;
      this.selectedFormName = data[0].formName;
    })
    this.selectedFormId = 1; 
    if(this.ispageLoad)
    this.getAllSubmissions(NgForm);
  }
  /**
   * Based on tab select calls the submissions
   * @param id 
   * @param name 
  */
  formTabChanged(id,name) {   
    this.selectedFormId = id;
    this.selectedFormName = name;
    $("#submissionDetailsModal").modal('hide');
    if(!this.ispageLoad)
    this.getAllSubmissions(NgForm)
  }
  /**
   * Based on tab select calls the submissions
   * @param e 
  */
  tabChanged(e) {
    if (e.index == 2)
      this.isRejectedTab = true;
    else
      this.isRejectedTab = false;
    
    // this.getAllSubmissions(this.selectedFormId, NgForm)
  }
  /**
   * Lists all the submissions 
   * @param formId 
   * @param f 
  */
  getAllSubmissions(f = NgForm) {
    this.staticService.getAllSubmissionDatas(this.selectedFormId).subscribe(res => {
      this.staticService.reviewDetails.allSubmissions = res;      
      this.ispageLoad = false;  
      let tempTableData = this.staticService.reviewDetails.allSubmissions;
      let dynamicTableheightData: any;
      if (tempTableData.length > 0) {
        for (let obj of tempTableData) {
          let val: any = [];
          Object.keys(obj.formDataHead).sort().forEach(key => {
            val[key] = obj.formDataHead[key];
          })
          let allKeys = Object.keys(val);
          for (let i = 0; i < allKeys.length; i++) {
            //if ((obj.rejected && this.isRejectedTab) && allKeys[i] !== 'L3_Village') {
            if ((this.isRejectedTab)) {
              let columnNames = allKeys[i].split("_");
              obj[columnNames[1]] = obj.formDataHead[allKeys[i]];
            } else if (!this.isRejectedTab) {
              let columnName = allKeys[i].split("_");
              obj[columnName[1]] = obj.formDataHead[allKeys[i]];
            }
          }
        }
        this.tableData = tempTableData;
        if (this.isRejectedTab == true)
          dynamicTableheightData = new TableDataFilterPipe().transform(tempTableData, true);
        else if (this.isRejectedTab == false)
          dynamicTableheightData = new TableDataFilterPipe().transform(tempTableData, false);
      } else {
        this.tableData = "";
      }
      this.errMessage = "";
      if (this.tableData)
        this.tableColumns = Object.keys(this.tableData[0]);

      /** add page height based on table data shown */  
      if (this.tableData) {
        if (dynamicTableheightData.length > 6 && dynamicTableheightData.length <=8) {
          $('.btnTabs').addClass('submiitedTabs10');
        } else if (dynamicTableheightData.length > 8) {
          $('.btnTabs').addClass('submiitedTabs15');
          $('.btnTabs').removeClass('submiitedTabs10');
        } else {
          $('.btnTabs').removeClass('submiitedTabs10');
          $('.btnTabs').removeClass('submiitedTabs15');
        }
      }

    }, err => {
      this.errMessage = err.error.message;
    });
  }
  rejectSuccess() {
    $("#successMatch").modal('hide');
    this.staticService.viewMoreClicked = false;
    $("#submissionDetailsModal").modal('hide');
    this.getAllSubmissions(NgForm);
    // this.router.navigateByUrl("/pages/review-submission")
  }
  downloadTableAsExcel(submissions) {
    let submissionIds: any = [];
    if (submissions.tableData) {
      submissions.tableData.forEach(element => {
        submissionIds.push(element.extraKeys.submissionId);
      });
    }
    this.http.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'api/report/getRawDataReportFinal?formId='
     + this.selectedFormId + '&submissionIds=' + submissionIds, { responseType: "blob" }).subscribe((response:any) => {
      if(!response.size){
        this.info = "Data Not Available";
        $("#infoModal").modal('show');
      }else{
        saveAs(response,this.selectedFormName+".xlsx")
        this.info = '';
        $("#infoModal").modal('hide');
      }
    }, err => {
      console.log(err);
    });
    // window.location.href = saveAs("submissionId"+".xlsx");
  }
  getPaginatedData(val){
    this.getAllSubmissions(NgForm)
  }
  showLists(){    
    $(".left-list").attr("style", "display: block !important"); 
    $('.mob-left-list').attr("style", "display: none !important");
  }
  ngAfterViewInit(){
    $("input, textarea, .select-dropdown").focus(function() {
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#4285F4"})
      
    })
    $("input, textarea, .select-dropdown").blur(function(){
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#333"})
    })
    $('body,html').click(function(e){   
      if((window.innerWidth)<= 767){
      if(e.target.className == "mob-left-list"){
        return;
      } else{ 
          $(".left-list").attr("style", "display: none !important"); 
          $('.mob-left-list').attr("style", "display: block !important");  
      }
     }
    });   
  }

}
