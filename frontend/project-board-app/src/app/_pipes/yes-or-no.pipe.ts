import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'yesOrNo'
})
export class YesOrNoPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    if (value) {
      return 'Ja';
    }
    return 'Nein';
  }
}
