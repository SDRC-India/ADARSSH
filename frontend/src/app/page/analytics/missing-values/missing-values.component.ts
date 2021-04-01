import { NgModel } from '@angular/forms';
import { HttpEventType, HttpResponse } from "@angular/common/http";
import { Output, EventEmitter, OnChanges, ViewChild } from "@angular/core";
import { Input } from "@angular/core";
import { Component, OnInit } from "@angular/core";
import { CommonServiceService } from "../service/api/common-service.service";
declare var $: any;

@Component({
  selector: "rmncha-missing-values",
  templateUrl: "./missing-values.component.html",
  styleUrls: ["./missing-values.component.scss"]
})
export class MissingValuesComponent implements OnInit, OnChanges {
  selectedRegrationType: any;
  checked: any;
  file: any;
  fileName: string;
  progress: { percentage: number } = { percentage: 0 };
  currentFileUpload: boolean = false;
  finalUpload: any;
  errorMessage: any;
  indicators: any;
  missValueFileUpload: any;
  uploadedIndicators: any;
  regrationFIleUpload: any;
  selectedIndicators: any;

  @Input("discriptiveFileData")
  discriptiveFileData: any;

  @Input("outlayerFileData")
  outlayerFileData: any;

  @Output()
  missingValueFile = new EventEmitter();

  @Output()
  missingValueData = new EventEmitter();

  finalInputFileData: any;

  selectedIndicator: any;

  genratedMissingValueOutput: ResponseModel;

  selectAllValue = false;

  indicatorId: any[] = [-1]

  constructor(private commonService: CommonServiceService) { }

  searchValue: any;


  ngOnInit() { }

  selectAll(select: NgModel) {

    this.selectAllValue = !this.selectAllValue;
    if (this.selectAllValue) {
      select.update.emit(this.indicatorId);
    }
    else {
      select.update.emit([])
    }
  }

  selectOne(select: NgModel) {
    if (this.selectAllValue) {
      let selection = select.value as number[];
      select.update.emit([]);
      select.update.emit(selection.filter(d => d != -1));
      this.selectAllValue = !this.selectAllValue;
    }
    else {
      if (this.indicatorId.length - 1 == this.selectedIndicators.length) {
        select.update.emit(this.indicatorId);
        this.selectAllValue = !this.selectAllValue;
      }
    }
  }


  ngOnChanges() {
    this.finalInputFileData = this.outlayerFileData ? this.outlayerFileData : this.discriptiveFileData;
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

    this.indicatorId = [-1];
    this.indicators.forEach(element => {
      this.indicatorId.push(element.id)
    });

    this.missingValueFile.emit({
      file: this.finalUpload,
      indicators: this.indicators,
      fileName: this.checked ? this.finalInputFileData.fileName : this.fileName
    });
  }

  uploadClicked() {
    $("#missingfileUpload").click();
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
            this.missValueFileUpload = this.finalUpload;
            this.getIndicators(this.finalUpload);
            this.progress.percentage = 0;
          }
        });
      } else {
        $event.srcElement.value = null;
        this.errorMessage = "Upload a csv file";
        $("#missingError").modal("show");
      }
    }
  }

  async getIndicators(file) {
    try {
      this.indicators = await this.commonService.getIndicatorList(file);
      this.uploadedIndicators = this.indicators;
      this.missingValueFile.emit({
        file: this.finalUpload,
        indicators: this.indicators,
        fileName: this.fileName
      });

      this.indicators.forEach(element => {
        this.indicatorId.push(element.id)
      });
    }
    catch (e) {
      this.errorMessage = "Some error Occured Please try after some time";
      $("#missingError").modal("show");
    }

  }

  treatMissingValue() {
    let selectedIndicatorsObject=[];
    let selectedIndicatorsName=[];
    selectedIndicatorsObject=this.indicators.filter(d=>this.selectedIndicators.indexOf(d.id)>-1)
       
    selectedIndicatorsObject.forEach(d=> { selectedIndicatorsName.push(d.name);})

    let outputFileName = this.checked ? this.finalInputFileData.fileName : this.fileName;

    let analysis = {
      fileName: this.finalUpload,
      indicators: selectedIndicatorsName
    };

    this.commonService.treatMissingValue(analysis).subscribe(
      data => {
        this.genratedMissingValueOutput = data as ResponseModel;
        this.genratedMissingValueOutput.inputName = outputFileName;
        this.missingValueData.emit(this.genratedMissingValueOutput);
      },
      error => {
        this.errorMessage = "Some error Occured Please try after some time";
        $("#missingError").modal("show");
      }
    );
  }

  modalClose() {
    $("#missingError").modal("hide");
  }
}
