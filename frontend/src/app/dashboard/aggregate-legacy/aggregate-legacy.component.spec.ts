import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AggregateLegacyComponent } from './aggregate-legacy.component';

describe('AggregateLegacyComponent', () => {
  let component: AggregateLegacyComponent;
  let fixture: ComponentFixture<AggregateLegacyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AggregateLegacyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AggregateLegacyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
