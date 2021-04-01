import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CmsSideMenuComponent } from './cms-side-menu.component';

describe('CmsSideMenuComponent', () => {
  let component: CmsSideMenuComponent;
  let fixture: ComponentFixture<CmsSideMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CmsSideMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CmsSideMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
