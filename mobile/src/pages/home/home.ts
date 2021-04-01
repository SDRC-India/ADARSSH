import {
  Component, HostListener
} from '@angular/core';
import {
  IonicPage,
  NavController,
  NavParams,
  Events,
  Platform,
  AlertController,
  Nav
} from 'ionic-angular';
import {
  QuestionServiceProvider
} from '../../providers/question-service/question-service';
import {
  MessageServiceProvider
} from '../../providers/message-service/message-service';
import {
  ConstantProvider
} from '../../providers/constant/constant';
import { DatePipe } from '@angular/common';
import { FormProvider } from '../../providers/registered-forms-provider/form.provider';

/**
 * Generated class for the HomePage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-home',
  templateUrl: 'home.html',
})
export class HomePage {
  @HostListener('window:popstate', ['$event'])
  onbeforeunload(event) {
    if (window.location.href.substr(window.location.href.length - 5) == 'login') {
      history.pushState(null, null, "" + window.location.href);
    }
  }

  formList: any
  homePageModelArr: IHomePageModel[] = [];
  public unregisterBackButtonAction: any;
  searchTerm:string;
  currentMonth: Number;
  previousMonth: Number;
  deadlineDateModel: any;
  dateLineDate: string;
  syncModal: boolean = true;

  constructor(public navCtrl: NavController, public navParams: NavParams, public questionService: QuestionServiceProvider,
    public messageProvider: MessageServiceProvider, public events: Events, private platform:Platform,
    private alertCtrl: AlertController, private nav: Nav,private datePipe: DatePipe,private formProvider: FormProvider) {}

  /**
   * This method call up the initial load. subscribe the syncStatus to refresh the page
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  ngOnInit() {
    this.events.subscribe('syncStatus', data=>{
      if(data){
        this.syncModal = false
        this.loadForms()
      }
    })
  }

  ionViewDidEnter(){
    if(this.syncModal)
    this.messageProvider.showLoader(ConstantProvider.message.pleaseWait);
    this.initializeBackButtonCustomHandler();
    this.loadForms()
  }

  /**
   * This method will fetch all the user specific froms
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   */
  async fetchData() {
    await this.questionService.getAllFormsId().then(async (formIds) => {
      this.formList = await formIds;
    });
  }

  /**
   * This method is called after fetchData to show the count of different submission of each form.
   *
   * @author Azhar (azaruddin@sdrc.co.in)
   */
  async loadForms(){
    let dbData = await this.questionService.getAllFilledFormsAgainstLoggedInUser()
    await this.fetchData()
    await  this.formProvider.getDeadlineDateInfoAll().then(deadlineData=>{
      this.deadlineDateModel = deadlineData
    })
    // console.log("this.deadlineDateModel",this.deadlineDateModel)
    for (let i = 0; i < this.formList.length; i++) {

      let save: number = 0;
      let finalize: number = 0;
      let sent: number = 0;
      let reject: number = 0;
      let pendingForSync: number = 0;
      let form: IHomePageModel = {
        formKeyName: this.formList[i],
        formName: this.formList[i].split("_")[1],
        formId: this.formList[i].split("_")[0],
        saveCount: save,
        rejectCount: reject,
        finalizeCount: finalize,
        sentCount: sent,
        pendingForSyncCount: pendingForSync
      }
      if(dbData != null && dbData[form.formKeyName] != undefined){
        for (let i = 0; i < Object.keys(dbData[form.formKeyName]).length; i++) {
          if(dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formStatus == "save"){
            save++
          }else if(dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formStatus == "finalized"){
            finalize++

            this.deadlineDateModel[dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formSubmissionId]
            ////////////////////
            let a =this.datePipe.transform(new Date(), 'dd-MM-yyyy');
            let b =this.datePipe.transform(new Date(), 'MM-yyyy')
            this.dateLineDate = this.deadlineDateModel[dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formSubmissionId].deadlineDays+"-"+b
            if(Number(a.split("-")[0]) > Number(this.deadlineDateModel[dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formSubmissionId].deadlineDays)){
              this.dateLineDate = this.addMonth(new Date(Number(this.dateLineDate.split("-")[2]), Number(this.dateLineDate.split("-")[1]), Number(this.dateLineDate.split("-")[0])))
            }else{
              this.dateLineDate = this.dateLineDate
            }
            let revisedDate: string = this.deductDate(new Date(Number(this.dateLineDate.split("-")[2]), Number(this.dateLineDate.split("-")[1])-1, Number(this.dateLineDate.split("-")[0])+1), Number(this.deadlineDateModel[dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formSubmissionId].notifyByDays))
            let currentDate = new Date(Number(a.split("-")[2]),Number(a.split("-")[1])-1,Number(a.split("-")[0]))
            let startDate = new Date(Number(revisedDate.split("-")[2]),Number(revisedDate.split("-")[1])-1,Number(revisedDate.split("-")[0]))
            let endingDate = new Date(Number(this.dateLineDate.split("-")[2]),Number(this.dateLineDate.split("-")[1])-1,Number(this.dateLineDate.split("-")[0]))

            let visistedDate = dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].visitedDate
            if(currentDate >= startDate && currentDate <= endingDate){
              if(Number(this.dateLineDate.split("-")[1]) == 1){
                if((Number(visistedDate.split("-")[1]) == 12) && (Number(visistedDate.split("-")[2]) == Number(this.dateLineDate.split("-")[2])-1)){
                  pendingForSync++
                }
              }else{
                if((Number(visistedDate.split("-")[1]) == Number(this.dateLineDate.split("-")[1])-1) && (Number(visistedDate.split("-")[2]) == Number(this.dateLineDate.split("-")[2]))){
                  pendingForSync++
                }
              }
            }else{
              pendingForSync = 0
            }
            ////////////////////
          }else if(dbData[form.formKeyName][Object.keys(dbData[form.formKeyName])[i]].formStatus == "sent"){
            sent++
          }else{
            reject++
          }

        }
      }

       form['saveCount'] = save
       form['rejectCount'] = reject
       form['finalizeCount'] = finalize
       form['sentCount'] = sent
       form['pendingForSyncCount'] = pendingForSync
       this.homePageModelArr[i] = form
     }
     if(this.syncModal)
     this.messageProvider.stopLoader();
  }

  /**
   * This method is called when user click on specific from. This method take the specific from data to next page.
   *
   * @author Jagat Bandhu (jagat@sdrc.co.in)
   * @param formId
   */

  form(formId: any,segment: any) {
    this.questionService.getQuestionBank(formId,null, ConstantProvider.lastUpdatedDate).then(data => {
        if (data) {
          this.navCtrl.push('FormListPage', {
            formId: formId,
            segment: segment
          })
        }
      })
      .catch((error) => {
        if (error.status == 500) {
          this.messageProvider.showErrorToast(ConstantProvider.message.networkError)
        }
      })
  }


    /**
   * This method will called, when user clicks on the add new button to fill a data in the new form
   *
   * @author Harsh Pratyush (Harsh@sdrc.co.in)
   */
  openNewBlankForm(formId: any,segment: any,xyz) {

    console.log("hi");
    
     this.navCtrl.push("MobileFormComponent", {
      formId: formId,
      formTitle: formId.split("_")[1].replace('_',' '),
      isNew: true,
      segment: segment
    });

    console.log("there");
    
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
    let confirm = this.alertCtrl.create({
      enableBackdropDismiss: false,
      cssClass: 'custom-font',
      title: 'Warning',
      message: "Are you sure you want to logout",
      buttons: [{
          text: 'No',
          handler: () => {}
        },
        {
          text: 'Yes',
          handler: () => {
            this.messageProvider.showSuccessToast("Logout Successfully.")
            this.nav.setRoot('LoginPage')
          }
        }
      ]
    });
    confirm.present();
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

  deductDate(date: Date, notifyDay: number): string{

    let newDate = new Date(date)
    newDate.setDate(newDate.getDate() - (notifyDay+1))
    let revisedDate = new Date(newDate)
    return this.datePipe.transform(revisedDate, "dd-MM-yyyy")

  }

  addMonth(dates: Date): string{
    let newDates = new Date(dates)
    return this.datePipe.transform(newDates, "dd-MM-yyyy")
  }

}
