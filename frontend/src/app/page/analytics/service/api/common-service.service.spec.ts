import { TestBed } from '@angular/core/testing';

import { CommonServiceService } from './common-service.service';

describe('CommonServiceService', () => {

  let service:CommonServiceService
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CommonServiceService = TestBed.get(CommonServiceService);
    expect(service).toBeTruthy();
  });

  it('#getObservableValue should return value from observable',
    (done: DoneFn) => {
    service.outlierDataMatrix("D:/R_Input/RMNCH-A_Analysis.csv").subscribe(value => {
      expect(value).toBe("D:");
      done();
    });
  });
});
