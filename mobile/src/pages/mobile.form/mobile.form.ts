import {
  Component,
  ViewChild,
  HostListener
} from '@angular/core';
import {
  IonicPage,
  NavController,
  NavParams,
  ViewController,
  Content,
  AlertController,
  Platform,
  IonicApp,
  Navbar,
  ActionSheetController
} from 'ionic-angular';

import {
  QuestionServiceProvider
} from '../../providers/question-service/question-service';
import {
  MessageServiceProvider
} from '../../providers/message-service/message-service';
import {
  FormServiceProvider
} from '../../providers/form-service/form-service';
import {
  DataSharingServiceProvider
} from '../../providers/data-sharing-service/data-sharing-service';
import {
  ConstantProvider
} from '../../providers/constant/constant';
import {
  DatePipe
} from '@angular/common';
import {
  DatePicker
} from '@ionic-native/date-picker';
import {
  EngineUtilsProvider
} from '../../providers/engine-utils/engine-utils';
import {
  AmazingTimePickerService
} from 'amazing-time-picker';
import {
  UUID
} from 'angular2-uuid';
import {
  WebFormComponent
} from '../../components/web/web.form';
import {
  IMyDpOptions,
  IMyDateModel
} from 'mydatepicker';
import {
  Geolocation
} from '@ionic-native/geolocation';
import {
  Camera,
  CameraOptions
} from '@ionic-native/camera';
import {
  ApplicationDetailsProvider
} from '../../providers/application/appdetails.provider';
import {
  CommonsEngineProvider
} from '../../providers/commons-engine/commons-engine';
import {
  UserServiceProvider
} from '../../providers/user-service/user-service';
import {
  Storage
} from '@ionic/storage';
import {
  ConstraintTokenizer
} from '../../providers/engine-utils/constraintsTokenizer';
import {
  ImagePicker
} from '@ionic-native/image-picker';
import {
  File, IWriteOptions
} from '@ionic-native/file';

@IonicPage()
@Component({
  selector: 'mobile-form',
  templateUrl: 'mobile.form.html'
})
export class MobileFormComponent {

  isWeb: boolean = false;
  section: String;
  dataSharingService: DataSharingServiceProvider;
  repeatSubSection: Map < Number, IQuestionModel > = new Map()
  tempFormSubSections2;
  sectionNames = []
  sectionHeading: any;
  selectedSection: Array < Map < String, Array < IQuestionModel >>> ;
  subSections: Array < Map < String, Array < IQuestionModel >>>
    sectionMap: Map < String, Array < Map < String, Array < IQuestionModel >>> >= new Map();
  data: Map < String, Array < Map < String, Array < IQuestionModel >>> > = new Map();
  dbFormModel: IDbFormModel;
  sectionHeader: any;
  maxDate: any;
  optionMap: {} = []
  questionMap: {} = {}
  formId: string
  formTitle: String
  formTitleActive: boolean = false;
  errorStatus: boolean = false;
  mandatoryQuestion: {} = {};
  disableStatus: boolean = false;
  disablePrimaryStatus: boolean = false;
  saveType: String
  uniqueId: String = "";
  fullDate: any;
  createdDate: String = "";
  updatedDate: String = "";
  updatedTime: String = "";
  checkFieldContainsAnyvalueStatus: boolean = false;
  segment: boolean = false
  countBeginRepeat = 0;
  beginrepeatArraySize: number = 0;
  isNewFormEntry: boolean;
  beginRepeatKey: Number;
  public unregisterBackButtonAction: any;
  base64Image: any;
  options = {
    enableHighAccuracy: true,
    timeout: 10000
  };


  questionDependencyArray: {} = {}
  questionFeaturesArray: {} = {}
  constraintsArray: {} = {}
  beginRepeatArray: {} = {}
  scoreKeyMapper: {} = [];

  //for score keeper
  questionInSectionMap: {} = [];
  questionInSubSectionMap: {} = [];

  sectionScoreKeyMapper: {} = [];
  subSectionScoreKeyMapper: {} = [];

  sectionMapScoreKeeper: {} = [];
  subSectionMapScoreKeeper: {} = [];

  checklist_score_keeper_colName: any
  indexMap: {} = {};
  public photo: any;
  /**
   * If it is new form entry, the vaue will be true otherwise the value will be false
   * @author Ratikanta
   * @type {boolean}
   * @memberof FormListPage
   */

  @ViewChild(Navbar) navBar: Navbar;
  @ViewChild(Content) content: Content;
  @ViewChild(WebFormComponent) customComponent: WebFormComponent;
  @HostListener('window:popstate', ['$event'])
  onbeforeunload(event) {
    if (window.location.href.substr(window.location.href.length - 4) == 'form') {

    }
    if (window.location.href.substr(window.location.href.length - 5) == 'login') {
      history.pushState(null, null, "" + window.location.href);
    }
  }
  constructor(private constraintTokenizer: ConstraintTokenizer, private commonsEngineProvider: CommonsEngineProvider, private applicationDetailsProvider: ApplicationDetailsProvider, public questionService: QuestionServiceProvider,
    public messageService: MessageServiceProvider, private navCtrl: NavController, public datepipe: DatePipe, public datePicker: DatePicker,
    public viewCtrl: ViewController, public formService: FormServiceProvider, public navParams: NavParams, private dataSharingProvider: DataSharingServiceProvider,
    private atp: AmazingTimePickerService, private alertCtrl: AlertController, private platform: Platform,
    private app: IonicApp, private engineUtilsProvider: EngineUtilsProvider, private imagePicker: ImagePicker, public actionSheetCtrl: ActionSheetController,
    private geolocation: Geolocation, private camera: Camera, private storage: Storage, private userService: UserServiceProvider, private file: File) {
    this.dataSharingService = dataSharingProvider;
  }

  /**
   * This method is used to fetch the geo location.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  getGeoLocation(question) {
    this.messageService.showLoader(ConstantProvider.message.pleaseWait);
    this.geolocation.getCurrentPosition(this.options).then(resp => {
      this.messageService.stopLoader();
      this.questionMap[question.columnName].value = "Lat :" + resp.coords.latitude + " Long :" + resp.coords.longitude;
    }).catch(error => {
      this.messageService.stopLoader();
      this.messageService.showErrorToast("Unable to fetch location, please try again.");
      console.log("Error getting location", error);
    });
  }

  /**
   * This method used to open the gallery for camera.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  openCamera(question) {
    document.getElementById(question.columnName + "file-input").click();
  }

  /**
   * This method is used for selection of image from the gallery
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param $event
   * @param question
   */
  openCameraGallery(question) {
    // if (question.value != null) {
    let actionSheet = this.actionSheetCtrl.create({
      title: 'Select Image Source',
      buttons: [{
          text: 'Load from Library',
          handler: () => {
            this.openGallery(question);
          }
        },
        {
          text: 'Use Camera',
          handler: () => {
            this.takePhoto(question);
          }
        },
        {
          text: 'Cancel',
          role: 'cancel'
        }
      ]
    });
    actionSheet.present();
    // }
  }

  /**
   * This method is used for selection of image from the gallery
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  openGallery(question) {
    let numOfImage = 1
    let options = {
      maximumImagesCount: numOfImage,
      quality: 50, // picture quality
      destinationType: this.camera.DestinationType.FILE_URI,
      encodingType: this.camera.EncodingType.JPEG,
      mediaType: this.camera.MediaType.PICTURE
    };

    const iWriteOptions: IWriteOptions = {
      replace: true
    }

    this.imagePicker.hasReadPermission().then(
      (result) => {
        if (result == false) {
          // no callbacks required as this opens a popup which returns async
          this.imagePicker.requestReadPermission();
        } else if (result == true) {
          this.imagePicker.getPictures(options).then(async imageURL => {

            let fileName = imageURL[0].substr(imageURL[0].lastIndexOf('/') + 1)
            let base64String = await this.file.readAsDataURL(imageURL[0].substr(0, imageURL[0].lastIndexOf('/') + 1), fileName)
            let imageBlob = await this.commonsEngineProvider.dataURItoBlob(base64String, "image/jpeg")
            this.commonsEngineProvider.createFoldersInMobileDevice(this.formId, this.uniqueId, this.file, this.messageService).then((async d => {
              let writeToFilePath = this.file.externalRootDirectory + ConstantProvider.appFolderName + "/" + this.formId + "/" + this.uniqueId;
              let writtenFile = await this.file.writeFile(writeToFilePath, fileName, imageBlob, iWriteOptions)
              this.questionMap[question.columnName].value = []
              this.questionMap[question.columnName].value.push(writtenFile.nativeURL)

              this.messageService.stopLoader();
            })).catch(err => {
              this.messageService.stopLoader()
              this.messageService.showErrorToast(err)
            })
          }, (err) => {
            this.messageService.stopLoader();
            console.log(err);
          });
        }
      }).catch((err) => {
        this.messageService.stopLoader();
        console.log(err);
      });
  }

  /**
   * This method is used for take photo using phone camera
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  takePhoto(question) {
    this.messageService.showLoader(ConstantProvider.message.pleaseWait);
    const options: CameraOptions = {
      quality: 30, // picture quality
      destinationType: this.camera.DestinationType.DATA_URL,
      encodingType: this.camera.EncodingType.JPEG,
      sourceType: this.camera.PictureSourceType.CAMERA,
      mediaType: this.camera.MediaType.PICTURE,
      saveToPhotoAlbum: false
    }

    const iWriteOptions: IWriteOptions = {
      replace: true
    }
    this.camera.getPicture(options).then(async base64 => {
      let image = "data:image/jpeg;base64," + base64
      let imageBlob = await this.commonsEngineProvider.dataURItoBlob(image, this.commonsEngineProvider.getContentType(image))

      this.commonsEngineProvider.createFoldersInMobileDevice(this.formId, this.uniqueId, this.file, this.messageService).then((async d => {
        let currentTime = +new Date();
        let random = Math.random()
        let trends = random + ""
        let filename = trends.replace('.', '') + '_' + question.columnName + '_' + currentTime + ".jpg";

        let writeToFilePath = this.file.externalRootDirectory + ConstantProvider.appFolderName + "/" + this.formId + "/" + this.uniqueId;
        let writtenFile = await this.file.writeFile(writeToFilePath, filename, imageBlob, iWriteOptions)

        this.questionMap[question.columnName].value = []
        this.questionMap[question.columnName].value.push(writtenFile.nativeURL)
        this.messageService.stopLoader()
      })).catch(err => {
        this.messageService.stopLoader()
        this.messageService.showErrorToast(err)
      })

    }, (err) => {
      console.log(err);
      this.messageService.stopLoader()
      this.messageService.showErrorToast("Please clear your cache.<br>" + err)
    });
  }

  /**
   * This method is used to delete the photo
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   * @param index
   */
  deletePhoto(question, index) {
    if (!this.disableStatus) {
      let confirm = this.alertCtrl.create({
        title: 'Warning',
        message: '<strong>Are sure you want to delete this photo?</strong>',
        buttons: [{
          text: 'No',
          handler: () => {
            console.log('Disagree clicked');
          }
        }, {
          text: 'Yes',
          handler: () => {
            console.log('Agree clicked');
            this.questionMap[question.columnName].value.splice(index, 1);
          }
        }]
      });
      confirm.present();
    }
  }

  // openCamera(question) {
  //   // this.messageService.showLoader("Processing Image...");
  //   if (this.applicationDetailsProvider.getPlatform().isAndroid) {
  //     const options: CameraOptions = {
  //       quality: 40,
  //       destinationType: this.camera.DestinationType.DATA_URL,
  //       encodingType: this.camera.EncodingType.JPEG,
  //       mediaType: this.camera.MediaType.PICTURE
  //     }
  //     this.camera.getPicture(options).then((imageData) => {
  //       // console.log(imageData)
  //       this.geolocation.getCurrentPosition(this.options).then(resp => {
  //         this.questionMap[question.columnName].value = {
  //           src: "data:image/jpeg;base64," + imageData,
  //           meta_info: "Latitude :" + resp.coords.latitude + "; Longitude :" + resp.coords.longitude + "; Accuracy :" + resp.coords.accuracy
  //         }
  //       }).catch(error => {
  //         this.questionMap[question.columnName].value = {
  //           src: "data:image/jpeg;base64," + imageData
  //         }
  //       });
  //     }, (err) => {
  //       // Handle error
  //     });
  //   } else {
  //     document.getElementById(question.columnName + "file-input").click();
  //   }
  // }

  /**
   * This method is used for selection of image from the gallery
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param $event
   * @param question
   */
  onCameraFileChange($event, question) {
    let files = $event.target.files;
    let file = files[0];

    if (
      (file.name.split(".")[(file.name.split(".") as string[]).length - 1] as String)
      .toLocaleLowerCase() === "png" || (file.name.split(".")[(file.name.split(".") as string[]).length - 1] as String)
      .toLocaleLowerCase() === "jpg" || (file.name.split(".")[(file.name.split(".") as string[]).length - 1] as String)
      .toLocaleLowerCase() === "jpeg"
    ) {
      let reader = new FileReader()
      reader.onload = this._handleReaderLoaded.bind(this);
      this.base64Image = question;
      reader.readAsBinaryString(file);
    } else {
      this.messageService.showErrorToast("Please select a image")
    }
  }


  /**
   * This method is used to convert the image file into base64 and set it ti the src variable ,also attach the lat long accurarcy in the meta_info
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param readerEvt
   */
  _handleReaderLoaded(readerEvt) {
    let binaryString = readerEvt.target.result;
    this.geolocation
      .getCurrentPosition(this.options)
      .then(resp => {
        this.questionMap[this.base64Image.columnName].value = {
          src: "data:image/jpeg;base64," + btoa(binaryString),
          meta_info: "Latitude :" + resp.coords.latitude + "; Longitude :" + resp.coords.longitude + "; Accuracy :" + resp.coords.accuracy
        }
      })
      .catch(error => {
        this.questionMap[this.base64Image.columnName].value = {
          src: "data:image/jpeg;base64," + btoa(binaryString)
        }
      });

  }

  /**
   * This method call up the initial load. Get all the form data from database
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  async ngOnInit() {
    this.isWeb = this.applicationDetailsProvider.getPlatform().isWebPWA
    this.formId = this.navParams.get('formId')
    this.formTitle = this.navParams.get('formTitle')
    this.isNewFormEntry = this.navParams.get('isNew') ? true : false
    this.segment = this.navParams.get('segment') == 'save' ? true : false
    this.maxDate = this.datepipe.transform(new Date(), 'yyyy-MM-dd')
    this.fullDate = this.datepipe.transform(new Date(), "yyyy-MM-dd").split("-");
    if (!this.isWeb) {
      this.messageService.showLoader(ConstantProvider.message.pleaseWait)
      if (this.navParams) {
        if (!(this.navParams.get("submission") == undefined)) {
          this.saveType = "old";


          let tempData = await this.storage.get(ConstantProvider.dbKeyNames.form + "-" + this.userService.user.username);
          let tempSubmissions = tempData[this.formId as any]
          let tempSubmission = tempSubmissions[(this.navParams.get("submission") as IDbFormModel).uniqueId as any] as any
          this.data = tempSubmission.formData

          let tempval = await this.storage.get(ConstantProvider.dbKeyNames.form + "-" + this.userService.user.username);
          let forSub = tempval[this.formId as any]
          let submission = forSub[(this.navParams.data.submission.uniqueId)] as any
          this.data = submission.formData

          this.disableStatus = submission.formStatus == "save" || submission.formStatus == "rejected" ? false : true;
          this.uniqueId = submission.uniqueId;
          this.createdDate = submission.createdDate;
          this.updatedDate = submission.updatedDate;
          this.updatedTime = submission.updatedTime;

          this.disablePrimaryStatus = true;
          await this.questionService.getQuestionBank(this.formId, null, ConstantProvider.lastUpdatedDate).then(async schema => {

            if (schema) {
              this.data = await this.commonsEngineProvider.loadDataIntoSchemaDef(schema, this.data)
              await this.loadQuestionBankIntoUI(this.data, "s");
            } else {
              this.navCtrl.setRoot("LoginPage");
            }
          });
        } else {
          this.saveType = "new";
          this.uniqueId = UUID.UUID();
          if (this.formId) {
            await this.questionService.getQuestionBank(this.formId, null, ConstantProvider.lastUpdatedDate).then(async data => {
              if (data) {
                let formData = data;
                await this.loadQuestionBankIntoUI(formData,"n");
              } else {
                this.navCtrl.setRoot("LoginPage");
              }
            });
          } else {
            this.navCtrl.setRoot("LoginPage");
          }
        }
      } else {
        this.navCtrl.setRoot("LoginPage");
      }
    }
  }

  /**
   * This function is use to set the options in the date picker like dateFormat,disableSince,editableDateField,showTodayBtn,showClearDateBtn
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  public myDatePickerOptions: IMyDpOptions = {
    // other options...
    dateFormat: 'dd-mm-yyyy',
    disableSince: {
      year: Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[0]),
      month: Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[1]),
      day: Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')[2]) + 1
    },
    editableDateField: false,
    showTodayBtn: false,
    showClearDateBtn: false
  };

  /**
   * This method is use to load ans set all the data from json to ui
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param data
   */
  async loadQuestionBankIntoUI(data,status) {
    this.data = data;

    if(status == "s"){
      this.optionMap =  await this.commonsEngineProvider.loadOptionsIntoData(this.formId);
    }
    for (let index = 0; index < Object.keys(data).length; index++) {
      this.sectionMap.set(Object.keys(data)[index], data[Object.keys(data)[index]]);
      for (let j = 0; j < data[Object.keys(data)[index]].length; j++) {
        let subSections = data[Object.keys(data)[index]][0];
        let counter = 1;
        //for score keeper
        let sectionName = Object.keys(data)[index];

        for (let qs = 0; qs < Object.keys(subSections).length; qs++) {

          //for score keeper
          let subSectionName = Object.keys(subSections)[qs];

          for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
            let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q];
            question.questionOrderDisplay = false;

            if (question.attachedFiles == null) question.attachedFiles = [];
            switch (question.controlType) {
              case "sub-score-keeper":
              case "score-keeper":
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                this.questionMap[question.columnName] = question;
                question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;
                break;
              case "score-holder":
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                this.questionMap[question.columnName] = question;
                question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;

                //for score keeper
                if (this.questionInSectionMap[sectionName] == undefined) {
                  this.questionInSectionMap[sectionName] = []
                  this.questionInSectionMap[sectionName].push(question)
                } else {
                  let a = this.questionInSectionMap[sectionName]
                  a.push(question)
                  this.questionInSectionMap[sectionName] = a
                }
                if (this.questionInSubSectionMap[sectionName + "_" + subSectionName] == undefined) {
                  this.questionInSubSectionMap[sectionName + "_" + subSectionName] = []
                  this.questionInSubSectionMap[sectionName + "_" + subSectionName].push(question)
                } else {
                  let a = this.questionInSubSectionMap[sectionName + "_" + subSectionName]
                  a.push(question)
                  this.questionInSubSectionMap[sectionName + "_" + subSectionName] = a
                }
                break;
              case "checklist-score-keeper":
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                this.questionMap[question.columnName] = question;
                break;
              case "table":
              case "tableWithRowWiseArithmetic":
                question.displayComponent = true;
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                for (let row = 0; row < question.tableModel.length; row++) {
                  for (let column = 0; column < Object.keys(question.tableModel[row]).length; column++) {
                    let value = question.tableModel[row][Object.keys(question.tableModel[row])[column]];
                    if (typeof value == "object") {
                      let cell = value;
                      if (cell.value != null) {
                        cell.value = String(cell.value)
                      }
                      cell.dependecy = cell.relevance != null ? true : false;
                      cell.displayComponent = cell.relevance == null ? true : false;
                      this.questionMap[cell.columnName] = cell;
                      //   cell = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(cell);
                      cell.relevance != null ? this.drawDependencyGraph(cell.relevance, cell) : null;
                      cell.sectionName = Object.keys(data)[index];
                      cell.subSectionName = Object.keys(subSections)[qs]
                      this.mandatoryQuestion[cell.columnName] = cell.finalizeMandatory;
                      if (this.disableStatus) {
                        cell.showErrMessage = false;
                      }
                    }
                  }
                }
                break;
              case 'beginrepeat':
                question.displayComponent = true
                question.beginRepeatMinusDisable = false
                question.beginrepeatDisableStatus = false
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                this.repeatSubSection.set(question.key, question)
                //   question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question)
                question.beginRepeatMinusDisable = false
                if (question.beginRepeat.length == 1) {
                  question.beginRepeatMinusDisable = true
                }
                this.questionMap[question.columnName] = question;
                for (let index = 0; index < question.beginRepeat.length; index++) {
                  let beginRepeatQuestions: IQuestionModel[] = question.beginRepeat[index];
                  for (let beginRepeatQuestion of beginRepeatQuestions) {

                    if(status == "s"){
                      switch (beginRepeatQuestion.controlType) {
                        case "dropdown":
                        case "autoCompleteTextView": 
                        case "autoCompleteMulti":
                           let optionTemp =  this.optionMap[beginRepeatQuestion.columnName.split("-")[3]]
                            beginRepeatQuestion.options = JSON.parse(JSON.stringify(optionTemp));
                      
                          break
                      } 
                    }

                    beginRepeatQuestion.sectionName = Object.keys(data)[index];
                    beginRepeatQuestion.subSectionName = Object.keys(subSections)[qs]
                    beginRepeatQuestion.dependecy = beginRepeatQuestion.relevance != null ? true : false;
                    beginRepeatQuestion.displayComponent = beginRepeatQuestion.relevance == null ? true : false;
                    this.questionMap[beginRepeatQuestion.columnName] = beginRepeatQuestion;
                    //   beginRepeatQuestion = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(beginRepeatQuestion);
                    beginRepeatQuestion.relevance != null ? this.drawDependencyGraph(beginRepeatQuestion.relevance, beginRepeatQuestion) : null;
                    this.mandatoryQuestion[beginRepeatQuestion.columnName] = beginRepeatQuestion.finalizeMandatory;
                    if (this.disableStatus) {
                      beginRepeatQuestion.showErrMessage = false;
                    }
                  }
                }
                break;
              case 'camera':
                question.questionOrderDisplay = true;
                this.indexMap[question.questionOrder] = counter;
                counter++;
                // if (question.value != null && question.value.length == 1 && question.attachmentsInBase64) {
                //   question.value.src = question.attachmentsInBase64[0]
                // }
                // if (question.value != null && question.value.length == 1 && question.attachmentsInBase64) {
                //   question.value = question.attachmentsInBase64[0]
                // }
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                //   question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question)
                this.questionMap[question.columnName] = question;
                question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;

                this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                if (this.disableStatus) {
                  question.showErrMessage = false;
                }
                break;
              case "dropdown":
              case "textbox":
              case "Time Widget":
              case "cell":
              case "textarea":
              case "uuid":
              case "file":
              case "mfile":
              case 'geolocation':
              case 'camera':
              case 'segment':
                if (!question.label.includes('Score')) {
                  question.questionOrderDisplay = true;
                  this.indexMap[question.questionOrder] = counter;
                  counter++;
                }
                if (question.controlType == 'textbox' && question.type == 'tel' && question.value != null) {
                  question.value = String(question.value)
                }

                
                if(status == "s" && question.controlType == 'dropdown'){
                  question.options = this.optionMap[question.columnName]
                }
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                //   question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question)
                this.questionMap[question.columnName] = question;
                question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;

                this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                if (this.disableStatus) {
                  question.showErrMessage = false;
                }


                break;
              case "Date Widget":
              case "Month Widget":
                question.questionOrderDisplay = true;
                this.indexMap[question.questionOrder] = counter;
                counter++;
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                //   question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question);
                this.questionMap[question.columnName] = question;
                this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;
                if (this.disableStatus) {
                  question.showErrMessage = false;
                }
                break;
              case "checkbox":

                  if(status == "s"){
                    question.options = this.optionMap[question.columnName]
                  }

                question.questionOrderDisplay = true;
                this.indexMap[question.questionOrder] = counter;
                counter++;
                question.sectionName = Object.keys(data)[index];
                question.subSectionName = Object.keys(subSections)[qs]
                question.dependecy = question.relevance != null ? true : false;
                question.displayComponent = question.relevance == null ? true : false;
                //   question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question);
                this.questionMap[question.columnName] = question;
                this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;
                if (this.disableStatus) {
                  question.showErrMessage = false;
                }
                break;
            }
            //set the default
          }
        }
      }
    }

    //for score keeper
    for (let index = 0; index < Object.keys(data).length; index++) {
      this.sectionMap.set(Object.keys(data)[index], data[Object.keys(data)[index]]);
      for (let j = 0; j < data[Object.keys(data)[index]].length; j++) {
        let subSections = data[Object.keys(data)[index]][0];

        //for score keeper
        let sectionName = Object.keys(data)[index];
        for (let qs = 0; qs < Object.keys(subSections).length; qs++) {

          //for score keeper
          let subSectionName = Object.keys(subSections)[qs];
          for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
            let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q];
            if (question.attachedFiles == null) question.attachedFiles = [];
            switch (question.controlType) {
              case "sub-score-keeper":
                this.subSectionScoreKeyMapper[question.columnName] = this.questionInSubSectionMap[sectionName + "_" + subSectionName];
                this.subSectionMapScoreKeeper[sectionName + "_" + subSectionName] = question;
                break;
              case "score-keeper":
                this.sectionScoreKeyMapper[question.columnName] = this.questionInSectionMap[sectionName];
                this.sectionMapScoreKeeper[sectionName] = question;
                break;
              case "checklist-score-keeper":
                this.checklist_score_keeper_colName = question.columnName
                break;
            }
          }
        }
      }
    }

    // check relevance fro each question
    this.checkRelevanceForEachQuestion()
    for (let questionKey of this.dataSharingService.getKeys(this.beginRepeatArray)) {
      let question = this.questionMap[questionKey];
      let bgQuestion = this.beginRepeatArray[questionKey];
      if (question.value == null || question.value == 0) {
        bgQuestion.beginrepeatDisableStatus = true;
      }
    }

    for (let q of Object.keys(this.questionMap)) {
      let ques = this.questionMap[q]
      this.setupDefaultSettingsAndConstraintsAndFeatureGraph(ques)
    }

    for (let questionKey of this.dataSharingService.getKeys(this.questionMap)) {
      let question = this.questionMap[questionKey]
      if (question.features != null) {
        let feature: string = question.features.split("@AND")
        switch (question.controlType) {
          case 'dropdown':
          case "autoCompleteMulti":
          case "autoCompleteTextView":
            for (let i = 0; i < feature.length; i++) {
              if (feature[i].includes('area_group')) {
                let groupQuestions = this.commonsEngineProvider.getDependentAreaGroupName(feature[i])
                let childLevelQuestion = this.questionMap[groupQuestions];
                let optionCount = 0;
                childLevelQuestion.optionsOther = []
                for (let option of childLevelQuestion.options) {
                  if (question.type == "checkbox") {
                    if (question.value && question.value.indexOf(option["parentId"]) != -1) {
                      //this is used to make the village field disabled initially by making option["visible"] = false; and optionCount++
                      if (option["parentId2"] == -2) {
                        option["visible"] = false;
                        optionCount++
                      } else {
                        option["visible"] = true;
                        childLevelQuestion.optionsOther.push(option)
                      }
                    } else {
                      //this is used to used to adding the other to the filtered village by making the option["visible"] = true;
                      if (option["parentId2"] == -2) {
                        option["visible"] = true;
                      } else {
                        option["visible"] = false;
                      }
                      optionCount++
                    }
                  } else {
                    if (option['parentId'] == question.value) {
                      //this is used to make the village field disabled initially by making option["visible"] = false; and optionCount++
                      if (option["parentId2"] == -2) {
                        option["visible"] = false;
                        optionCount++
                      } else {
                        option["visible"] = true;
                        childLevelQuestion.optionsOther.push(option)
                      }
                    } else {
                      //this is used to used to adding the other to the filtered village by making the option["visible"] = true;
                      if (option["parentId2"] == -2) {
                        option["visible"] = true;
                      } else {
                        option["visible"] = false;
                      }
                      optionCount++
                    }
                  }
                }
                if (optionCount == childLevelQuestion.options.length) {
                  childLevelQuestion.constraints = "disabled"
                } else {
                  childLevelQuestion.constraints = ""
                }
              }
            }
            break;
        }
      }


    }

    // console.log(this.questionMap);
    // console.log("features array: ", this.questionFeaturesArray);
    // console.log("dependencies array: ", this.questionDependencyArray);
    // console.log("beginRepeatArray", this.beginRepeatArray);
    // console.log("repeat Subsection", this.repeatSubSection);
    // console.log('constraints array', this.constraintsArray)
    // console.log('scoreKeyMapper array', this.scoreKeyMapper)
    this.sectionNames = Array.from(this.sectionMap.keys());
    this.section = this.sectionNames[0];
    this.sectionSelected(this.section);
    this.messageService.stopLoader()
  }
  //Biswa
  tempCaryId: any;

  /**
   * This method called when user need to switch to sub-section dynamically
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @author Biswa Ranjan (biswaranjan@sdrc.co.in)
   * @param sectionHeading
   */
  sectionSelected(key ? : any) {
    this.sectionHeader = this.section
    this.content.scrollToTop(300);
    this.selectedSection = this.sectionMap.get(this.section)
  }

  /**
   * This method is for table data calculation.
   *
   * @author Azhar (azaruddin@sdrc.co.in)
   * @param cell
   * @param columnIndex
   * @param rowIndex
   * @param tableModel
   */
  deleteLastWorker(key: Number, bgquestion: IQuestionModel) {
    let beginRepeatParent: IQuestionModel = this.repeatSubSection.get(key);
    let clonedQuestion: IQuestionModel[] = beginRepeatParent.beginRepeat[beginRepeatParent.beginRepeat.length - 1];
    for (let index = 0; index < clonedQuestion.length; index++) {
      clonedQuestion[index].relevance != null ? this.removeFromDependencyGraph(clonedQuestion[index].relevance, clonedQuestion[index]) : null
    }

    if (bgquestion.beginRepeat.length > 1) {
      bgquestion.beginRepeat.pop();
      if (bgquestion.beginRepeat.length == 1) {
        bgquestion.beginRepeatMinusDisable = true;
      } else {
        bgquestion.beginRepeatMinusDisable = false;
      }
    } else {
      for (let i = 0; i < bgquestion.beginRepeat.length; i++) {
        for (let j = 0; j < bgquestion.beginRepeat[i].length; j++) {
          bgquestion.beginRepeat[i][j].value = null;
        }
      }
    }
  }

  
  saveConfirm(type: String){

    //info message

    let confirm = this.alertCtrl.create({
      enableBackdropDismiss: false,
      cssClass: 'custom-font',
      title: 'Info',
      message: type == 'save' ? 'This action will save the record and redirect to the home page. <br><br>Click on <strong> "CANCEL"</strong>  to continue filling the form, click on <strong>"OK"</strong> to save.'
             : 'Once Finalized this record will be ready to sync to server.',
      buttons: [
        {
          text: 'Cancel',
          handler: () => {
          }},
        {
          text: 'Ok',
          handler: () => {
          
            this.onSave(type)
            // .then(async data => {
            //   if(type == 'save'){
            //     this.navCtrl.pop()
            //   }
      
            //  } )
            
          }
        }
      ]
    });
    // if(type == 'save')
    //   this.navCtrl.pop()
    confirm.present();
  }
  /**
   * This method will call, when user clicks on save or finalize button.
   * This methods checks saveMandatory field to save the record and checks all mandatory fields while finalizing the form.
   * If any field is blank, while checking the mandatory field, switch the control to that particular section and show the blank question name.
   *
   *
   * @author Jagat (jagat@sdrc.co.in)
   * @param type
   */
  async onSave(type: String) {
    // let uniqueName;
    let formId;
    //   let headerData: Map < string, string | number | any[] > = new Map();
    let headerData: {} = {}
    let image: string = ConstantProvider.defaultImage;
    this.messageService.showLoader(ConstantProvider.message.pleaseWait);
    let visitedDate;
    for (let index = 0; index < Object.keys(this.data).length; index++) {
      this.sectionMap.set(Object.keys(this.data)[index], this.data[Object.keys(this.data)[index]])
      for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
        let subSections = this.data[Object.keys(this.data)[index]][0]
        for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
          for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
            let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q]
            formId = question.formId
            switch (question.controlType) {
              case "Date Widget":
                if (question.defaultSettings == "prefetchDate:current_date") {
                  visitedDate = this.datepipe.transform(question.value, "dd-MM-yyyy")
                }
                break;
            }
          }
        }
      }
    }
    if (type == 'save') {
      this.errorStatus = false;
      loop1: for (let index = 0; index < Object.keys(this.data).length; index++) {
        this.sectionMap.set(Object.keys(this.data)[index], this.data[Object.keys(this.data)[index]])
        for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
          let subSections = this.data[Object.keys(this.data)[index]][0]
          for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
            for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
              let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q]
              formId = question.formId
              if (question.saveMandatory == true) {
                switch (question.controlType) {

                  case "geolocation":
                    if (question.displayComponent == true && (question.value == null || question.value == "")) {
                      this.errorStatus = true;
                      this.sectionSelected(question.columnName);
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please enter " + question.label);
                      break loop1;
                    }
                    break;
                  case "camera":
                    if (question.displayComponent == true && (question.value == null || question.value == "")) {
                      this.errorStatus = true;
                      this.sectionSelected(question.columnName);
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please enter " + question.label);
                      break loop1;
                    }
                    break;
                  case 'dropdown':
                    for (let i = 0; i < question.options.length; i++) {
                      if (question.displayComponent == true && question.value == null) {
                        this.errorStatus = true
                        // uniqueName = question.options[i]['value']
                        this.sectionSelected(question.columnName)
                        this.errorColor(Object.keys(this.data)[index], question.columnName);
                        this.messageService.showErrorToast("Please select " + question.label)
                        break loop1
                      }
                    }
                    break;
                  case 'textbox':
                    if (question.displayComponent == true && (question.value == null || question.value == '')) {
                      this.errorStatus = true
                      // uniqueName = question.value
                      this.sectionSelected(question.columnName)
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please select " + question.label)
                      break loop1
                    }
                    break;
                  case "Date Widget":
                    if (question.displayComponent == true && (question.value == null || question.value == "")) {
                      this.errorStatus = true;
                      this.sectionSelected(question.columnName);
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please select " + question.label);
                      break loop1;
                    }
                    break;
                  case 'Month Widget':
                    if (question.displayComponent == true && (question.value == null || question.value == "")) {
                      this.errorStatus = true;
                      this.sectionSelected(question.columnName);
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please select " + question.label);
                      break loop1;
                    }
                    break;
                  case "Time Widget":
                    if (question.displayComponent == true && (question.value == null || question.value == "")) {
                      this.errorStatus = true;
                      this.sectionSelected(question.columnName);
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please select " + question.label);
                      break loop1;
                    }
                    break;
                  case 'checkbox':
                    if (question.displayComponent == true && (question.value == null || question.value == '')) {
                      this.errorStatus = true
                      // uniqueName = question.value
                      this.sectionSelected(question.columnName)
                      this.messageService.showErrorToast("Please enter " + question.label)
                      break loop1
                    }
                    break;
                  case "table":
                  case 'tableWithRowWiseArithmetic':
                    let tableData = question.tableModel
                    for (let i = 0; i < tableData.length; i++) {
                      for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                        let cell = (tableData[i])[Object.keys(tableData[i])[j]]
                        if (typeof cell == 'object') {
                          if ((cell.value == null || (cell.value as string).trim() == "")) {
                            this.errorStatus = true
                            this.sectionSelected(Object.keys(this.data)[index])
                            this.messageService.showErrorToast("Please enter " + question.label + " " + (cell.label).replace('@@split@@', ''))
                            break loop1;
                          }
                          // else if (cell.typeDetailIdOfDependencyType == this.questionMap[cell.dependentColumn.split(',')[0] as any].value &&
                          //   (cell.value == null || (cell.value as string).trim() == "")) {
                          //   this.errorStatus = true
                          //   this.sectionSelected(Object.keys(this.data)[index])
                          //   this.messageService.showErrorToast("Please enter " + question.label + " " + (cell.label).replace('@@split@@', ''))
                          //   break loop1;
                          // }
                        }
                      }
                    }
                    break;
                  case 'textarea':
                    if (question.displayComponent == true && (question.value == null || question.value == '')) {
                      this.errorStatus = true
                      this.sectionSelected(question.columnName)
                      this.messageService.showErrorToast("Please enter " + question.label)
                      break loop1
                    }
                    break;
                }

              }
              if (question.reviewHeader) {
                switch (question.controlType) {
                  case 'segment':
                  case 'dropdown':
                    if (question.value == null || question.value == "" || question.value == undefined) {
                      // headerData[question.reviewHeader] = "";
                      break;
                    }
                    if (question.type == "option") {
                      headerData[question.reviewHeader] = question.options && question.options.length ?
                        question.options.filter(d => d.key === question.value)[0].value : question.value;
                    } else {
                      // for checkbox
                      let names = "";
                      let vals = question.options.filter(d => question.value.find(element => {
                        return (d.key === element)
                      }))
                      vals.forEach(e => {
                        names = names + e.value + ","
                      })
                      if (names.includes(",")) {
                        names = names.substring(0, names.lastIndexOf(","))
                      }
                      headerData[question.reviewHeader] = names;
                    }
                    break;
                  case 'textbox':
                    headerData[question.reviewHeader] = question.value;
                    break;
                  case 'camera':
                    headerData[question.reviewHeader] = question.value.src;
                    image = question.value.src;
                    break;
                }
              }
            }
          }
        }
      }
    }
    if (type == 'finalized') {
      // if (!this.checkFinalizedConstraints()) {
      this.checkFinalizedConstraints()
      this.errorStatus = false;
      loop1: for (let index = 0; index < Object.keys(this.data).length; index++) {
        this.sectionMap.set(Object.keys(this.data)[index], this.data[Object.keys(this.data)[index]])
        for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
          let subSections = this.data[Object.keys(this.data)[index]][0]
          for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
            for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
              let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q]
              formId = question.formId
              // if (question.finalizeMandatory == true) {
              switch (question.controlType) {
                case "geolocation":
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == "")) {
                    this.errorStatus = true;
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please enter " + question.label);
                    break loop1;
                  }
                  break;
                case "camera":
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == "")) {
                    this.errorStatus = true;
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please enter " + question.label);
                    break loop1;
                  }
                  break;
                case 'dropdown':
                  for (let i = 0; i < question.options.length; i++) {
                    if (question.finalizeMandatory == true && question.displayComponent == true && question.value == null) {
                      this.errorStatus = true
                      this.section = Object.keys(this.data)[index]
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please select " + question.label)
                      break loop1
                    } else if (question.finalizeMandatory == true && question.value == null && (question.displayComponent == true || question.displayComponent == null)) {
                      this.errorStatus = true
                      this.section = Object.keys(this.data)[index]
                      this.errorColor(Object.keys(this.data)[index], question.columnName);
                      this.messageService.showErrorToast("Please select " + question.label)
                      break loop1
                    }
                  }
                  break;
                case 'textbox':
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == '')) {
                    this.errorStatus = true
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please enter a valid " + question.label)
                    break loop1
                  }
                  break;
                case 'Time Widget':
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == '')) {
                    this.errorStatus = true
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please select " + question.label)
                    break loop1
                  }
                  break;
                case 'Date Widget':
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == '')) {
                    this.errorStatus = true
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please select " + question.label)
                    break loop1
                  }
                  break;
                case 'Month Widget':
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == '')) {
                    this.errorStatus = true
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please select " + question.label)
                    break loop1
                  }
                  break;
                case 'checkbox':
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == '')) {
                    this.errorStatus = true
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please enter " + question.label)
                    break loop1
                  }
                  break;
                case "table":
                case 'tableWithRowWiseArithmetic':
                  let tableData = question.tableModel
                  for (let i = 0; i < tableData.length; i++) {
                    for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                      let cell = (tableData[i])[Object.keys(tableData[i])[j]]
                      if (typeof cell == 'object') {
                        if (cell.finalizeMandatory == true && cell.displayComponent == true && (cell.value == null || cell.value == '')) {
                          this.errorStatus = true
                          this.errorColor(Object.keys(this.data)[index], cell.columnName, "table");
                          this.messageService.showErrorToast("Please enter " + question.label + " " + (cell.label).replace('@@split@@', ''))
                          break loop1;
                        }
                        //  else if (cell.dependecy == true && cell.mandatory == 'yes' && cell.typeDetailIdOfDependencyType == this.questionMap[cell.dependentColumn.split(',')[0] as any].value &&
                        //   (cell.value == null || (cell.value as string).trim() == "")) {
                        //   this.errorStatus = true
                        //   this.sectionSelected(Object.keys(this.data)[index])
                        //   this.messageService.showErrorToast("Please enter " + question.label + " " + (cell.label).replace('@@split@@', ''))
                        //   break loop1;
                        // }
                      }
                    }
                  }
                  break;
                case 'textarea':
                  if (question.finalizeMandatory == true && question.displayComponent == true && (question.value == null || question.value == '')) {
                    this.errorStatus = true
                    this.section = Object.keys(this.data)[index]
                    this.errorColor(Object.keys(this.data)[index], question.columnName);
                    this.messageService.showErrorToast("Please enter " + question.label)
                    break loop1
                  } else {
                    question.showErrMessage = false
                  }
                  break;
                case 'beginrepeat':
                  for (let bgindex = 0; bgindex < question.beginRepeat.length; bgindex++) {
                    let beginRepeatQuestions: IQuestionModel[] = question.beginRepeat[bgindex];
                    for (let beginRepeatQuestion of beginRepeatQuestions) {
                      if (beginRepeatQuestion.finalizeMandatory == true && beginRepeatQuestion.displayComponent == true && (beginRepeatQuestion.value == null || beginRepeatQuestion.value == '')) {
                        this.errorStatus = true
                        this.section = Object.keys(this.data)[index]
                        this.errorColor(Object.keys(this.data)[index], beginRepeatQuestion.columnName);
                        this.messageService.showErrorToast("Please enter " + beginRepeatQuestion.label)
                        break loop1
                      }
                    }
                  }
                  break;
              }

              if (question.reviewHeader) {
                switch (question.controlType) {
                  case 'segment':
                  case 'dropdown':
                    if (question.value == null || question.value == "" || question.value == undefined) {
                      // headerData[question.reviewHeader] = "";
                      break;
                    }
                    if (question.type == "option") {
                      headerData[question.reviewHeader] = question.options && question.options.length ?
                        question.options.filter(d => d.key === question.value)[0].value : question.value;
                    } else {
                      // for checkbox
                      let names = "";
                      let vals = question.options.filter(d => question.value.find(element => {
                        return (d.key === element)
                      }))
                      vals.forEach(e => {
                        names = names + e.value + ","
                      })
                      if (names.includes(",")) {
                        names = names.substring(0, names.lastIndexOf(","))
                      }
                      headerData[question.reviewHeader] = names;
                    }
                    break;
                  case 'textbox':
                    headerData[question.reviewHeader] = question.value;
                    break;
                  case 'camera':
                    headerData[question.reviewHeader] = question.value.src;
                    image = question.value.src;
                    break;
                }
              }
            }
          }
        }
      }
      if (!this.errorStatus) {

        this.messageService.stopLoader();
        let confirm = this.alertCtrl.create({
          enableBackdropDismiss: false,
          cssClass: 'custom-font',
          title: 'Warning',
          message: "Once you finalize the form, it cannot be further edited.<br><br><strong>Are you sure you want to finalize this form?</strong>",
          buttons: [{
              text: 'No',
              handler: () => {
                // this.navCtrl.pop()
              }
            },
            {
              text: 'Yes',
              handler: async () => {
                this.data = await this.commonsEngineProvider.getKeyValue(this.data, this.userService.user.username, this.formId, this.uniqueId, null)
                        
                this.dbFormModel = {
                  createdDate: this.createdDate,
                  updatedDate: this.updatedDate,
                  formStatus: type == 'save' ? 'save' : 'finalized',
                  extraKeys: null,
                  formData: this.data,
                  formSubmissionId: formId,
                  uniqueId: this.uniqueId,
                  formDataHead: headerData,
                  image: image,
                  attachmentCount: 0,
                  visitedDate: visitedDate,
                  formId: this.formId
                }
                // this.data = this.commonsEngineProvider.sanitizeOptions(this.data)
                this.formService.saveData(this.formId, this.dbFormModel, this.saveType).then(data => {
                  if (data == 'data') {
                    this.navCtrl.pop()
                    this.messageService.showSuccessToast(ConstantProvider.message.finalizedSuccess)
                  } else {
                    this.navCtrl.pop()
                  }
                });

              }
            }
          ]
        });
        confirm.present();
      } else {
        this.messageService.stopLoader();
      }
      // }
    } else {
      if (!this.errorStatus) {
        
          this.data = await this.commonsEngineProvider.getKeyValue(this.data, this.userService.user.username, this.formId, this.uniqueId, null)
        // this.data = this.commonsEngineProvider.sanitizeOptions(this.data)
        this.dbFormModel = {
          createdDate: this.createdDate,
          updatedDate: this.updatedDate,
          formStatus: type == 'save' ? 'save' : 'finalized',
          extraKeys: null,
          formData: this.data,
          formSubmissionId: formId,
          uniqueId: this.uniqueId,
          formDataHead: headerData,
          image: image,
          attachmentCount: 0,
          visitedDate: visitedDate,
          formId: this.formId
        }
        await this.formService.saveData(this.formId, this.dbFormModel, this.saveType).then(data => {
          if (data == 'data') {
            this.navCtrl.pop()
            if (type == 'save') {
              this.messageService.stopLoader();
              this.messageService.showSuccessToast(ConstantProvider.message.saveSuccess)
            } else {
              this.messageService.showSuccessToast(ConstantProvider.message.finalizedSuccess)
            }
          } else {
            this.navCtrl.pop()
          }
        });
      } else {
        this.messageService.stopLoader();
      }
    }
  }

  /**
   * This method checks ie the field should not take the value after current year
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param e
   * @param type
   * @param question
   */
  checkNumber(e, type, question) {
    if (question.type == 'tel') {
      let newValue = e.target.value;
      let pass = /[4][8-9]{1}/.test(e.charCode) || /[5][0-7]{1}/.test(e.charCode) || e.keyCode === 8 || e.keyCode === 32;
      if (!pass) {
        let regExp = new RegExp('^[0-9?]+$');
        if (!regExp.test(newValue)) {
          e.target.value = newValue.slice(0, -1);
        }
      }
    }
  }

  /**
   * This method is for table data calculation.
   *
   * @author Azhar (azaruddin@sdrc.co.in)
   * @param cell
   * @param columnIndex
   * @param rowIndex
   * @param tableModel
   */
  calculateTableArithmetic(cellQ: any, columnIndex: number, rowIndex: number, tableModel: IQuestionModel[]) {
    //-------------calculation using features array-------------------------
    let cellEventSource = tableModel[rowIndex][Object.keys(tableModel[rowIndex])[columnIndex]];
    let fresult: number = null;
    let cells = this.questionFeaturesArray[cellEventSource.columnName];
    if (cells) {
      for (let cell of cells) {
        if (typeof cell == "object" && cell.features != null && cell.features.includes("exp:") && cell.features.includes("{" + cellEventSource.columnName + "}")) {
          for (let feature of cell.features.split("@AND")) {
            switch (feature.split(":")[0]) {
              case "exp":
                for (let cols of feature.split(":")[1].split("&")) {
                  let arithmeticExpression = feature.split(":")[1];
                  let result = this.engineUtilsProvider.resolveExpression(arithmeticExpression, this.questionMap, "default");
                  if (result != null && result != "NaN" && cell.type == "tel") {
                    fresult = parseInt(result as string);
                    cell.value = fresult;
                  }
                  if (fresult == null) {
                    cell.value = null;
                  }
                }
                break;
            }
          }
        }
      }
    }
    // let constraintsCells = this.constraintsArray[cellEventSource.columnName];
    // if (constraintsCells) {
    //   for (let constraintsCell of constraintsCells) {
    //     if ((constraintsCell.controlType == 'cell') && (constraintsCell.constraints != null) && (constraintsCell.constraints.includes('exp:'))) {
    //       for (let c of constraintsCell.constraints.split('@AND')) {
    //         switch (c.split(":")[0]) {
    //           case 'exp':
    //             {
    //               let exp = c.split(":")[1]
    //               let rr = this.engineUtilsProvider.resolveExpression(exp, this.questionMap, "constraint")
    //               if (parseInt(rr) == 0) {
    //                 // if in some devices data is not clear make sure to increase delay time
    //                 setTimeout(() => {

    //                   this.questionMap[cellEventSource.columnName].value = null;
    //                   this.checkRelevance(cellEventSource);
    //                   this.clearFeatureFilters(cellEventSource);
    //                   this.compute(cellEventSource);
    //                   this.validateBeginRepeat(cellEventSource.columnName);
    //                   this.calculateScore(cellEventSource)
    //                 }, 500)
    //               }
    //             }
    //             break;
    //         }
    //       }
    //     }
    //   }
    // }
  }


  /**
   * This method check the number pattern and decimal pattern
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param event
   * @param question
   */
  numberInput(event, question) {
    if (question.type == 'tel') {

      let pass = /[4][8-9]{1}/.test(event.charCode) || /[5][0-7]{1}/.test(event.charCode) || event.keyCode === 8;
      if (!pass) {
        return false;
      }
    } else if (question.type == 'singledecimal') {
      let pass = event.charCode == 46 || (event.charCode >= 48 && event.charCode <= 57) || event.keyCode === 8;
      if (!pass) {
        return false;
      }
    } else {
      // return event.charCode >= 97 && event.charCode <= 122 || event.charCode >= 65 && event.charCode <= 90 || event.charCode == 32
      return true;
    }
  }



  /**
   * This method is use to compute 2 field and the result should be shown in another filed
   *
   * @author Azhar (azaruddin@sdrc.co.in)
   * @param event
   * @param focusedQuestion
   */
  compute(focusedQuestion: IQuestionModel) {
    if (focusedQuestion.type == "tel") {
      let dependencies = this.questionFeaturesArray[focusedQuestion.columnName];
      if (dependencies) {
        for (let question of dependencies) {
          if (question.controlType == "textbox" && question.features != null && question.features.includes("exp:")) {
            for (let feature of question.features.split("@AND")) {
              switch (feature.split(":")[0]) {
                case "exp":
                  let expression = feature.split(":")[1];
                  let result = this.engineUtilsProvider.resolveExpression(expression, this.questionMap, "default");
                  if (result != null && result != "NaN" && result != NaN && result != "null") question.value = String(result);
                  else question.value = null;
                  this.calculateScore(question);
                  break;
              }
            }
          }
          if (question.features != null) {
            this.compute(question);
          }
        }
        if (this.beginRepeatArray[focusedQuestion.columnName]) this.validateBeginRepeat(focusedQuestion);
      }
    }
  }

  /**
   * This method is used to validate begin repeat section
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param bgParentQuestion
   */
  validateBeginRepeat(focusedQuestion) {
    if (this.beginRepeatArray[focusedQuestion.columnName]) {
      let bgQuestion = this.beginRepeatArray[focusedQuestion.columnName];
      let dependentQuestion = this.questionMap[focusedQuestion.columnName];
      if (dependentQuestion.value != null && dependentQuestion.value > 0) {
        bgQuestion.beginrepeatDisableStatus = false;
        this.setRenderDefault(true, bgQuestion);
      }
      if (dependentQuestion.value == null || dependentQuestion.value == 0) {
        while (bgQuestion.beginRepeat.length > 1) {
          bgQuestion.beginRepeat.pop();
        }
        for (let i = 0; i < bgQuestion.beginRepeat.length; i++) {
          for (let j = 0; j < bgQuestion.beginRepeat[i].length; j++) {
            bgQuestion.beginRepeat[i][j].value = null;
          }
        }
        bgQuestion.beginrepeatDisableStatus = true;
      } else {
        let diff = bgQuestion.beginRepeat.length - dependentQuestion.value;
        while (diff >= 1) {
          bgQuestion.beginRepeat.pop();
          diff--;
        }
      }
      if (bgQuestion.beginRepeat.length == 1) {
        bgQuestion.beginRepeatMinusDisable = true;
      } else {
        bgQuestion.beginRepeatMinusDisable = false;
      }
    }
  }

  /**
   * This method is called to set the default value of the field by checking the status ie dependentCondition == render_default
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param status
   * @param bgquestion
   */
  setRenderDefault(status: boolean, bgquestion: IQuestionModel) {
    let beginrepeat = bgquestion.beginRepeat
    for (let i = 0; i < beginrepeat.length; i++) {
      for (let j = 0; j < beginrepeat[i].length; j++) {
        if (beginrepeat[i][j].controlType == 'dropdown') {
          if (beginrepeat[i][j].features && beginrepeat[i][j].features.includes('render_default')) {
            if (status) {
              beginrepeat[i][j].value = Number(beginrepeat[i][j].defaultValue)
            } else {
              beginrepeat[i][j].value = null
            }
          }
        }
      }
    }
  }

  /**
   * This method is used to check the relevance, based on the relevance the status for the  displayComponent, disable status, dependency , value will be
   * change accordingly to show/hide the field with clearing of value of that field.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   * @param type
   */
  checkRelevance(question: IQuestionModel, type ? : string) {
    if (type == "ui" && question.constraints != null && question.constraints.split(":")[0] == 'clear') {
      setTimeout(() => {
        this.questionMap[question.constraints.split(":")[1]].value = null
      }, 100)
    }
    if (this.questionDependencyArray[question.columnName + ":" + question.key + ":" + question.controlType + ":" + question.label] != null)
      for (let q of this.questionDependencyArray[question.columnName + ":" + question.key + ":" + question.controlType + ":" + question.label]) {
        let arithmeticExpression: String = this.engineUtilsProvider.expressionToArithmeticExpressionTransfomerForRelevance(q.relevance, this.questionMap);
        let rpn: String[] = this.engineUtilsProvider.transformInfixToReversePolishNotationForRelevance(arithmeticExpression.split(" "));
        let isRelevant = this.engineUtilsProvider.arithmeticExpressionResolverForRelevance(rpn);
        q.tempFinalizedMandatory = false;
        q.tempSaveMandatory = false;

        if (isRelevant) {
          q.displayComponent = isRelevant;
          if (q.defaultSettings == "disabled") {
            q.disabled = true
          } else {
            q.disabled = false
          }
          q.dependecy = true;
          if (q.defaultSettings && (q.defaultSettings.split(",")[0].split(":")[0] == "prefetchNumber")) {
            q.value = Number(q.defaultSettings.split(",")[0].split(":")[1])
          }
          if (q.finalizeMandatory && isRelevant) q.tempFinalizedMandatory = true;
          if (q.saveMandatory && isRelevant) q.tempSaveMandatory = true;
        } else {
          q.displayComponent = false;
          q.disabled = true
          q.value = null;
          // recalculate the score for those components, which have dependency of parent component.
          this.calculateScore(q)
          //clear the attachment, if any component have the control type file/mfile and have on dependency on some
          // other component(and isRelevant for that component is false).
          if (q.controlType == "file" || q.controlType == "mfile") q.attachedFiles = [];
          q.dependecy = false;
          q.duplicateFilesDetected = false;
          q.errorMsg = null;
          q.wrongFileExtensions = false;
          q.fileSizeExceeds = false;
          if (q.finalizeMandatory && !isRelevant) q.tempFinalizedMandatory = false;
          if (q.saveMandatory && !isRelevant) q.tempSaveMandatory = false;
        }
      }
  }

  /**
   * This method will clear the value of the field that contains some feature like area_group, filter_single, filter_multiple.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  clearFeatureFilters(question: IQuestionModel) {

    if (this.questionFeaturesArray[question.columnName + ':' + question.key + ':' + question.controlType + ':' + question.label] != null)
      for (let q of this.questionFeaturesArray[question.columnName + ':' + question.key + ':' + question.controlType + ':' + question.label]) {
        for (let feature of q.features.split("@AND")) {
          switch (feature) {
            case 'area_group':
            case "filter_single":
            case "filter_multiple":
              q.value = null;
              break;
          }
        }
      }
  }

  /**
   * This method is used to sync the data between 2 component, both having relevance with each other
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   * @param parentQuestion
   * @param event
   */
  syncGroup(question: IQuestionModel, parentQuestion: IQuestionModel, event) {
    if (question.features == null) return;
    for (let feature of question.features.split("@AND")) {
      feature = feature.trim()
      switch (feature.split(":")[0].trim()) {
        case "date_sync": {
          let groupQuestions = "";
          for (let f of feature.split(":")[1].split("&")) {
            groupQuestions = groupQuestions + f + ",";
          }
          groupQuestions = groupQuestions.substring(0, groupQuestions.length - 1);
          switch (question.controlType) {
            case "Date Widget": {
              if (question.value != null) {
                for (let qcolname of groupQuestions.split(",")) {
                  let groupQuestion: IQuestionModel;
                  if (parentQuestion == null) groupQuestion = this.questionMap[qcolname] as IQuestionModel;
                  else if (parentQuestion.controlType == "beginrepeat") {
                    let rowIndexOfQuestion = question.columnName.split("-")[1];
                    let questions: IQuestionModel[] = parentQuestion.beginRepeat[rowIndexOfQuestion];
                    for (let ques of questions) {
                      if (ques.columnName == qcolname) {
                        groupQuestion = this.questionMap[ques.columnName] as IQuestionModel;
                        break;
                      }
                    }
                  }
                  switch (groupQuestion.controlType) {
                    case "textbox": {
                      let dt1 = new Date();
                      let dt2 = new Date(question.value);
                      var diff = (dt1.getTime() - dt2.getTime()) / 1000;
                      diff /= 60 * 60 * 24;
                      let yearDiff = Math.abs(Math.round(diff / 365.25));
                      groupQuestion.value = String(yearDiff);
                    }
                    break;
                  case "dropdown": {
                    let dt1 = new Date();
                    let dt2 = new Date(question.value);
                    let diff = (dt1.getTime() - dt2.getTime()) / 1000;
                    diff /= 60 * 60 * 24;
                    let yearDiff = Math.abs(Math.round(diff / 365.25));
                    let enteredValue = yearDiff;
                    for (let option of groupQuestion.options) {
                      let start: number;
                      let end: number;
                      if ((option["value"] as String).includes("-")) {
                        start = parseInt(option["value"].split("-")[0]);
                        end = parseInt(option["value"].split("-")[1]);
                        if (enteredValue >= start && enteredValue <= end) {
                          groupQuestion.value = option["key"];
                          break;
                        }
                      } else {
                        start = parseInt(option["value"].split(" ")[0]);
                        if (enteredValue >= start) {
                          groupQuestion.value = option["key"];
                          break;
                        }
                      }
                    }
                  }
                  }
                }
              }
            }
            break;
          case "textbox": {
            for (let qcolname of groupQuestions.split(",")) {
              let groupQuestion: IQuestionModel = this.questionMap[qcolname] as IQuestionModel;
              // if (parentQuestion == null)
              //   groupQuestion = this.questionMap[qcolname] as IQuestionModel
              // else if (parentQuestion.controlType == 'beginrepeat') {
              //   let rowIndexOfQuestion = question.columnName.split('-')[1]
              //   let questions: IQuestionModel[] = parentQuestion.beginRepeat[rowIndexOfQuestion]
              //   for (let ques of questions) {
              //     if (ques.columnName == qcolname) {
              //       groupQuestion = this.questionMap[ques.columnName] as IQuestionModel
              //       break;
              //     }
              //   }
              // }
              if (question.value == null || question.value == "") {
                groupQuestion.value = null;
              }
              switch (groupQuestion.controlType) {
                case "dropdown": {
                  let enteredValue = question.value;
                  for (let option of groupQuestion.options) {
                    let start: number;
                    let end: number;
                    if ((option["value"] as String).includes("-")) {
                      start = parseInt(option["value"].split("-")[0]);
                      end = parseInt(option["value"].split("-")[1]);
                      if (parseInt(enteredValue) >= start && parseInt(enteredValue) <= end) {
                        groupQuestion.value = option["key"];
                        break;
                      }
                    } else {
                      start = parseInt(option["value"].split(" ")[0]);
                      if (parseInt(enteredValue) >= start) {
                        groupQuestion.value = option["key"];
                        break;
                      }
                    }
                  }
                  if (question.value == null || question.value == "") {
                    groupQuestion.value = null;
                  }
                }
                break;
              case "Date Widget": {
                if (event == "" || (event.keyCode != undefined && event.keyCode === 8)) {
                  groupQuestion.value = null;
                }
              }
              break;
              }
            }
          }
          break;
          }
          break;
        }
        case "area_group":
        case "filter_single": {
          if (question.features != null && (question.features.includes("area_group") || question.features.includes("filter_single"))) {
            let optionCount = 0
            let groupQuestions = feature.split(":")[1];
            let childLevelQuestion = this.questionMap[groupQuestions];
            for (let option of childLevelQuestion.options) {
              if (!option['parentId2']) {
                if (option["parentId"] == question.value) {
                  option["visible"] = true;
                } else {
                  option["visible"] = false;
                  optionCount++
                }
              } else {
                if (option["parentId"] == question.value && this.questionMap['f1FacilityType'].value == option["parentId2"]) {
                  option["visible"] = true;
                } else {
                  option["visible"] = false;
                  optionCount++
                }

              }

            }
            // if (optionCount == childLevelQuestion.options.length) {
            //   childLevelQuestion.disabled = true
            // } else {
            //   childLevelQuestion.disabled = false
            // }
            childLevelQuestion.value = null;
          }
        }
        break;
      case "filter_multiple": {
        if (question.features != null && question.features.includes("filter_multiple")) {
          let groupQuestions = feature.split(":")[1];
          let childLevelQuestion = this.questionMap[groupQuestions];
          for (let option of childLevelQuestion.options) {
            option["visible"] = false;
            for (let parentId of option["parentIds"]) {
              if (parentId == question.value) {
                option["visible"] = true;
                break;
              }
            }
          }
          childLevelQuestion.value = null;
        }
      }
      break;

      case "filterByExp": {
        if (question.features != null && feature.includes("filterByExp") && !feature.includes("filterByExp:({when")) {
          feature = feature.trim()
          let optionCount = 0
          let groupQuestions = feature.split(":")[1];
          let childLevelQuestion = this.questionMap[groupQuestions];
          for (let option of childLevelQuestion.options) {
            option["visible"] = false;
            let result = this.engineUtilsProvider.resolveExpression(option["filterByExp"], this.questionMap, "default");
            if (result > 0) {
              option["visible"] = true;
            } else {
              option["visible"] = false;
              optionCount++
            }
          }
          if (optionCount == childLevelQuestion.options.length) {
            childLevelQuestion.disabled = true
          } else {
            childLevelQuestion.disabled = false
          }
          childLevelQuestion.value = null;
        } else if (question.features != null && feature.includes("filterByExp") && feature.includes("filterByExp:({when")) {
          feature = feature.trim()
          let expressionArr = feature.split(":");
          expressionArr.shift()
          let colName = expressionArr[expressionArr.length - 1].split(".")[0];
          let modExp = ""
          for (let index = 0; index < expressionArr.length - 1; index++) {
            modExp = modExp + expressionArr[index] + ":"
          }
          modExp = modExp.substring(0, modExp.length - 1)

          let index = 0;
          for (let option of this.questionMap[colName].options) {
            if (option['key'] == 31247) {
              option['key']
            }
            option['visible'] = false
            let arithmeticExpression: String = this.engineUtilsProvider.expressionToArithmeticExpressionTransfomerForRelevance(modExp, this.questionMap, index);
            index++
            let rpn: String[] = this.engineUtilsProvider.transformInfixToReversePolishNotationForRelevance(arithmeticExpression.split(" "));
            let isRelevantToDisplay = this.engineUtilsProvider.arithmeticExpressionResolverForRelevance(rpn);


            if (isRelevantToDisplay) {
              option['visible'] = true
            }
          }
        }
      }
      break;
      case "dropdown_auto_select": {}
      }
    }
    this.checkRelevanceForEachQuestion()
  }

  /**
   * This methos is used to restict the copypaste value in the respective field.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  onPaste(question: any) {
    setTimeout(() => {
      question.value = null;
    }, 0);
  }


  /**
   * This method is called for each and every field tp check the null or blank value, it returns false if tyhe value is null or black
   * in any of the field or else return true.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param data
   */
  checkFieldContainsAnyvalue(data: any) {
    for (let index = 0; index < Object.keys(this.data).length; index++) {
      for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
        let subSections = this.data[Object.keys(this.data)[index]][0]
        for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
          for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
            let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q]

            switch (question.controlType) {
              case "textbox":
                if (question.value != null && (question.value as string).trim() != "")
                  return true
                break;
              case "dropdown":
              case "segment":
                if (question.value != null && question.value != "")
                  return true
                break;
              case "Time Widget":
                if (question.value != null && question.value != "")
                  return true
                break;
              case "Date Widget":
              case "Month Widget":
                if (question.value != null && question.value != "")
                  return true
                break;
              case "checkbox":
                if (question.value != null && question.value != "")
                  return true
                break;
              case 'tableWithRowWiseArithmetic': {
                let tableData = question.tableModel
                let tableArray: any[] = []
                for (let i = 0; i < tableData.length; i++) {
                  let tableRow: {} = {}
                  for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                    let cell = (tableData[i])[Object.keys(tableData[i])[j]]
                    if (typeof cell == 'object') {
                      if (cell.value != null && cell.value.trim() != "")
                        return true
                      break;
                    }
                  }
                }
              }
              break;
            case "beginrepeat":
              let beginrepeat = question.beginRepeat
              let beginrepeatArray: any[] = []
              let beginrepeatMap: {} = {}
              for (let i = 0; i < beginrepeat.length; i++) {
                beginrepeatMap = {}
                for (let j = 0; j < beginrepeat[i].length; j++) {
                  let colName = (beginrepeat[i][j].columnName as String).split('-')[3]
                  beginrepeatMap[colName] = beginrepeat[i][j].value
                  // console.log('begin-repeat', beginrepeat[i][j])
                  switch (beginrepeat[i][j].controlType) {
                    case "textbox":
                      if (beginrepeat[i][j].value != null && (beginrepeat[i][j].value as string).trim() != "")
                        return true
                      break;
                    case "dropdown":
                    case "segment":
                      if (beginrepeat[i][j].value != null && beginrepeat[i][j].valu != "")
                        return true
                      break;
                    case "Time Widget":
                      if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "")
                        return true
                      break;
                    case "Date Widget":
                      if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "")
                        return true
                      break;
                    case "Month Widget":
                      if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "")
                        return true
                      break;
                    case "checkbox":
                      if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "")
                        return true
                      break;
                  }
                }
                beginrepeatArray.push(beginrepeatMap)
              }
              break;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * This method will add the columns to the "questionDependencyArray" having any relevances for further use.
   *
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param expression
   * @param question
   */
  drawDependencyGraph(expression: String, question: any) {
    for (let str of expression.split("}")) {
      let expressions: String[] = str.split(":");
      for (let i = 0; i < expressions.length; i++) {
        let exp: String = expressions[i];
        switch (exp) {
          case "optionEquals":
          case "optionEqualsMultiple": {
            let dColName: any = expressions[i - 1];
            if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
            } else if (question.dependecy == true) {
              let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
              let keyFound = false
              for (let dps of a) {
                if (dps.columnName == question.columnName)
                  keyFound = true
              }
              if (!keyFound) {
                a.push(this.questionMap[question.columnName]);
                this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = a;

              }

            }
            i = i + 2;
          }
          break;
        case "textEquals":
        case "equals":
        case "greaterThan":
        case "greaterThanEquals":
        case "lessThan":
        case "lessThanEquals": {
          let dColName: any = expressions[i - 1];
          if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
            this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
          } else if (question.dependecy == true) {
            let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
            let keyFound = false
            for (let dps of a) {
              if (dps.columnName == question.columnName)
                keyFound = true
            }
            if (!keyFound) {
              a.push(this.questionMap[question.columnName]);
              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = a;

            }
          }
          i = i + 1;
        }
        break;
        }
      }
    }
  }

  /**
   * This method is use to set the default values having any constraints or features
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  setupDefaultSettingsAndConstraintsAndFeatureGraph(question: IQuestionModel): IQuestionModel {
    if (question.defaultSettings != null) {
      for (let settings of question.defaultSettings.split(',')) {

        switch (settings.split(":")[0]) {
          case 'current_date':
            question.value = this.datepipe.transform(new Date(), "yyyy-MM-dd")
            break;
          case "prefetchNumber":
            question.value = parseInt(settings.split(":")[1])
            break;
          case 'prefetchText':
            question.value = new String(settings.split(":")[1]);
            break;
          case 'prefetchDropdownWithValue':
            question.value = new Number(settings.split(":")[1]);
            break;
          case 'disabled':
            question.disabled = true
            break;
          case "prefetchDate":
            if (settings.split(":")[1] == 'current_date') {
              if (question.value == null) {
                question.value = this.datepipe.transform(new Date(), "yyyy-MM-dd")
              }
            }
            break;
        }
      }
    }
    if (question.constraints != null) {
      for (let settings of question.constraints.split(',')) {

        switch (settings.split(":")[0]) {
          case 'maxLength':
            question.maxLength = parseInt(settings.split(":")[1])
            break;
          case 'minLength':
            question.minLength = parseInt(settings.split(":")[1])
            break;
          case "maxValue":
            question.maxValue = parseInt(settings.split(":")[1]);
            break;
          case "minValue":
            question.minValue = parseInt(settings.split(":")[1]);
            break;
          case 'lessThan':
          case 'lessThanEquals':
          case 'greaterThan':
          case 'greaterThanEquals':
          case 'exp':

            if (this.constraintsArray[question.columnName] == null)
              this.constraintsArray[question.columnName] = [question]
            else {
              let constraints = this.constraintsArray[question.columnName]
              constraints.push(question)
              this.constraintsArray[question.columnName] = constraints
            }
            break;
          case 'limit_bg_repeat':
            question.limit_bg_repeat = settings
            let dcolName = settings.split(":")[1]
            question.bgDependentColumn = dcolName
            this.beginRepeatArray[dcolName] = question
            break;
        }
      }
    }
    if (question.features != null) {
      for (let features of question.features.split("@AND")) {
        switch (features.split(":")[0]) {
          case "exp":
            let exp = features.split(":")[1] as String;
            let str = exp.split("");
            for (let i = 0; i < str.length; i++) {
              if (str[i] == "$") {
                let qName = "";
                for (let j = i + 2; j < str.length; j++) {
                  if (str[j] == "}") {
                    i = j;
                    break;
                  }
                  qName = qName + str[j];
                }
                if (this.questionFeaturesArray[qName] == undefined) this.questionFeaturesArray[qName] = [question];
                else {
                  let a = this.questionFeaturesArray[qName];
                  a.push(question);
                  this.questionFeaturesArray[qName] = a;
                }
              }
            }
            break;
        }
      }
    }


    return question
  }

  /**
   * This method is called when user clicks on time widget to set the appropriate time in 24hr format.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  open(question: any) {
    if (!this.disableStatus) {
      const amazingTimePicker = this.atp.open();
      amazingTimePicker.afterClose().subscribe(time => {
        question.value = time;
        if (question.constraints != null && question.constraints != "" && question.controlType == "Time Widget") {
          for (let settings of question.constraints.split("@AND")) {
            switch (settings.split(":")[0]) {
              case "lessThan":
              case "greaterThan": {
                if (question.value != null && this.questionMap[question.constraints.split(":")[1]].value != null) {
                  this.questionMap[question.constraints.split(":")[1]].value = null
                }
              }
            }
          }
        }
      });
    }
  }

  /**
   * This method is used to check the constraints(greatherThan/lessThan) on thr month widget.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  onMonthChanged(question: IQuestionModel) {
    if (question.constraints != null) {
      if (question.constraints.split(":")[0] == "greaterThan" && (this.questionMap[question.constraints.split(":")[1].split("-")[0]].value != null) && question.value != null) {

        let yearone = parseInt(this.questionMap[question.constraints.split(":")[1].split("-")[0]].value.split("-")[0])
        let monthone = parseInt(this.questionMap[question.constraints.split(":")[1].split("-")[0]].value.split("-")[1])
        let yeartwo = parseInt(question.value.split("-")[0])
        let monthtwo = parseInt(question.value.split("-")[1])
        if (yeartwo < yearone) {
          this.messageService.showErrorToast(question.cmsg);
          setTimeout(() => {
            question.value = null;
          }, 100)
        } else if (yeartwo == yearone && monthtwo < monthone) {
          this.messageService.showErrorToast(question.cmsg);
          setTimeout(() => {
            question.value = null;
          }, 100)
        } else {
          let numberOfMonths = (yeartwo - yearone) * 12 + (monthtwo - monthone);
          if (numberOfMonths < 2) {
            this.questionMap[question.constraints.split(":")[1].split("-")[1]].value = 3
          } else if (numberOfMonths < 3) {
            this.questionMap[question.constraints.split(":")[1].split("-")[1]].value = 2
          } else if (numberOfMonths == 3) {
            this.questionMap[question.constraints.split(":")[1].split("-")[1]].value = 1
          } else {
            this.questionMap[question.constraints.split(":")[1].split("-")[1]].value = 0
          }
          let sectionScoreKeeper = this.sectionMapScoreKeeper[this.questionMap['f3qescore'].sectionName]
          let sectionSum = 0;
          if (sectionScoreKeeper != undefined) {
            for (let scoreHolder of this.sectionScoreKeyMapper[sectionScoreKeeper.columnName]) {
              sectionSum = Number(sectionSum) + Number(scoreHolder.value);
            }
            sectionScoreKeeper.value = Number(sectionSum);
          }

        }
      } else if (question.constraints.split(":")[0] == "lessThan") {
        this.questionMap[question.constraints.split(":")[1].split("-")[0]].value = null
        this.questionMap[question.constraints.split(":")[1].split("-")[1]].value = null
        this.questionMap['f3qescore'].value = null
      }
    }
  }

  /**
   * This method is used to set the date value.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question1
   * @param question2
   * @param event
   */
  onDateChanged(question1: any, question2: any, event: IMyDateModel) {
    this.syncGroup(question1, question2, null);
  }

  /**
   * This method is called to check all the constraint ie (min value, max value, greaterThan, lessThan).
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question1
   * @param question2
   */
  checkMinMax(question1, question2) {
    if (question1.value != null && question1.constraints != "" && question1.controlType == "textbox") {
      if (question1.maxValue != null && Number(question1.value) >= question1.maxValue) {
        question1.value = null;
        this.syncGroup(question1, question2, null);
        return (question1.value = null);
      } else if (question1.maxValue != null && Number(question1.value) <= question1.minValue) {
        question1.value = null;
        this.syncGroup(question1, question2, null);
        return (question1.value = null);
      }
    } else if (question1.value != null && question1.constraints != "" && question1.controlType == "Time Widget") {
      for (let settings of question1.constraints.split("@AND")) {
        switch (settings.split(":")[0].trim()) {
          case "greaterThan": {
            if (this.questionMap[settings.split(":")[1]].value != null && question1.value) {
              let timeOfConstraint = this.questionMap[settings.split(":")[1]].value;
              let timeOfActiveQ = question1.value;
              let hourOfConstraint = parseInt(timeOfConstraint.split(":")[0]);
              let minuteOfConstraint = parseInt(timeOfConstraint.split(":")[1]);
              let hourOfActiveQ = parseInt(timeOfActiveQ.split(":")[0]);
              let minuteOfActiveQ = parseInt(timeOfActiveQ.split(":")[1]);
              // passing year, month, day, hourOfA and minuteOfA to Date()
              let dateOfConstraint: Date = new Date(2010, 6, 15, hourOfConstraint, minuteOfConstraint);
              let dateOfActiveQ: Date = new Date(2010, 6, 15, hourOfActiveQ, minuteOfActiveQ);
              if (dateOfConstraint > dateOfActiveQ) {
                setTimeout(() => {
                  question1.value = null;
                }, 100)
              }
            }
          }
          case "lessThan": {
            if (this.questionMap[settings.split(":")[1]].value != null && question1.value) {
              let timeOfConstraint = this.questionMap[settings.split(":")[1]].value;
              let timeOfActiveQ = question1.value;
              let hourOfConstraint = parseInt(timeOfConstraint.split(":")[0]);
              let minuteOfConstraint = parseInt(timeOfConstraint.split(":")[1]);
              let hourOfActiveQ = parseInt(timeOfActiveQ.split(":")[0]);
              let minuteOfActiveQ = parseInt(timeOfActiveQ.split(":")[1]);
              // passing year, month, day, hourOfA and minuteOfA to Date()
              let dateOfConstraint: Date = new Date(2010, 6, 15, hourOfConstraint, minuteOfConstraint);
              let dateOfActiveQ: Date = new Date(2010, 6, 15, hourOfActiveQ, minuteOfActiveQ);
              if (dateOfActiveQ > dateOfConstraint) {
                setTimeout(() => {
                  question1.value = null;
                }, 100)
              }
            }
          }
        }
      }
    }
  }

  /**
   * This method will help to select the (file or image) for uploading. it will also checking what max number offile a user can select ,
   * what should be the max file can be upload each, which file extension should be allow.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param event
   * @param question
   */
  onFileChange(event, question: IQuestionModel) {
    if (event.target.files) {
      let files = event.target.files;
      // console.log(files);
      let totalFileSize = 0;
      question.duplicateFilesDetected = false;
      question.wrongFileExtensions = false;
      let testDuplicate: boolean = false;
      let fileSizeLimit: number = 100;
      if (question.controlType == "mfile") {
        for (let j = 0; j < question.attachedFiles.length; j++) {
          if (question.attachedFiles.length > 1 && question.attachedFiles[question.attachedFiles.length - 1] && event.target.files[event.target.files.length - 1].name == question.attachedFiles[j].fileName) {
            // duplicate file being attached
            // show error msg and return
            testDuplicate = true;
          }
        }
      } else {
        question.attachedFiles = [];
      }
      if (files.length) {
        for (let a = 0; a < files.length; a++) {
          let file = files[a];
          let extension = file.name.split(".")[file.name.split(".").length - 1];
          if (extension.toLowerCase() == "pdf" || extension.toLowerCase() == "doc" || extension.toLowerCase() == "docx" || extension.toLowerCase() == "xls" || extension.toLowerCase() == "xlsx") {
            let reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => {
              let f = {
                base64: (reader.result as String).split(",")[1],
                fileName: file.name,
                fileSize: file.size,
                columnName: question.columnName,
                attachmentId: null,
                fileType: extension.toLowerCase()
              };
              if (testDuplicate) {
                question.errorMsg = file.name + " :File has been already attached!!";
              } else if (Math.round(files[a].size / 1024) >= fileSizeLimit) {
                question.errorMsg = "Can not upload!! size limit exceeds (" + fileSizeLimit + " kb) for " + file.name + " !!";
                question.fileSizeExceeds = true;
              } else {
                question.errorMsg = null;
                question.attachedFiles.push(f as any);
                question.fileSizeExceeds = false;
              }
            };
          } else {
            question.wrongFileExtensions = true;
            //  question.fileSizeExceeds = false;
          }
          // $(event.target).parent().parent().find('input').val("");
          // }
          // else{
          //   question.fileSizeExceeds = true;
          // $(event.target).parent().parent().find('input').val("");
          // }
        }
      } else {
        question.duplicateFilesDetected = true;
        //  $(event.target).parent().parent().find('input').val("");
      }
    } else {
      question.attachedFiles = [];
    }
    // console.log(question);
  }

  /**
   * This method help to delete the list of file that have been selected.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param fIndex
   * @param question
   */
  deleteFile(fIndex, question) {
    question.attachedFiles.splice(fIndex, 1)
    // console.log(question.attachedFiles)
  }

  /**
   * This method is use to check the constraint of textbox or table only.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param fq
   */
  checkConstraints(fq: IQuestionModel) {
    let ccd = this.constraintsArray[fq.columnName as any]
    if (ccd) {
      for (let qq of ccd) {
        if ((qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
          for (let c of qq.constraints.split('@AND')) {
            switch (c.split(":")[0].replace(/\s/g, '')) {
              case 'exp': {
                let exp = c.split(":")[1]
                let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                  // if in some devices data is not clear make sure to increase delay time
                  if (parseInt(rr) == 0) {
                    // if (fq.controlType == 'cell')
                    // this.messageService.showErrorToast(fq.cmsg)
                    setTimeout(() => {
                      // this.questionMap[qq.columnName].value = null;
                      //   this.errorColor1(fq.columnName)
                      fq.showErrMessage = true

                      this.checkRelevance(fq);
                      this.clearFeatureFilters(fq);
                      this.compute(fq);
                      this.validateBeginRepeat(fq.columnName);
                      this.calculateScore(fq)
                    }, 500)
                  } else {

                    //   this.removeColor(fq.columnName)
                    fq.showErrMessage = false
                  }
                }
              }
              break;
            }
          }
        }

      }
    } else if (fq.finalizeMandatory == true && (fq.controlType == 'textbox' || fq.controlType == 'cell') && fq.constraints == null && fq.cmsg != null && (fq.value == null || fq.value == "")) {
      fq.showErrMessage = true
    } else {
      fq.showErrMessage = false
    }

  }

  /**
   * This method is use to remove the error color from the field.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param key
   */
  removeColor(key: any) {
    if (this.tempCaryId != null) {
      let temp = document.getElementById(this.tempCaryId + "")
      if (temp != null && temp != undefined)
        document.getElementById(this.tempCaryId + "").style.removeProperty("border-bottom");
      this.tempCaryId = null;
    }

    if (key != null && key != '' && key != undefined) {
      let temp = document.getElementById(key + "")
      if (temp != null && temp != undefined)
        document.getElementById(key + "").style.removeProperty("border-bottom");
      this.tempCaryId = key;
    }
  };

  // errorColor(sectionHeading: any, key: any) {
  //   this.sectionHeading = sectionHeading;
  //   this.subSections = this.sectionMap.get(sectionHeading);
  //   if (this.tempCaryId != null) this.removeColor(this.tempCaryId);

  //   if (key != null && key != "" && key != undefined) {
  //     setTimeout(() => {
  //       let eleId = document.getElementById(key + "");

  //       if (eleId != null) {
  //         let toscrl = eleId.parentNode.parentElement.offsetTop;
  //         eleId.style.setProperty("outline", "#FF0000 double 1px", "important");
  //         document
  //           .getElementsByClassName("scroll-content")[3]
  //           .scrollTo(0, toscrl);
  //         this.tempCaryId = key;
  //       } else {
  //         document.getElementsByClassName("scroll-content")[3].scrollTop;
  //       }
  //     }, 50);
  //   } else {
  //     document.getElementsByClassName("scroll-content")[3].scrollTop = 0;
  //   }
  // }

  /**
   * This method is used to set the error color to the field.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param sectionHeading
   * @param key
   * @param type
   */
  errorColor(sectionHeading: any, key: any, type ? : any) {
    this.sectionHeading = sectionHeading;
    this.selectedSection = this.sectionMap.get(sectionHeading);
    if (this.tempCaryId != null) this.removeColor(this.tempCaryId);
    if (key != null && key != "" && key != undefined) {
      setTimeout(() => {
        let eleId = document.getElementById(key + "");
        if (eleId != null) {
          let toscrl = eleId.parentNode.parentElement.offsetTop;
          eleId.style.setProperty("border-bottom", "#FF0000 double 1px", "important");
          if (document
            .getElementsByClassName("scroll-content")[3]) {
            document
              .getElementsByClassName("scroll-content")[3]
              .scrollTo(0, toscrl);
          }
          this.tempCaryId = key;
        } else {
          if (type != "table")
            document.getElementsByClassName("scroll-content")[3].scrollTop;
        }
      }, 50);
    } else {
      document.getElementsByClassName("scroll-content")[3].scrollTop = 0;
    }
  }

  /**
   * This method is used to calculate the score of the section,  sub-section as individualy and as a total.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   */
  calculateScore(question: IQuestionModel) {
    if (this.scoreKeyMapper[question.columnName]) {
      for (let impactedScoreHolders of this.scoreKeyMapper[question.columnName]) {
        let result = this.commonsEngineProvider.calculateScore(this.questionMap[impactedScoreHolders], this.questionMap)

        if (this.questionMap[question.columnName] != undefined && this.questionMap[question.columnName].value != null) {
          this.questionMap[impactedScoreHolders].value = result
        } else {
          this.questionMap[impactedScoreHolders].value = null
        }

        let sectionScoreKeeper = this.sectionMapScoreKeeper[this.questionMap[impactedScoreHolders].sectionName]
        let sectionSum = 0;

        if (sectionScoreKeeper != undefined) {
          let checkListSum = this.questionMap[this.checklist_score_keeper_colName].value == null ? 0 : this.questionMap[this.checklist_score_keeper_colName].value

          for (let scoreHolder of this.sectionScoreKeyMapper[sectionScoreKeeper.columnName]) {
            sectionSum = Number(sectionSum) + Number(scoreHolder.value);
          }

          checkListSum = Number(checkListSum) - Number(sectionScoreKeeper.value)
          checkListSum = Number(checkListSum) + Number(sectionSum)
          sectionScoreKeeper.value = Number(sectionSum);
          this.questionMap[this.checklist_score_keeper_colName].value = Number(checkListSum)
        }

        let subSectionScoreKeeper = this.subSectionMapScoreKeeper[this.questionMap[impactedScoreHolders].sectionName + "_" + this.questionMap[impactedScoreHolders].subSectionName]
        let subSectionSum = 0;
        if (subSectionScoreKeeper != undefined) {
          for (let scoreHolder of this.subSectionScoreKeyMapper[subSectionScoreKeeper.columnName]) {
            subSectionSum = Number(subSectionSum) + Number(scoreHolder.value);
          }
          subSectionScoreKeeper.value = Number(subSectionSum);
        }
      }
    }
  }

  /**
   * This method will add the columns to the "questionDependencyArray" having any (relevances with score component) for further use .
   *
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param expression
   * @param question
   */
  drawScoreDependencyGraph(question: IQuestionModel) {
    let scoreExpChars = question.scoreExp.split("");
    for (let i = 0; i < scoreExpChars.length; i++) {
      let ch: string = scoreExpChars[i] as string;
      if (ch == '$') {
        let qName = "";
        for (let j = i + 2; j < scoreExpChars.length; j++) {
          if (scoreExpChars[j] == "}") {
            i = j;
            break;
          }
          qName = qName + (scoreExpChars[j]);
        }
        if (this.scoreKeyMapper[qName] != undefined) {
          let vals = this.scoreKeyMapper[qName]
          let keyFound = false;
          for (let val of vals) {
            if (val == question.columnName) {
              keyFound = true
            }
          }
          if (!keyFound) {
            this.scoreKeyMapper[qName].push(question.columnName)
          }
        } else {
          this.scoreKeyMapper[qName] = []
          this.scoreKeyMapper[qName].push(question.columnName)
        }
      }
    }
  }

  /**
   * This method is used to check the relevances for each question
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  checkRelevanceForEachQuestion() {
    for (let questionKey of this.dataSharingService.getKeys(this.questionMap)) {
      let question = this.questionMap[questionKey];

      switch (question.controlType) {
        case "sub-score-keeper":
        case "score-keeper":
        case "score-holder":
          question.scoreExp != null ? this.drawScoreDependencyGraph(question) : null;
          break;
        case "dropdown":
        case "segment":
          if (question.groupType == "area_group" || question.groupType == "filter_single" || question.groupType == "filter_multiple") {
            if (question.groupQuestions != null) {
              let childLevelQuestion = this.questionMap[question.groupQuestions];
              let optionCount = 0;
              for (let option of childLevelQuestion.options) {
                if (option["parentId"] == question.value) {
                  option["visible"] = true;
                } else {
                  option["visible"] = false;
                  optionCount++;
                }
              }
              if (optionCount == childLevelQuestion.options.length) {
                childLevelQuestion.constraints = "disabled";
              } else {
                childLevelQuestion.constraints = "";
              }
            }
          }
          this.checkRelevance(question)
          break;
        case "table":
        case "tableWithRowWiseArithmetic":
          question.displayComponent = true;
          for (let row = 0; row < question.tableModel.length; row++) {
            for (let column = 0; column < Object.keys(question.tableModel[row]).length; column++) {
              let value = question.tableModel[row][Object.keys(question.tableModel[row])[column]];
              if (typeof value == "object") {
                let cell = value;
                this.checkRelevance(cell)
              }
            }
          }
          break;
        case 'beginrepeat':
          for (let index = 0; index < question.beginRepeat.length; index++) {
            let beginRepeatQuestions: IQuestionModel[] = question.beginRepeat[index];
            for (let beginRepeatQuestion of beginRepeatQuestions) {
              this.checkRelevance(beginRepeatQuestion)
            }
          }
          break;
        default:
          this.checkRelevance(question)
          break;
      }
    }
  }

  /**
   * This method is used to remove the dependency from the "questionDependencyArray".
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param expression
   * @param question
   */
  removeFromDependencyGraph(expression: String, question: IQuestionModel) {
    if (this.questionMap[question.parentColumnName] && this.questionMap[question.parentColumnName].controlType == 'beginrepeat') {
      delete this.questionDependencyArray[question.columnName]
      return
    }
    for (let str of expression.split("}")) {
      let expressions: String[] = str.split(":");
      for (let i = 0; i < expressions.length; i++) {
        let exp: String = expressions[i];
        switch (exp) {
          case "optionEquals":
          case "optionEqualsMultiple": {
            let dColName: any = expressions[i - 1];
            if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
            } else if (question.dependecy == true) {
              let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
              let keyFoundIndex = -1
              for (let i = 0; a.length; i++) {
                if (a[i].columnName == question.columnName) {
                  // remove the object from dependent key. if array size is 1, remove the key itself
                  if (a.length == 1) {
                    delete this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label]
                  } else {
                    keyFoundIndex = i
                    break
                  }
                }

              }
              if (keyFoundIndex > -1) {
                a.splice(keyFoundIndex, 1)
              }

            }
            i = i + 2;
          }
          break;
        case "textEquals":
        case "equals":
        case "greaterThan":
        case "greaterThanEquals":
        case "lessThan":
        case "lessThanEquals": {
          let dColName: any = expressions[i - 1];
          if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
            this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
          } else if (question.dependecy == true) {
            let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
            let keyFound = false
            for (let dps of a) {
              if (dps.columnName == question.columnName)
                keyFound = true
            }
            if (!keyFound) {
              a.push(this.questionMap[question.columnName]);
              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = a;

            }
          }
          i = i + 1;
        }
        break;
        }
      }
    }
  }


  /**
   * This method is for table data calculation.
   *
   * @author Azhar (azaruddin@sdrc.co.in)
   * @param cell
   * @param columnIndex
   * @param rowIndex
   * @param tableModel
   */
  addAnotherWorker(key: Number) {
    let beginRepeatParent: IQuestionModel = this.repeatSubSection.get(key);
    let beginRepeatQuestionList: IQuestionModel[] = beginRepeatParent.beginRepeat[beginRepeatParent.beginRepeat.length - 1];
    let size = beginRepeatParent.beginRepeat.length;
    let clonedQuestion: IQuestionModel[];
    clonedQuestion = JSON.parse(JSON.stringify(beginRepeatQuestionList));

    for (let index = 0; index < clonedQuestion.length; index++) {
      //if dependent question is inside begin repeat section,
      // we have rename dependent column name as we have renamed depending question column name
      let colName = (clonedQuestion[index].columnName as String).split("-")[3];
      let colIndex = (clonedQuestion[index].columnName as String).split("-")[2];


      clonedQuestion[index].value = null;
      clonedQuestion[index].othersValue = false;
      clonedQuestion[index].isOthersSelected = false;
      clonedQuestion[index].dependecy = clonedQuestion[index].relevance != null ? true : false;
      clonedQuestion[index].columnName = beginRepeatParent.columnName + "-" + size + "-" + colIndex + "-" + colName;
      clonedQuestion[index] = this.commonsEngineProvider.renameRelevanceAndFeaturesAndConstraintsAndScoreExpression(clonedQuestion[index], this.questionMap, beginRepeatParent, size);

      //setting up default setting and added to dependency array and feature array
      clonedQuestion[index].displayComponent = clonedQuestion[index].relevance == null ? true : false;
      this.questionMap[clonedQuestion[index].columnName] = clonedQuestion[index];
    }

    for (let index = 0; index < clonedQuestion.length; index++) {
      clonedQuestion[index].relevance != null ? this.drawDependencyGraph(clonedQuestion[index].relevance, clonedQuestion[index]) : null;
      clonedQuestion[index] = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(clonedQuestion[index]);
    }

    this.checkRelevanceForEachQuestion()
    if (beginRepeatParent.limit_bg_repeat) {
      if (this.questionMap[beginRepeatParent.bgDependentColumn as any].value != null) {
        if (beginRepeatParent.beginRepeat.length < this.questionMap[beginRepeatParent.bgDependentColumn as any].value) {
          beginRepeatParent.beginRepeat.push(clonedQuestion);
        } else {
          this.messageService.showErrorToast("Exceed Size");
        }
      } else {
        this.sectionSelected(Object.keys(this.data)[0]);
        this.messageService.showErrorToast("Please enter " + this.questionMap[beginRepeatParent.bgDependentColumn as any].label);
      }
    } else {
      beginRepeatParent.beginRepeat.push(clonedQuestion);
    }
    if (beginRepeatParent.beginRepeat.length > 1) {
      beginRepeatParent.beginRepeatMinusDisable = false;
    }
  }

  /**
   * This method is used to check the finalized constraint.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  checkFinalizedConstraints(): boolean {
    let wasConstraintFailed = false;
    for (let index = 0; index < Object.keys(this.data).length; index++) {
      this.sectionMap.set(Object.keys(this.data)[index], this.data[Object.keys(this.data)[index]]);
      for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
        let subSections = this.data[Object.keys(this.data)[index]][0];
        for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
          for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
            let fq: IQuestionModel = subSections[Object.keys(subSections)[qs]][q];
            if (fq.controlType == 'tableWithRowWiseArithmetic' || fq.controlType == 'table') {
              {
                let tableData = fq.tableModel;
                for (let i = 0; i < tableData.length; i++) {
                  for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                    let cell = tableData[i][Object.keys(tableData[i])[j]];
                    if (typeof cell == "object") {
                      let ccd = this.constraintsArray[cell.columnName as any]
                      if (ccd) {
                        for (let qq of ccd) {
                          if (qq.finalizeMandatory == true && (qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                            // console.log("question" + qq.columnName)
                            for (let c of qq.constraints.split('@AND')) {
                              switch (c.split(":")[0].replace(/\s/g, '')) {
                                case 'exp': {
                                  let exp = c.split(":")[1]

                                  let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                                  if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                                    // if in some devices data is not clear make sure to increase delay time
                                    if (parseInt(rr) == 0) {
                                      setTimeout(() => {
                                        cell.showErrMessage = true
                                      }, 500)
                                    } else {
                                      setTimeout(() => {
                                        cell.showErrMessage = false
                                      }, 500)
                                    }
                                    // console.log(qq, exp, rr)
                                  }
                                }
                                break;
                              }
                            }
                          }
                        }
                        // }
                      } else if (cell.finalizeMandatory == true && (cell.controlType == 'textbox' || cell.controlType == 'cell') && cell.constraints == null && cell.cmsg != null && (cell.value == null || cell.value == "")) {
                        cell.showErrMessage = true
                      } else {
                        cell.showErrMessage = false
                      }
                    }
                  }
                }
              }
            } else if (fq.controlType == 'beginrepeat') {
              for (let bgindex = 0; bgindex < fq.beginRepeat.length; bgindex++) {
                let beginRepeatQuestions: IQuestionModel[] = fq.beginRepeat[bgindex];
                for (let beginRepeatQuestion of beginRepeatQuestions) {
                  let ccd = this.constraintsArray[beginRepeatQuestion.columnName as any]
                  if (ccd) {
                    for (let qq of ccd) {
                      if (qq.finalizeMandatory == true && (qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                        for (let c of qq.constraints.split('@AND')) {
                          switch (c.split(":")[0].replace(/\s/g, '')) {
                            case 'exp': {
                              let exp = c.split(":")[1]
                              let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                              if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                                // if in some devices data is not clear make sure to increase delay time
                                if (parseInt(rr) == 0) {
                                  setTimeout(() => {
                                    beginRepeatQuestion.showErrMessage = true
                                  }, 500)
                                } else {
                                  setTimeout(() => {
                                    beginRepeatQuestion.showErrMessage = false
                                  }, 500)
                                }
                              }
                            }
                            break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            } else {
              let ccd = this.constraintsArray[fq.columnName as any]
              if (ccd) {
                for (let qq of ccd) {
                  if (qq.finalizeMandatory == true && (qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                    for (let c of qq.constraints.split('@AND')) {
                      switch (c.split(":")[0].replace(/\s/g, '')) {
                        case 'exp': {
                          let exp = c.split(":")[1]
                          let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                          if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                            // if in some devices data is not clear make sure to increase delay time
                            if (parseInt(rr) == 0) {
                              setTimeout(() => {
                                fq.showErrMessage = true
                              }, 500)
                            } else {
                              setTimeout(() => {
                                fq.showErrMessage = false
                              }, 500)
                            }
                          }
                        }
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return wasConstraintFailed;
  }

  /**
   * This method is called when user click the back button of app
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  ionViewDidEnter() {
    this.initializeBackButtonCustomHandler();
    this.navBar.backButtonClick = () => {
      if (this.segment) {
        this.initializeNavBackButton();
      } else {
        this.navCtrl.pop()
      }
    };
  }

  /**
   * This method will initialize the hardware backbutton
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @since 1.0.0
   */
  public initializeBackButtonCustomHandler(): void {
    this.unregisterBackButtonAction = this.platform.registerBackButtonAction(() => {
      this.customHandleBackButton();
    }, 10);
  }

  /**
   * This method will show a confirmation popup to exit the app, when user click on the hardware back button
   * in the home page
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @since 1.0.0
   */
  private customHandleBackButton(): void {
    const overlayView = this.app._overlayPortal._views[0];
    if (overlayView && overlayView.dismiss) {
      overlayView.dismiss();
    } else {
      if (this.segment) {
        this.initializeNavBackButton();
      } else {
        this.navCtrl.pop()
      }
    }
  }

  /**
   * Fired when you leave a page, before it stops being the active one
   * Unregister the hardware backbutton
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @since 1.0.0
   */
  ionViewWillLeave() {
    // Unregister the custom back button action for this page
    this.unregisterBackButtonAction && this.unregisterBackButtonAction();
  }

  /**
   * This method is called, when clicks on the hardware back button app device.
   * This method checks the save mandaotry field and gave a alert fro save confirmation the record.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  initializeNavBackButton() {
    let confirm = this.alertCtrl.create({
      enableBackdropDismiss: false,
      cssClass: 'custom-font',
      title: 'Warning',
      message: "Do you want to save this record? ",
      buttons: [{
          text: 'No',
          handler: () => {
            this.navCtrl.pop()
          }
        },
        {
          text: 'Yes',
          handler: () => {
            if (this.isWeb) {
              this.customComponent.onSave('save')
            } else {
              this.onSave('save')
            }
          }
        }
      ]
    });
    confirm.present();
  }

  /**
   * This method is used to check the relevant questions are hidden in this section or not
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param questions
   */
  checkQuestionSizeBasedOnSubsectionRelevance(questions: IQuestionModel[]) {
    for (let q of questions) {
      if (q.displayComponent == true) {
        if (q.defaultSettings && !q.defaultSettings.includes('hidden')) {
          return true
        } else if (!q.defaultSettings) {
          return true
        }
      }
    }
    return false
  }

  /**
   * This method is use to restrict the input alpha field to alpha only.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param question
   * @param event
   */
  _alphabetsKeyPress(question: IQuestionModel, event: any) {
    if (question.type == 'alpha') {
      const pattern = /^[a-zA-Z .,]*$/;
      var a = event.charCode;
      if (a == 0) {
        return;
      }
      let inputChar = String.fromCharCode(event.charCode);
      if (event.target["value"].length >= 200) {
        event.preventDefault();
      }
      if (!pattern.test(inputChar)) {
        event.preventDefault();
      }
    }
  }

}
