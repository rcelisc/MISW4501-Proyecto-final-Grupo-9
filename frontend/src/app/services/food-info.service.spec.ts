import { TestBed } from '@angular/core/testing';

import { FoodInfoService } from './food-info.service';

describe('FoodInfoService', () => {
  let service: FoodInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FoodInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
