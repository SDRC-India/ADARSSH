import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'tableAreasFilter'
})
export class TableAreasFilterPipe implements PipeTransform {

  transform(data: any, args?: any): any {
    return data.filter(datas => datas.extraKeys.status == args)
  }

}
