import { TestBed } from '@angular/core/testing';

import { FormServicService } from './form-servic.service';

describe('FormServicService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FormServicService = TestBed.get(FormServicService);
    expect(service).toBeTruthy();
  });
});
