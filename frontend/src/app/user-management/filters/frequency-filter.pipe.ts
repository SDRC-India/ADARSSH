import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'frequencyFilter'
})
export class FrequencyFilterPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    return null;
  }

}
