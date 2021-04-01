import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Constants } from 'src/app/constants';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class CmsServiceService {

  constructor(private httpClient : HttpClient) { }
  
  getCMSleftData():any{
    // return this.httpClient.get('assets/cmsData.json');
    return this.httpClient.get(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL  +'getCMSData');
  }
  getCMSRightData(){
    return this.httpClient.get('assets/cmscontent.json');
  }

  uploadFile(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();
    formdata.append('file', file);
    const req = new HttpRequest('POST', Constants.DASHBOARD_FILE_UPLOAD_URL  + 'uploadFile', formdata, {
      reportProgress: true
    });

    return this.httpClient.request(req);
  }

  saveUpdateData(data):Observable<any>
  {
   return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL  +'saveUpdateData',data)
  }

  deleteData(data): Observable<any> {
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL + 'deleteData', data)
  }

}
