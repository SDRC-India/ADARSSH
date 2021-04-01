import { TestBed } from '@angular/core/testing';

import { SessionCheckService } from './session-check.service';

describe('SessionCheckService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SessionCheckService = TestBed.get(SessionCheckService);
    expect(service).toBeTruthy();
  });
});
