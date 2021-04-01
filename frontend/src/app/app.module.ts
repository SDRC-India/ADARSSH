import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { NgModule, PipeTransform, Pipe } from "@angular/core";

import { AppComponent } from "./app.component";
import { HeaderComponent } from "./fragments/header/header.component";
import { FooterComponent } from "./fragments/footer/footer.component";
import { LoadingBarHttpClientModule } from "@ngx-loading-bar/http-client";
import { LoadingBarRouterModule } from "@ngx-loading-bar/router";
import { LoadingBarModule } from "@ngx-loading-bar/core";
import { NgxSpinnerModule } from 'ngx-spinner';
import { HomeComponent } from './page/home/home.component';
import { MDBBootstrapModule, DropdownModule } from 'angular-bootstrap-md';
import { MatStepperModule, MatFormFieldModule, MatSelectModule, MatInputModule, MatRadioModule, MatTabsModule, MatCardModule, MatCheckboxModule, MatIcon, MatPaginatorModule, MatSortModule, MatToolbarModule, MatAutocompleteModule, MatIconModule } from "@angular/material";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { NgxMatSelectSearchModule } from "ngx-mat-select-search";
import { LoginComponent } from './login/login.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { XhrInterceptorService } from "./services/xhr-interceptor.service";
import { SessionCheckService } from "./services/session-check.service";
import { UserService } from "./services/user/user.service";
import { AppService } from "./app.service";
import { Exception404Component } from './exception404/exception404.component';
import { BrowserModule, DomSanitizer } from "@angular/platform-browser";
import { AppRoutingModule } from "./app-routing.module";
import { UserManagementModule } from "./user-management/user-management.module";
import { DatePipe, AsyncPipe } from "@angular/common";
import { MessagingService } from "./shared/messaging.service";
import { AngularFireMessagingModule } from "@angular/fire/messaging";
import { AngularFireModule } from "@angular/fire";
import { environment } from "src/environments/environment";
import { AngularFireAuthModule } from "@angular/fire/auth";
import { AngularFireDatabaseModule } from "@angular/fire/database";
import { ToastrModule } from 'ngx-toastr';
import {ToastModule} from 'ng6-toastr/ng2-toastr';
import { CommonsEngineProvider } from "./page/static-page/engine/commons-engine";
import { DataSharingServiceService } from "./page/static-page/engine/data-sharing-service/data-sharing-service.service";
import { EngineUtilsService } from "./page/static-page/engine/engine-utils.service";
import { MessageServiceService } from "./page/static-page/engine/message-service/message-service.service";
import { UnderConstructionComponent } from './under-construction/under-construction.component';

// import { SdrcFormModule } from 'sdrc-form';

@Pipe({
  name: 'safehome'
})
export class SafePipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) { }
  transform(url) {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

}

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    Exception404Component,
    UnderConstructionComponent,
    SafePipe
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    LoadingBarHttpClientModule,
    LoadingBarRouterModule,
    LoadingBarModule,
    BrowserAnimationsModule,
    NgxSpinnerModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatAutocompleteModule,
    MatRadioModule,
    MatTabsModule,
    MatCardModule,
    MatToolbarModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSortModule,
    NgxMatSelectSearchModule,
    UserManagementModule,
    MatIconModule,
    // SdrcFormModule,
    MDBBootstrapModule.forRoot(),
    AngularFireDatabaseModule,
    AngularFireAuthModule,
    AngularFireMessagingModule,
    AngularFireModule.initializeApp(environment.firebase),
    ToastrModule.forRoot(),
    ToastModule.forRoot(),
  ],
   providers: [
    { provide: HTTP_INTERCEPTORS, useClass: XhrInterceptorService, multi: true }, XhrInterceptorService, SessionCheckService, UserService,
    AppService, DatePipe, MessagingService, AsyncPipe,CommonsEngineProvider,DataSharingServiceService,EngineUtilsService,MessageServiceService],
  bootstrap: [AppComponent,]
})
export class AppModule { }
