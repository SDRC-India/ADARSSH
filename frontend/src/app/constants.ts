export class Constants {
  public static get API_GATE_WAY(): string {return '/si-rmncha-gateway/'; }
  public static defaultImage:string;
  public static get SERVICE_GATE_WAY(): string {return 'service/'; }
  // public static get API_GATE_WAY(): string {return '/'; }
  // public static get HOME_URL(): string {return 'si-rmncha-collection/'; }
  public static get COLLECTION_SERVICE_URL(): string {return Constants.API_GATE_WAY+ Constants.SERVICE_GATE_WAY + 'si-rmncha-collection/'; }
  public static get FILE_UPLOAD_URL(): string {return Constants.API_GATE_WAY+ 'zuul/'+Constants.SERVICE_GATE_WAY + 'si-rmncha-collection/';  }
  public static get DASHBOARD_FILE_UPLOAD_URL(): string {return Constants.API_GATE_WAY+ 'zuul/'+Constants.SERVICE_GATE_WAY + 'si-rmncha-dashboard/';  }
  public static get ACCESS_TOKEN(): string{return 'rmncha_access_token';}
  public static get REFRESH_TOKEN(): string{return 'rmncha_refresh_token';}
  public static get USER_DETAILS(): string{return 'rmncha_user_details';}
  public static get DASHBOARD_SERVICE_URL(): string {return 'si-rmncha-dashboard/'; }
  public static get DOWNLOAD_TEMPLATE(): string {return 'Please download the template from here to update the data, only '
  + 'the template downloaded from here will accepted.'; }
  public static get UPLOAD_TEMPLATE(): string {return 'Please upload the template downloaded '
   + 'from this page. Any other template or a blank template will get rejected'; }
   public static get SERVER_ERROR_MESSAGE(): string {return 'Could not connect to server. Please try again'; }
   public static get IMPORT_DATA_REQUEST_SUCCESS_MESSAGE(): string {return 'Data import request has been sent successfully'; }
   public static get IMPORT_DATA_REQUEST_ERROR_MESSAGE(): string {return 'Server returned an error, please wait for the server response'; }
}