import { Injectable } from '@angular/core';

/**
 * This provider will help to keep all the constants
 * @author Jagat Bandhu
 * @since 0.0.1
 */
@Injectable()
export class ConstantProvider {


  static baseUrl : string = 'https://adarssh.in/si-rmncha-collection/';
  // static baseUrl : string = 'https://testserver.sdrc.co.in:8443/si-rmncha-pwa';
    // static baseUrl : string = 'https://devserver.sdrc.co.in/rani/';
      //  static baseUrl : string = 'https://testserver.sdrc.co.in:8443/si-rmncha-collection/';
  //  static baseUrl : string = 'https://uat.sdrc.co.in/si-rmncha-collection/';
  // static baseUrl : string = 'https://testserver.sdrc.co.in:8443/si-rmncha-collection/'
    //  static baseUrl : string = 'http://192.168.1.254:8765/si-rmncha-gateway/service/si-rmncha-collection/';
    // static baseUrl : string = 'http://localhost:8082/si-rmncha-collection/';
    //  static baseUrl : string = 'http://localhost:8082/si-rmncha-collection/';



    // static baseUrl : string = 'http://localhost:8080/bypass/';


    static defaultImage: string ='assets/imgs/uploadImage.png';
    static message: IMessages = {
    checkInternetConnection: "Please check your internet connection.",
    serverError:"Error connecting to server ! Please try after some time.",
    networkError: 'Server error.',
    pleaseWait: 'Please wait..',
    validUserName: 'Please enter username.',
    validPassword:'Please enter Password.',
    dataClearMsg:'Last user saved data will be erased. Are you sure you want to login?',
    invalidUser:'No data entry facility available for state and national level user.',
    invalidUserNameOrPassword:'Invalid usename or password.',
    syncingPleaseWait: 'Syncing please wait...',
    syncSuccessfull: 'Sync Successful.',
    getForm: 'Fetching forms from server, please wait...',
    warning: 'Warning',
    deleteFrom: 'Do you want to delete the selected record?',
    saveSuccess: 'Saved Successfully.',
    finalizedSuccess: 'Finalized Successfully.',
    fillAtleastOnField: 'Please fill data of atleast one field',
    autoSave: 'Auto save Successfully',
    anganwadiCenter: 'Please select the anganwadi center number.',
    schoolname: 'Please enter the school name.',
    respondentName: 'Please enter the respondent name.',
    womanName: 'Please enter the woman name.',
    errorWhileClearingFile: 'Error while deleting data of previous user.',
    clearingDataPleaseWait: 'Clearing data, please wait...',
    formUpdationSuccess: 'Form Updated Successfully',
    formUpdationNotFound: 'No forms to update',
    formUpdating: 'Checking for updates, Please wait... '
  }
  static dbKeyNames: IDBKeyNames = {
    // user: "user",
    form: "form",
    // getAllForm: "getAllForm",
    // getBlankForm: "getBlankForm",
    submissionData:"submissionData",
    dataToSend:'dataToSend',
    // loginResponse: 'loginResponse'
    userAndForm : 'userAndForm',
    deadLineDate: 'deadLineDate'

  }

  static appFolderName: string ="RMNCHA";
  static lastUpdatedDate : string  = "1970-07-03 12:08:14";
}
