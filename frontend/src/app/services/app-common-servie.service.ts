import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AppCommonServieService {

  constructor() { }

  getConstantsObject():Constants
  {
    let constants:Constants={
      copyRightText:' IPE GLOBAL LIMITED'
    };
    return constants;

  }

}
