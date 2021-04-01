import { Injectable } from "@angular/core";

import { HttpClient, HttpRequest, HttpEvent } from "@angular/common/http";
import { Observable } from "rxjs";
import { Constants } from "src/app/constants";

@Injectable()
export class CommonServiceService {
  constructor(private httpClient: HttpClient) {}

  async getIndicatorList(fileName) {
    try
    {
    let indicators = (await this.httpClient
      .post( Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+"analytics/getCols", fileName)
      .toPromise()) as any[];
    return indicators;
    }
    catch(e)
    {
      return null;
    }
  }

  uploadFile(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();
    formdata.append("file", file);

    const req = new HttpRequest("POST", Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+"analytics/saveFile", formdata, {
      reportProgress: true,
      responseType: "text"
    });

    return this.httpClient.request(req);
  }

  discriptiveStatics(
    fileName,
    analysisType,
    indicatorName
  ): Observable<Object> {
    let analysis = {
      fileName: fileName,
      analysisType: analysisType,
      indicatorValue: indicatorName
    };
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+"analytics/singleDiscriptiveStatics", analysis);
  }

  downloadFile(filePath) {
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+"analytics/downloadFile", filePath, {
      responseType: "blob"
    });
  }

  outlierDataMatrix(analysis) {
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+"analytics/outlierDataMatrix", analysis);
  }

  independentLinerRegration(analysis) {
    return this.httpClient.post(
      Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+ "analytics/independentLinerRegration",
      analysis
    );
  }

  treatMissingValue(fileName) {
    return this.httpClient.post(Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+"analytics/treatMissingValues", fileName);
  }
}
