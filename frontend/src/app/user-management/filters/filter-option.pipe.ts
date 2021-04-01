import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filterOption'
})
export class FilterOptionPipe implements PipeTransform {

  transform(value: any[],searchText: string,keySearch:string): any {
    if(searchText)
    return value.filter(d=>d[keySearch].toLocaleLowerCase().includes(searchText.toLocaleLowerCase()))
    else
    return value;
  }

}
