import { TestBed } from '@angular/core/testing';

import { XhrInterceptorService } from './xhr-interceptor.service';

describe('XhrInterceptorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: XhrInterceptorService = TestBed.get(XhrInterceptorService);
    expect(service).toBeTruthy();
  });
});
