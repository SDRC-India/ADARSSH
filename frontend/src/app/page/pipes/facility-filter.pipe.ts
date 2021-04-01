import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'facilityFilter'
})
export class FacilityFilterPipe implements PipeTransform {

  transform(facilities: any[], formId?:any): any {
    if(!formId)
    {
      return [];
    }
    else
    {
      return facilities[formId];
    }
  }

}
