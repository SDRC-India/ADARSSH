import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TemplateDownloadUploadComponent } from './template-download-upload.component';

describe('TemplateDownloadUploadComponent', () => {
  let component: TemplateDownloadUploadComponent;
  let fixture: ComponentFixture<TemplateDownloadUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TemplateDownloadUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TemplateDownloadUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
