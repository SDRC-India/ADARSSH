import { CommonServiceService } from "./../service/api/common-service.service";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import saveAs from "save-as";
declare var $: any;

@Component({
  selector: "rmncha-analytics-home",
  templateUrl: "./analytics-home.component.html",
  styleUrls: ["./analytics-home.component.scss"]
})
export class AnalyticsHomeComponent implements OnInit {


  descriptiveForm: FormGroup;
  outlayerForm: FormGroup;
  missingValueForm: FormGroup;
  regrationForm: FormGroup;

  discriptiveResult: ResponseModel;

  discriptiveFileData: any;

  outlayerResult: ResponseModel;

  outlayerFileData: any;

  missingValueFileData: any;

  regrationFileData: any;

  regrationResult: any;

  missingResult: any;

  isLinear:boolean=false;

  errorMessage:String;

  constructor(
    private _formBuilder: FormBuilder,
    private commonServiceService: CommonServiceService
  ) {}

  ngOnInit() {
    this.descriptiveForm = this._formBuilder.group({
      firstCtrl: [this.discriptiveResult, Validators.required]
    });
    this.outlayerForm = this._formBuilder.group({
      firstCtrl: [this.outlayerResult, Validators.required]
    });
    this.missingValueForm = this._formBuilder.group({
      firstCtrl: [this.missingResult, Validators.required]
    });

    this.regrationForm = this._formBuilder.group({
      firstCtrl: [this.regrationResult, Validators.required]
    });
  }

  discriptiveData($event) {
    this.discriptiveResult = $event as ResponseModel;
  }

  downloadFile(fileData: ResponseModel) {
    this.commonServiceService.downloadFile(fileData.filePath).subscribe(
      data => {
        saveAs(data, fileData.fileName);
      },
      error => {
        this.errorMessage = "Looks like file has been deleted form server.";
        $("#divError").modal("show");
      }
    );
  }
  downloadOutputFile(fileData: ResponseModel) {
    this.commonServiceService.downloadFile(fileData.outputFilePath).subscribe(
      data => {
        saveAs(data, fileData.fileName);
      },
      error => {
        this.errorMessage = "Looks like file has been deleted form server.";
        $("#divError").modal("show");
      }
    );
  }

  discriptiveFile($event) {
    this.discriptiveFileData = $event as any;
  }

  outLayerData($event) {
    this.outlayerResult = $event as ResponseModel;
  }

  outLayerFile($event) {
    this.outlayerFileData = $event as any;
  }

  regrationFile($event) {
    this.regrationFileData = $event as any;
  }

  regrationData($event) {
    this.regrationResult = $event as any;
  }

  missingValueFile($event) {
    this.missingValueFileData = $event as any;
  }

  missingValueData($event) {
    this.missingResult = $event as ResponseModel;
  }

  modalClose() {
    $("#divError").modal("hide");
  }
}
