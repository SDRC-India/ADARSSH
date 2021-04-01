import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DataSharingServiceService {

  allformData: any;
  selectedSection: string = 'a';
  sectionNames = [];
  questionKeyValueMap : {} ={}
  constructor() {
    this.selectedSection = "a"
  }

  /*
    This functions are used to retrieve keys and type of any objectfrom html
  */
  getKeys(obj): any[]{
    return Object.keys(obj);
  }

  getType(value):string{
    return typeof(value);
  }
}
