import { NgModel } from '@angular/forms';
import { EventEmitter } from "@angular/core";
import { Output } from "@angular/core";
import { CommonServiceService } from "./../service/api/common-service.service";
import { HttpResponse } from "@angular/common/http";
import { HttpEventType } from "@angular/common/http";
import { Input } from "@angular/core";
import { Component, OnInit } from "@angular/core";
declare var $: any;
@Component({
  selector: "rmncha-outlayer",
  templateUrl: "./outlayer.component.html",
  styleUrls: ["./outlayer.component.scss"]
})
export class OutlayerComponent implements OnInit {
  @Input("discriptiveFileData")
  discriptiveFileData: any;

  file: any;

  fileName: string;
  progress: { percentage: number } = { percentage: 0 };
  selectedOutlayerType: any;
  currentFileUpload: boolean = false;

  finalUpload: any;
  outlayerFIleUpload: any;

  indicators: any;

  uploadedIndicators: any;

  selectedIndicators: any;

  checked: any;

  genratedOutlayerOutput: any;

  errorMessage: any;

  @Output()
  outLayerData = new EventEmitter();

  @Output()
  outLayerFile = new EventEmitter();

  selectAllValue=false;

  indicatorId:any[]=[-1]

  constructor(private commonService: CommonServiceService) {}

  searchValue:any;

  ngOnInit() {}

  selectAll(select: NgModel) {

    this.selectAllValue=!this.selectAllValue;
    if(this.selectAllValue)
    {

  
      select.update.emit(this.indicatorId); 
  }
  else
  {
    select.update.emit([])
  }
  }

  selectOne(select: NgModel)
  {
    if(this.selectAllValue)
    {
      let selection=select.value as number[];
      select.update.emit([]); 
      select.update.emit(selection.filter(d=> d!=-1)); 
      this.selectAllValue=!this.selectAllValue;
    }
    else
    {

     if(this.indicatorId.length-1==this.selectedIndicators.length)
    {
      select.update.emit(this.indicatorId);
      this.selectAllValue=!this.selectAllValue; 
    }

    
    }
  }


  onFileChange($event) {
    if ($event.srcElement.files[0]) {
      if (
        ($event.srcElement.files[0].name.split(".")[
          ($event.srcElement.files[0].name.split(".") as string[]).length - 1
        ] as String).toLocaleLowerCase() === "csv"
      ) {
        this.fileName = $event.srcElement.files[0].name;
        this.file = $event.srcElement.files[0];
        this.currentFileUpload = true;
        this.commonService.uploadFile(this.file).subscribe(event => {
          if (event.type === HttpEventType.UploadProgress) {
            this.progress.percentage = Math.round(
              (100 * event.loaded) / event.total
            );
          } else if (event instanceof HttpResponse) {
            this.currentFileUpload = false;
            this.finalUpload = event.body;
            this.outlayerFIleUpload = this.finalUpload;
            this.getIndicators(this.finalUpload);
            this.progress.percentage = 0;
          }
        });
      } else {
        $event.srcElement.value = null;
        this.errorMessage = "Upload a csv file";
        $("#outLayerError").modal("show");
      }
    }
  }

  uploadClicked() {
    $("#outlayerfileUpload").click();
  }

  async getIndicators(file) {
    try{
    this.indicators = await this.commonService.getIndicatorList(file);
    this.indicators = this.indicators.filter(d=>d.colType==1);
    this.uploadedIndicators = this.indicators
    this.outLayerFile.emit({
      file: this.finalUpload,
      indicators: this.indicators,
      fileName: this.fileName
    });
    this.indicatorId=[-1];
    this.indicators.forEach(element => {
      if(element.colType==1)
      this.indicatorId.push(element.id)
    });
  }
  catch(e)
  {
    this.errorMessage = "Some error Occured Please try after some time";
    $("#outLayerError").modal("show");
  }
  }

  getOutlier() {
    let analysis = {};
    let outputFileName=this.checked?this.discriptiveFileData.fileName:this.fileName;
    let selectedIndicatorsObject=[];
    let selectedIndicatorsName=[];
    // if(this.selectedIndicators.length>1)
    {
       selectedIndicatorsObject=this.indicators.filter(d=>this.selectedIndicators.indexOf(d.id)>-1)
       
    }
    selectedIndicatorsObject.forEach(d=> { selectedIndicatorsName.push(d.name);})

    // if (this.selectedOutlayerType == 2) {
      analysis = {
        fileName: this.finalUpload,
        indicators: selectedIndicatorsName
      };
    // } else {
    //   analysis = { fileName: this.finalUpload };
    // }

    this.commonService.outlierDataMatrix(analysis).subscribe(
      data => {
        this.genratedOutlayerOutput = data as ResponseModel;
        this.genratedOutlayerOutput.inputName=outputFileName;
        this.outLayerData.emit(this.genratedOutlayerOutput);
      },
      error => {
        this.errorMessage = "Some error Occured Please try after some time";
        $("#outLayerError").modal("show");
      }
    );
  }

  fileChecked($event) {
    
    if (this.checked) {
      this.indicators = null;
      this.indicators = this.discriptiveFileData.indicators;
  
      this.finalUpload = this.discriptiveFileData.file;
    } else {
      this.indicators = null;
      this.indicators = this.uploadedIndicators;
   
      this.finalUpload = this.outlayerFIleUpload;
    }

    this.indicatorId=[-1];
    this.indicators.forEach(element => {
      if(element.colType==1)
      this.indicatorId.push(element.id)
    });
   
    this.outLayerFile.emit({
      file: this.finalUpload,
      indicators: this.indicators,
      fileName: this.checked ? this.discriptiveFileData.fileName : this.fileName
    });
  }

  modalClose() {
    $("#outLayerError").modal("hide");
  }
}
