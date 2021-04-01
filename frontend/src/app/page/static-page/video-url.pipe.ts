import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Pipe({
  name: 'videoUrl'
})
export class VideoUrlPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) { }
  
  transform(value: any, args?: any): any {
    if (value.toLowerCase().includes('youtube.com'))
    value = value.replace('youtube.com/watch?v=','youtube.com/embed/');

    if (value.toLowerCase().includes('youtu.be'))
    value = value.replace('youtu.be', 'youtube.com/embed/');

    if (value.toLowerCase().includes('vimeo.com'))
    value = value.replace('vimeo.com','player.vimeo.com/video')

    if (value.toLowerCase().includes('dailymotion.com'))
      value = value.replace('dailymotion.com', 'dailymotion.com/embed/')
    

    if (value.toLowerCase().includes('dai.ly'))
      value = value.replace('dai.ly', 'dailymotion.com/embed/video/')
   

    if (value.toLowerCase().includes('facebook'))
    {
      value = 'https://www.facebook.com/plugins/video.php?href=' + value +'&width=500'
    }

    if (value.toLowerCase().includes('twitter.com'))
    {
      value = 'https://twitframe.com/show?url='+value
    }
    //linkedin.com/embed
    if (value.toLowerCase().includes('linkedin.com'))
      value = value.replace('linkedin.com', 'linkedin.com/embed')

    return this.sanitizer.bypassSecurityTrustResourceUrl(value);
  }

}
