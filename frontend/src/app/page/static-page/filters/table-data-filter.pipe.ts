import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'tableDataFilter'
})
export class TableDataFilterPipe implements PipeTransform {

  transform(data: any, args?: any): any {
    return data.filter(datas => datas.rejected == args)
  }

}
