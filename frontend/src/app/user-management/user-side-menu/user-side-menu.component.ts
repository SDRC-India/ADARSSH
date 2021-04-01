import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'rmncha-user-side-menu',
  templateUrl: './user-side-menu.component.html',
  styleUrls: ['./user-side-menu.component.scss']
})
export class UserSideMenuComponent implements OnInit {
  router: Router;
  constructor(router:Router) { 
    this.router = router;
  }

  ngOnInit() {
  }

}
