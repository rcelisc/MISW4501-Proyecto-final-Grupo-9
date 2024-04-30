import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DemographicInfoService } from './demographic-info.service';

describe('DemographicInfoService', () => {
  let service: DemographicInfoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DemographicInfoService]
    });
    service = TestBed.inject(DemographicInfoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to create demographic info', () => {
    const userId = 1;
    const demographicData = { /* mock demographic data */ };
    const mockResponse = { /* mock response data */ };

    service.createDemographicInfo(userId, demographicData).subscribe(response => {
      expect(response).toEqual(mockResponse); // Check if response matches mock response
    });

    const req = httpMock.expectOne(`http://localhost:3006/users/${userId}/demographic_data`);
    expect(req.request.method).toBe('POST'); // Check if POST request is sent
    req.flush(mockResponse); // Simulate server response
  });
});
