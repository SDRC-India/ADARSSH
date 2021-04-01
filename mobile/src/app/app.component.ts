import {
  Component,
  ViewChild,
  HostListener
} from '@angular/core';
import {
  Platform,
  AlertController,
  Nav,Events, MenuController, IonicApp, App
} from 'ionic-angular';
import {
  StatusBar
} from '@ionic-native/status-bar';
import {
  SplashScreen
} from '@ionic-native/splash-screen';
import {
  ApplicationPlatformImpl
} from '../class/ApplicationPlatformImpl';

import {
  MessageServiceProvider
} from '../providers/message-service/message-service';
import {
  ConstantProvider
} from '../providers/constant/constant';
import {
  SyncServiceProvider
} from '../providers/sync-service/sync-service';
import {
  Storage
} from '@ionic/storage'
import { UserServiceProvider } from '../providers/user-service/user-service';
import { LoginServiceProvider } from '../providers/login-service/login-service';
import { Network } from '@ionic-native/network';
import { ApplicationDetailsProvider } from '../providers/application/appdetails.provider';
import { QuestionServiceProvider } from '../providers/question-service/question-service';
import { HttpErrorResponse } from '@angular/common/http';
import { File } from '@ionic-native/file';

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  @ViewChild(Nav) nav: Nav;

  rootPage: any;
  userdata: any;
  private _innerNavCtrl;
  activeComponent:string;
  splitEnabled:boolean=false;

  // @HostListener('window:popstate', ['$event'])
  // onbeforeunload(event) {
  //   if(!(this.platform.is('android') && this.platform.is('cordova'))){
  //     if (window.location.href.substr(window.location.href.length - 5) == 'login') {
  //       history.pushState(null, null, "/#/login");
  //     }
  //   }
  // }

  constructor(public platform: Platform, public statusBar: StatusBar, public splashScreen: SplashScreen,
    private applicationDetailsProvider: ApplicationDetailsProvider, public messageProvider: MessageServiceProvider, public constantProvider: ConstantProvider,
    private alertCtrl: AlertController, public syncSerivice: SyncServiceProvider, public storage: Storage, private userService: UserServiceProvider,
    public loginService: LoginServiceProvider, public events: Events, public network: Network,
    private questionService: QuestionServiceProvider,
    private _app: App, private _ionicApp: IonicApp, private _menu: MenuController, 
    private messageService: MessageServiceProvider,private file:File) {
    this.initializeApp();
  }

  initializeApp() {
    this.platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      this.statusBar.styleDefault();
      this.splashScreen.hide();

      // this.setupBackButtonBehavior ();
      //Setting platforms
      let applicationPlatform: ApplicationPlatform = new ApplicationPlatformImpl()

      if (this.platform.is('mobileweb')) {
        applicationPlatform.isMobilePWA = true
      } else if (this.platform.is('core')) {
        applicationPlatform.isWebPWA = true
      } else if (this.platform.is('android') && this.platform.is('cordova')) {
        this.createProjectFolder()
        applicationPlatform.isAndroid = true
      }

      this.applicationDetailsProvider.setPlatform(applicationPlatform)
      this.rootPage = 'LoginPage'

    });
  }

  ngOnInit(){

    this.events.subscribe('user', data=>{
      this.userdata = data.username;
      this.rootPage = 'LoginPage'
    })
    this.storage.get(ConstantProvider.dbKeyNames.userAndForm).then(data=>{
      if(data){
        this.userdata = data.user.username
      }
    })
    this.nav.viewWillEnter.subscribe(view => {
      this.activeComponent = view.instance.constructor.name;
      if (this.activeComponent == "HomePage") {
      this.splitEnabled = true;
      } else {
      this.splitEnabled = false;
      }
      });
  }
 /**
   * This method will update all master forms and the user specific froms in local storage from server.
   *
   * @author Sourav Nath (souravnath@sdrc.co.in)
   */
  async updateForms() {
    this.messageService.showLoader(ConstantProvider.message.formUpdating);
    this.storage.get(ConstantProvider.dbKeyNames.userAndForm).then(async (item) => {
      if (item) {
        this.getAllFormsForUser(item.tokens.accessToken, item.lastUpdatedDate).then(async forms => {
          if (Object.keys(forms.allQuestions).length > 0) {
            let newObjForm = {};
            for (let key in item.getAllForm) {
              let newKey = key.split("_")[0];
              await this.getNewFormIndex(newKey, forms.allQuestions).then((index) => {
                if (index == -1) {
                  newObjForm[key] = item.getAllForm[key];
                } else {
                  newObjForm[index] = forms.allQuestions[index];
                }
              }).catch((error) => {
                console.log(error)
                //reject(error)
              });
            }
            for (let key in forms.allQuestions) {
              let newKey = key.split("_")[0];
              await this.getNewFormIndex(newKey, item.getAllForm).then((index) => {
                if (index == -1) {
                  newObjForm[key] = item.getAllForm[key];
                }
              }).catch((error) => {
                console.log(error)
                //reject(error)
              });
            }
            item.getAllForm = newObjForm;
            let userAndForm: IUserAndForm = {
              user: item.user,
              getAllForm: newObjForm,
              tokens: item.tokens,
              lastUpdatedDate: forms.lastUpdatedDate
            }
            this.messageService.stopLoader();
            await this.userService.saveNewForms(userAndForm).then(async a => {
              await this.checkFormExists(item, forms).then(val => {
                if (val) {
                  let confirm = this.alertCtrl.create({
                    enableBackdropDismiss: false,
                    title: 'Confirmation',
                    message: "All 'Saved' and 'Finalized' data for the updated forms shall erased from the device on confirmation. <br><br> Are you sure you want to proceed with the update?",
                    buttons: [{
                      text: 'Cancel',
                      handler: () => { }
                    },
                    {
                      text: 'Update',
                      handler: () => {
                        let user = item.user["username"]
                        this.storage.get("form-" + user).then(async (data) => {
                          if (data) {
                            for (let key in data) {
                              let newKey = key.split("_")[0];
                              await this.getNewFormIndex(newKey, forms.allQuestions).then((index) => {
                                if (index !== -1) {
                                  delete data[key];
                                }
                              }).catch((error) => {
                                console.log(error)
                                //reject(error)
                              });
                            }
                            this.userService.updateSaveAndFinalizeForm("form-" + user, data)
                            this.messageProvider.showSuccessToast(ConstantProvider.message.formUpdationSuccess)
                          }
                        })
                      }
                    }
                    ]
                  });
                  confirm.present();

                } else {
                  this.messageProvider.showSuccessToast(ConstantProvider.message.formUpdationSuccess)
                }

              }).catch((error) => {
                //reject(error)
              })
            }).catch((error) => {
              //reject(error)
            })
          } else {
            this.messageService.stopLoader();
            this.messageProvider.showSuccessToast(ConstantProvider.message.formUpdationNotFound)
          }
        }).catch((error) => {
          //reject(error)
        })
      }
    })
  }
  checkFormExists(item, newforms): Promise<any> {
    return new Promise<any>(async (resolve, reject) => {
      let user = item.user["username"];
      await this.storage.get("form-" + user).then(async (data) => {
        if (data) {
          for (let key in data) {
            let newKey = key.split("_")[0];
            await this.checkFormStatus(data[key]).then(async (status) => {
              if (status) {
                await this.getNewFormIndex(newKey, newforms.allQuestions).then((index) => {
                  if (index !== -1) {
                    resolve(true)
                  }
                }).catch((error) => {
                  resolve(true)
                });
              }
          }).catch((error) => {
            resolve(true)
          });
          }
        }
      })
      resolve(false)
    });
  }
  checkFormStatus(checkList): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      for (let key in checkList) {
        if (checkList[key].formStatus == "save" || checkList[key].formStatus == "finalized") {
          resolve(true)
          break
        }
      }
      resolve(false)
    });
  }
  getNewFormIndex(newKey, checkList): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      for (let key in checkList) {
        let oldKey = key.split("_")[0];
        if (oldKey == newKey) {
          resolve(key)
          break
        }
      }
      resolve(-1)
    });
  }
  async getAllFormsForUser(accessToken, lastUpdatedDate): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.questionService.getUpdateQuestionBank(null, accessToken, lastUpdatedDate).then(data => {
        resolve(data)
      })
        .catch((error) => {
          reject(error)
        })
    });
  }
  async sync() {
    this.messageProvider.showLoader(ConstantProvider.message.syncingPleaseWait);
    try {
      let scount = await this.syncSerivice.synchronizeDataWithServer().then((sCount)=>{
        return sCount;
      })
      let rcount =  await this.syncSerivice.getRejectedForms().then((rCount)=>{
        return rCount
      })

      if((scount > 0 && rcount > 0) || (scount > 0 || rcount > 0)){
       this.messageService.stopLoader();
       this.messageService.showSuccessAlert("Info", "(" +scount +") Forms succesfully sent.<br>" + "(" +rcount + ") Forms rejected." )
       this.events.publish('syncStatus', true)
      }else{
       this.messageService.stopLoader();
       this.messageService.showSuccessAlert("Info", "No data to sync" )
      }
    } catch (error) {
      HttpErrorResponse
      // this.messageService.showErrorToast(JSON.stringify(error.error))
      if (error.status == 101) {
        this.messageService.stopLoader();
        this.messageService.showErrorToast("Sync Failed. Please use high speed internet connectivity during sync.")
        throw (error);
      }
      else if (error.status == 412) {
        this.messageService.stopLoader();
        this.messageService.showErrorToast(error.error)
        throw (error);
      } else if (error.status == 417) {
        this.messageService.stopLoader();
        this.messageService.showErrorToast(error.error)
        throw (error);
      } else if (error.status == 401) {
        this.messageService.stopLoader();
        this.messageService.showErrorToast(error.error)
        throw (error);
      }else if (!navigator.onLine) {
        this.messageService.stopLoader();
        this.messageService.showErrorToast(ConstantProvider.message.checkInternetConnection)
      } else {
        this.messageService.stopLoader();
        this.messageService.showErrorToast(error.error)
      }
      this.events.publish('syncStatus', true)
    }
  }

  logout() {
    let confirm = this.alertCtrl.create({
      enableBackdropDismiss: false,
      title: '&emsp;&emsp;<strong>&#9888;</strong> Warning',
      message: "<strong>Are you sure you want to logout</strong>",
      cssClass: 'custom-font',
      buttons: [{
          text: 'No',
          cssClass: 'cancel-button',
          handler: () => {}
        },
        {
          text: 'Yes',
          cssClass: 'exit-button',
          handler: () => {
            let isLogoutClicked = `true`;
            localStorage.setItem('isLogoutClicked', isLogoutClicked);
            this.messageProvider.showSuccessToast("Logout Successfully.")
            // if(!(this.platform.is('android') && this.platform.is('cordova'))){
            //   history.pushState(null,null,"/#/login");
            // }
            this.nav.setRoot('LoginPage')
          }
        }
      ]
    });
    confirm.present();
  }
  private setupBackButtonBehavior () {

    // If on web version (browser)
    if (window.location.protocol !== "file:") {

      // Listen to browser pages
      this.events.subscribe("navController:current", (navCtrlData) => {
        this._innerNavCtrl = navCtrlData[0];
      });

      // Register browser back button action(s)
      window.onpopstate = (evt) => {

        // Close menu if open
        if (this._menu.isOpen()) {
          this._menu.close ();
          return;
        }

        // Close any active modals or overlays
        let activePortal = this._ionicApp._loadingPortal.getActive() ||
          this._ionicApp._modalPortal.getActive() ||
          this._ionicApp._toastPortal.getActive() ||
          this._ionicApp._overlayPortal.getActive();

        if (activePortal) {
          activePortal.dismiss();
          return;
        }

        // Navigate back
        if (this._app.getRootNav().canGoBack()) this.nav.setRoot('LoginPage');

      };

      // Fake browser history on each view enter
      this._app.viewDidEnter.subscribe((app) => {
        history.pushState (null, null, "");
      });

    }

  }

  createProjectFolder() {
    //checking folder existance
    this.file.checkDir(this.file.externalRootDirectory, ConstantProvider.appFolderName)
      .catch(err => {
        if (err.code === 1) {
          // folder not present, creating new folder
          this.file.createDir(this.file.externalRootDirectory, ConstantProvider.appFolderName, false)
            .then(data => {
            })
            .catch(err => {
              this.messageService.showErrorToast(err.message)
            })
        }
      })
  }
}
