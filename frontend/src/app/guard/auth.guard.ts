import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AppService } from '../app.service';
// import { AppService } from '../app.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  app: any
  constructor( private router: Router, private appService: AppService){
  this.app = appService;
  }
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
      
      if(this.app.checkLoggedIn()){
        return true;
      }else{
        this.router.navigate(['/login'])
        return false;
      }
  }
  // canActivate() :Observable<boolean> | Promise<boolean> | boolean{
  //   return ;
  // }
}