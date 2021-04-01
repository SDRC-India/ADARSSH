import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Constants } from 'src/app/constants';
import { RequestModel } from 'src/app/models/request-model';

@Injectable({
  providedIn: 'root'
})
export class CommonService {

  constructor(private httpClient: HttpClient) { }

  downloadTemplate() {
    return this.httpClient.post(Constants.API_GATE_WAY +Constants.SERVICE_GATE_WAY+ Constants.DASHBOARD_SERVICE_URL + 'api/v1/downloadExcelTemplate', '', {
      responseType: 'blob'
    });
  }

  uploadFile(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    const req = new HttpRequest('POST', Constants.DASHBOARD_FILE_UPLOAD_URL + 'api/v1/uploadTemplate', formdata, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.httpClient.request(req);
  }


  validateData(filePath: string): Observable<ResponseModel> {
    return this.httpClient.post<ResponseModel>(Constants.API_GATE_WAY +Constants.SERVICE_GATE_WAY+ Constants.DASHBOARD_SERVICE_URL + 'api/v1/validate', filePath);
  }


  async importData(requestModel: RequestModel) {
    return this.httpClient.post<ResponseModel>(
      Constants.DASHBOARD_FILE_UPLOAD_URL  + 'api/v1/importData',
      requestModel
    ).toPromise();
  }
}
