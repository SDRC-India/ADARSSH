import { TestBed } from '@angular/core/testing';

import { EngineUtilsService } from './engine-utils.service';

describe('EngineUtilsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: EngineUtilsService = TestBed.get(EngineUtilsService);
    expect(service).toBeTruthy();
  });
});
