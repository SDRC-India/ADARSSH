import { Pipe, PipeTransform } from '@angular/core';

/**
 * Generated class for the SortItemPipe pipe.
 *
 * See https://angular.io/api/core/Pipe for more info on Angular Pipes.
 */
@Pipe({
  name: 'sortItem',
})
export class SortItemPipe implements PipeTransform {
  transform(areas: any[], ...args): any[] {

    if(args!=null && args[0]!=null){
      if(areas != undefined && areas != null && areas.length > 0){
        areas.sort((area1: any, area2: any) => {
          var area1Name = area1.value.toLowerCase(), area2Name = area2.value.toLowerCase()
          if (area1Name < area2Name) //sort string ascending
            return -1
          if (area1Name > area2Name)
            return 1
          return 0 //default return value (no sorting)
        })
      
      }
  }
  return areas;
  }

}
