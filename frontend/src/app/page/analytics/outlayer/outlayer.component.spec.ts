import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OutlayerComponent } from './outlayer.component';

describe('OutlayerComponent', () => {
  let component: OutlayerComponent;
  let fixture: ComponentFixture<OutlayerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OutlayerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OutlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
