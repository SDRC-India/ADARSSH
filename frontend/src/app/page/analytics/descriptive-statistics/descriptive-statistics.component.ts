import { CommonServiceService } from "./../service/api/common-service.service";
import { Component, OnInit, Input, Output, EventEmitter } from "@angular/core";
import { HttpEventType, HttpResponse } from "@angular/common/http";
import { NgModel } from "@angular/forms";
declare var $: any;
@Component({
  selector: "rmncha-descriptive-statistics",
  templateUrl: "./descriptive-statistics.component.html",
  styleUrls: ["./descriptive-statistics.component.scss"]
})
export class DescriptiveStatisticsComponent implements OnInit {
  indicators: any;
  selectedIndicator: any;
  fileName: string;
  progress: { percentage: number } = { percentage: 0 };
  selectedAnalysisType: any;
  currentFileUpload: boolean = false;

  analysisResult: any;
  selectedIndicators: any;
  finalUpload: any;

  categoricalInidcatorId: any[] = [-1];

  numericIndicatorId: any[] = [-1];

  file: any;

  genratedOutput: ResponseModel;

  errorMessage: any;

  searchValue: any;

  selectAllValue = false;

  @Output()
  discriptiveData = new EventEmitter();

  @Output()
  discriptiveFile = new EventEmitter();

  constructor(private commonService: CommonServiceService) { }

  ngOnInit() { }

  selectAll(select: NgModel) {

    this.selectAllValue = !this.selectAllValue;
    if (this.selectAllValue) {
      if (this.selectedAnalysisType == 1) {
        select.update.emit(this.numericIndicatorId);
      }
      else {
        select.update.emit(this.categoricalInidcatorId);
      }
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
      if (this.selectedAnalysisType == 1 && this.numericIndicatorId.length - 1 == this.selectedIndicators.length) {
        select.update.emit(this.numericIndicatorId);
        this.selectAllValue = !this.selectAllValue;

      }
      else if (this.categoricalInidcatorId.length - 1 == this.selectedIndicators.length) {
        select.update.emit(this.categoricalInidcatorId);
        this.selectAllValue = !this.selectAllValue;

      }

    }
  }

  async getIndicators(file) {
    try {
      this.indicators = await this.commonService.getIndicatorList(file);
      this.indicators.forEach(element => {
        if (element.colType == 1)
          this.numericIndicatorId.push(element.id)
        else
          this.categoricalInidcatorId.push(element.id)
      });
      this.discriptiveFile.emit({
        file: this.finalUpload,
        indicators: this.indicators,
        fileName: this.fileName
      });
    }
    catch (e) {
      this.errorMessage = "Some error Occured Please try after some time";
      $("#error").modal("show");
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
            this.getIndicators(this.finalUpload);
            this.progress.percentage = 0;
          }
        });
      } else {
        $event.srcElement.value = null;
        this.errorMessage = "Upload a csv file";
        $("#error").modal("show");
      }
    }
  }

  uploadClicked() {
    $("#fileUpload").click();
  }

  singleStatics() {
    let outputFileName = this.fileName;
    this.commonService
      .discriptiveStatics(
        this.finalUpload,
        this.selectedAnalysisType,
        this.selectedIndicator
      )
      .subscribe(
        data => {
          if (data) {
            this.genratedOutput = data as ResponseModel;
            this.genratedOutput.fileName = outputFileName;
            this.discriptiveData.emit(this.genratedOutput);
          } else {
            switch (this.selectedAnalysisType) {
              case "1":
                this.errorMessage = "Select a numerical type variable";
                break;

              case "2":
                this.errorMessage = "Select a categorical type variable";
                break;
            }
            $("#error").modal("show");
          }
        },
        error => {
          this.errorMessage = "Some error Occured Please try after some time";
          $("#error").modal("show");
        }
      );
  }


  multipleStatics() {
    let outputFileName = this.fileName;
    let selectedIndicatorsObject=[];
    let selectedIndicatorsName=[];
    if(this.selectedIndicators.length>1)
    {
       selectedIndicatorsObject=this.indicators.filter(d=>this.selectedIndicators.indexOf(d.id)>-1)
       
    }
    selectedIndicatorsObject.forEach(d=> { selectedIndicatorsName.push(d.name);})
    this.commonService
      .discriptiveStatics(
        this.finalUpload,
        this.selectedAnalysisType,
        this.selectedIndicators.length>1?selectedIndicatorsName:this.selectedIndicators
      )
      .subscribe(
        data => {
          if (data) {
            this.genratedOutput = data as ResponseModel;
            this.genratedOutput.inputName = outputFileName;
            this.discriptiveData.emit(this.genratedOutput);
          } else {
            switch (this.selectedAnalysisType) {
              case "1":
              case "1":
                this.errorMessage = "Select a numerical type variable";
                break;

              case "2":
                this.errorMessage = "Select a categorical type variable";
                break;
            }
            $("#error").modal("show");
          }
        },
        error => {
          this.errorMessage = "Some error Occured Please try after some time";
          $("#error").modal("show");
        }
      );
  }

  modalClose() {
    $("#error").modal("hide");
  }
}
