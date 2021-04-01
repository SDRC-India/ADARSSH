import { Component } from '@angular/core';
import { CommonService } from '../service/common.service';
import saveAs from 'save-as';
import { Constants } from 'src/app/constants';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { CommonServiceService } from '../../analytics/service/api/common-service.service';
import * as status from 'http-status-codes';
import { MessagingService } from 'src/app/shared/messaging.service';
import { RequestModel } from 'src/app/models/request-model';
declare var $: any;
@Component({
  selector: 'rmncha-template-download-upload',
  templateUrl: './template-download-upload.component.html',
  styleUrls: ['./template-download-upload.component.scss']
})
export class TemplateDownloadUploadComponent{

  errorMessage: string;
  temlateDownloadDisclamer:string = Constants.DOWNLOAD_TEMPLATE;
  templateUploadDisclamer:string = Constants.UPLOAD_TEMPLATE;
  progress: { percentage: number } = { percentage: 0 };
  currentFileUpload: boolean = false;
  fileName:string = '';
  file:any;
  finalUpload:any;
  validated:boolean = false;
  validateResponse:ResponseModel;
  uploadedResponse:ResponseModel;
  sucessMessage:string = '';
  infoMessage:string = ''
  message: any;
  publishedMessage: any;
  requestModel: RequestModel = new RequestModel();
  constructor(private commonService: CommonService,
    private commonServiceService: CommonServiceService,public messagingService: MessagingService) { }

  onFileChange($event) {
    if ($event.srcElement.files[0]) {
      if (
        ($event.srcElement.files[0].name.split('.')[
          ($event.srcElement.files[0].name.split('.') as string[]).length - 1
        ] as String).toLocaleLowerCase() ===  'xlsx'
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
            this.validateResponse = null;
            this.validated = false;
            this.finalUpload = event.body;
            this.progress.percentage = 0;
          }
        }, error => {
          this.currentFileUpload = false;
          this.validateResponse = null;
          this.validated = false;
          this.progress.percentage = 0;
          this.errorMessage = Constants.SERVER_ERROR_MESSAGE;
          $('#error').modal('show');
       });
      } else {
        $event.srcElement.value = null;
        this.errorMessage = 'Upload a xlsx file';
        $('#error').modal('show');
      }
    }
  }
  uploadClicked() {
    $('#fileUpload').click();
  }
  downloadClicked() {
   this.commonService.downloadTemplate().subscribe(
    data => {
      saveAs(data, 'dataentry_template_rmncha' + Date.now() + '.xlsx');
    },
    error => {
       this.errorMessage = Constants.SERVER_ERROR_MESSAGE;
       $('#error').modal('show');
    }
   );
  }

  uploadedFile() {
    saveAs(this.file, this.fileName);
  }
  validateData() {
  this.commonService.validateData(this.finalUpload).subscribe(data => {
    this.validateResponse = data;
    this.requestModel.excelPath = this.validateResponse.filePath;
      if (this.validateResponse.statusCode === status.OK) {
        this.validated = true;
        this.sucessMessage = this.validateResponse.message;
        $('#sucess').modal('show');
      } else {
        this.validated = false;
        this.errorMessage = this.validateResponse.message;
        $('#error').modal('show');
      }
    }, error =>  {
      this.errorMessage = Constants.SERVER_ERROR_MESSAGE;
      $('#error').modal('show');
    });
  }

  importData() {
    this.requestModel.fcmToken = this.messagingService.messageToken;
    this.commonService.importData(this.requestModel).then(e => {
      this.infoMessage = Constants.IMPORT_DATA_REQUEST_SUCCESS_MESSAGE;
      $('#info').modal('show');
    },error=>{
      this.errorMessage = Constants.IMPORT_DATA_REQUEST_ERROR_MESSAGE;
      $('#error').modal('show');
    });
  }

  downloadOutputFile(fileData: ResponseModel) {
    this.commonServiceService.downloadFile(fileData.filePath).subscribe(
        data => {
          saveAs(data, fileData.fileName);
        },
        error => {
          this.errorMessage = Constants.SERVER_ERROR_MESSAGE;
          $('#divError').modal('show');
        }
      );
    }
  }
