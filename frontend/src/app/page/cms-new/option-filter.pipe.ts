import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'optionFilter'
})
export class OptionFilterPipe implements PipeTransform {

  transform(options: any[], parentColumn: any, field: any): any {
    if(field.optionsParentColumn){
      return options.filter(option => {
        if(parentColumn.value)
          return parentColumn.value.indexOf(option.parentKey) != -1
        else 
          return false;
      });
    }
    else{
      return options;
    }
  }

}
