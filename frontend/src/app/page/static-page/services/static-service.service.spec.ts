import { TestBed } from '@angular/core/testing';

import { StaticServiceService } from './static-service.service';

describe('StaticServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: StaticServiceService = TestBed.get(StaticServiceService);
    expect(service).toBeTruthy();
  });
});
