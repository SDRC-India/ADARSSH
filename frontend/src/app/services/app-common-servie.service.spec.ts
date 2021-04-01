import { TestBed } from '@angular/core/testing';

import { AppCommonServieService } from './app-common-servie.service';

describe('AppCommonServieService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AppCommonServieService = TestBed.get(AppCommonServieService);
    expect(service).toBeTruthy();
  });
});
