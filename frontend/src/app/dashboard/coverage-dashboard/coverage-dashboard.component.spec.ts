import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CoverageDashboardComponent } from './coverage-dashboard.component';

describe('CoverageDashboardComponent', () => {
  let component: CoverageDashboardComponent;
  let fixture: ComponentFixture<CoverageDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CoverageDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CoverageDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
