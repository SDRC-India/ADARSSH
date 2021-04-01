import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegrationComponent } from './regration.component';

describe('RegrationComponent', () => {
  let component: RegrationComponent;
  let fixture: ComponentFixture<RegrationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RegrationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
