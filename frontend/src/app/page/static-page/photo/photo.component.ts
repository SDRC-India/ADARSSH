import { Component, OnInit, ViewChild } from '@angular/core';
import { NgxImageGalleryComponent, GALLERY_IMAGE, GALLERY_CONF } from 'ngx-image-gallery';
import { Constants } from 'src/app/constants';
import { StaticServiceService } from '../services/static-service.service';

@Component({
  selector: 'rmncha-photo',
  templateUrl: './photo.component.html',
  styleUrls: ['./photo.component.scss']
})
export class PhotoComponent implements OnInit {

  searchTexts: any;
  galleryData:any[]=[];
  p: number = 1;
  gallerySection:any={};
  @ViewChild('ngxImageGallery') ngxImageGallery: NgxImageGalleryComponent;
  images: GALLERY_IMAGE[] = [];
  isImageGalleryOpen: Boolean = false;
  apigateway= Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL;

  conf: GALLERY_CONF = {
    imageOffset: '0px',
    showDeleteControl: false,
    showImageTitle: true,
    closeOnEsc:true,
  };
  
  constructor(private staticService: StaticServiceService) { }

  ngOnInit() {
    let data = {
      mainMenu: "Resources",
      subMenu: "Photo"
    }

    this.staticService.getCMSRequestData(data).subscribe(data => {
      this.galleryData = data['Photo'];

      this.galleryData.forEach(element => {
        this.images.push({ url: this.apigateway + 'anynomus/getFile?fileName=' + element.q2[0].filePath,title:element.q1})
      });
    })
 


  }
  

  // METHODS
  // open gallery
  openGallery(index) {
    // this.isImageGalleryOpen = true;
    this.ngxImageGallery.open(index);
  }

  // close gallery
  closeGallery() {
    this.ngxImageGallery.close();
  }

  // set new active(visible) image in gallery
  newImage(index: number = 0) {
    this.ngxImageGallery.setActiveImage(index);
  }

  // next image in gallery
  nextImage() {
    this.ngxImageGallery.next();
  }

  // prev image in gallery
  prevImage() {
    this.ngxImageGallery.prev();
  }

  /**************************************************/

  // EVENTS
  // callback on gallery opened
  galleryOpened(index) {
  }

  // callback on gallery closed
  galleryClosed() {
    this.isImageGalleryOpen = false;
  }

  // callback on gallery image clicked
  galleryImageClicked(index) {
  }

  // callback on gallery image changed
  galleryImageChanged(index) {
  }

  openModal(index: number) {

   let actualIndex= this.galleryData.findIndex(d=> d.index == index);
    this.isImageGalleryOpen = true;
    // this.ngxImageGallery.loading=false;
    this.ngxImageGallery.opened=true;
    this.ngxImageGallery.activeImageIndex = actualIndex;
    this.ngxImageGallery.setActiveImage(actualIndex);
    document.getElementById("ngxalbum").style.display = "block";
    
    
  }

}
