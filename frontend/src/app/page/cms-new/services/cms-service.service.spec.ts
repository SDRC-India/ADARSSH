import { TestBed } from '@angular/core/testing';

import { CmsServiceService } from './cms-service.service';

describe('CmsServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CmsServiceService = TestBed.get(CmsServiceService);
    expect(service).toBeTruthy();
  });
});
