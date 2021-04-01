import { NgModel } from '@angular/forms';
import { Input, OnChanges, Output, EventEmitter } from "@angular/core";
import { CommonServiceService } from "./../service/api/common-service.service";
import { HttpResponse } from "@angular/common/http";
import { HttpEventType } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
declare var $: any;
@Component({
  selector: "rmncha-regration",
  templateUrl: "./regration.component.html",
  styleUrls: ["./regration.component.scss"]
})
export class RegrationComponent implements OnInit, OnChanges {
  selectedRegrationType: any;

  checked: any;

  file: any;

  fileName: string;
  progress: { percentage: number } = { percentage: 0 };
  currentFileUpload: boolean = false;

  finalUpload: any;
  errorMessage: any;
  indicators: any;

  uploadedIndicators: any;
  regrationFIleUpload: any;

  @Input("discriptiveFileData")
  discriptiveFileData: any;

  @Input("outlayerFileData")
  outlayerFileData: any;

  finalInputFileData: any;

  selectedIndicator: any;

  @Input("missingValueFileData")
  missingValueFileData: any;

  @Output()
  regrationFile = new EventEmitter();

  @Output()
  regrationData = new EventEmitter();

  selectedIndicatorFirst: any;

  selectedIndicatorSecond: any;

  genratedRegrationOutput: ResponseModel;

  selectAllValue=false;

  indicatorId:any[]=[-1]

  constructor(private commonService: CommonServiceService) {}

  searchValue:any;

  singleSearchValue:any;


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

     if(this.indicatorId.length-1==this.selectedIndicatorSecond.length)
    {
      select.update.emit(this.indicatorId);
      this.selectAllValue=!this.selectAllValue; 
    }

    
    }
  }


  ngOnChanges() {
    this.finalInputFileData = this.missingValueFileData
      ? this.missingValueFileData
      : this.outlayerFileData
        ? this.outlayerFileData
        : this.discriptiveFileData;
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
            this.regrationFIleUpload = this.finalUpload;
            this.getIndicators(this.finalUpload);
            this.progress.percentage = 0;
          }
        });
      } else {
        $event.srcElement.value = null;
        this.errorMessage = "Upload a csv file";
        $("#regrationError").modal("show");
      }
    }
  }

  uploadClicked() {
    $("#regrationfileUpload").click();
  }

  async getIndicators(file) {
    try
    {
    this.indicators = await this.commonService.getIndicatorList(file);
    this.uploadedIndicators = this.indicators;
    this.indicatorId=[-1]
    this.indicators.forEach(element => {
      if(element.colType==1)
      this.indicatorId.push(element.id)
     
    });
    this.regrationFile.emit({
      file: this.finalUpload,
      indicators: this.indicators,
      fileName: this.fileName
    });
  }
  catch(e)
  {
    this.errorMessage = "Some error Occured Please try after some time";
    $("#regrationError").modal("show");
  }
  }

  fileChecked($event) {
    if (this.checked) {
      this.indicators = null;
      this.indicators = this.finalInputFileData.indicators;
      this.finalUpload = this.finalInputFileData.file;
    } else {
      this.indicators = null;
      this.indicators = this.uploadedIndicators;
      this.finalUpload = this.regrationFIleUpload;
    }
    this.indicatorId=[-1]
    this.indicators.forEach(element => {
      if(element.colType==1)
      this.indicatorId.push(element.id)
     
    });

    this.regrationFile.emit({
      file: this.finalUpload,
      indicators: this.indicators,
      fileName: this.checked ? this.finalInputFileData.fileName : this.fileName
    });
  }

  modalClose() {
    $("#regrationError").modal("hide");
  }

  getLinerRegration() {
    let outputFileName=this.checked?this.finalInputFileData.fileName:this.fileName;
    let analysis = {};


    let selectedIndicatorsObject=[];
    let selectedIndicatorsName=[];
    {
       selectedIndicatorsObject=this.indicators.filter(d=>this.selectedIndicatorSecond.indexOf(d.id)>-1)
     
    }
    selectedIndicatorsObject.forEach(d=> { selectedIndicatorsName.push(d.name);})
    if(selectedIndicatorsName.indexOf(this.selectedIndicatorFirst)<0)
    {
      selectedIndicatorsName.push(this.selectedIndicatorFirst)
    }
    
      analysis = {
        fileName: this.finalUpload,
        indicator: this.selectedIndicatorFirst,
        secondaryIndicator:selectedIndicatorsName
      };
    

    this.commonService.independentLinerRegration(analysis).subscribe(
      data => {
        if(data)
        {
        this.genratedRegrationOutput = data as ResponseModel;
        this.genratedRegrationOutput.inputName=outputFileName;
        this.regrationData.emit(this.genratedRegrationOutput);
        }
        else
        {
        this.errorMessage = "Some error Occured Please try after some time";
        $("#regrationError").modal("show");
        }
      },
      error => {
        this.errorMessage = "Some error Occured Please try after some time";
        $("#regrationError").modal("show");
      }
    );
  }
}
