import { TestBed } from '@angular/core/testing';

import { CreateServiceService } from './create-service.service';

describe('ServiceService', () => {
  let service: CreateServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreateServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
