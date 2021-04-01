import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'removeArray'
})
export class RemoveArrayPipe implements PipeTransform {

  transform(items: any, args?: any): any {
    if(items){
     const filteredItems = items.filter(item => !args.includes(item))  
     return filteredItems;
    }
   }

}
