import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'columnPipe'
})
export class ColumnPipePipe implements PipeTransform {

  transform(value: any, args?: any,search?:any): any {

    if(args)
    {
      if(search)
      return value?value.filter(d=>d.colType==args&&(d.name as string).toLocaleLowerCase().includes(search.toLowerCase())):value
    
      else
      return value?value.filter(d=>d.colType==args):value
    }
    else if(search)
    {
      return value?value.filter(d=>(d.name as string).toLocaleLowerCase().includes(search.toLowerCase())):value
    }

    else
    {
      return value;
     
    }
   
  }

}
