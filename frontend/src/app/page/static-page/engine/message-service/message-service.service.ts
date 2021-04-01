import { Injectable } from '@angular/core';
import { ToastsManager } from 'ng6-toastr';

@Injectable({
  providedIn: 'root'
})
export class MessageServiceService {

  constructor() {
  }



  showSuccessToast(message: string,toastr:ToastsManager) {
//     let toastNotificationConfiguration: ToastNotificationConfiguration = {
//       message: message,
//       displayDuration: 1000,
//       autoHide: true,
//       showCloseButton: true,
//       toastType: ToastType.SUCCESS
// };

    toastr.success(message);
  }

  showErrorToast(message: string,toastr:ToastsManager) {
//     let toastNotificationConfiguration: ToastNotificationConfiguration = {
//       message: message,
//       displayDuration: 1000,
//       autoHide: true,
//       showCloseButton: true,
//       toastType: ToastType.ERROR
// };
toastr.error(message);
  }





//   showSuccessToast(message: string,toastr:ToasterService) {
//     let toastNotificationConfiguration: ToastNotificationConfiguration = {
//       message: message,
//       displayDuration: 1000,
//       autoHide: true,
//       showCloseButton: true,
//       toastType: ToastType.SUCCESS
// };

//     toastr.showToastMessage(toastNotificationConfiguration);
//   }

//   showErrorToast(message: string,toastr:ToasterService) {
//     let toastNotificationConfiguration: ToastNotificationConfiguration = {
//       message: message,
//       displayDuration: 1000,
//       autoHide: true,
//       showCloseButton: true,
//       toastType: ToastType.ERROR
// };
// toastr.showToastMessage(toastNotificationConfiguration);
//   }
}
