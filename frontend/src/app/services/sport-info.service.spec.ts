import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SportInfoService } from './sport-info.service';

describe('SportInfoService', () => {
  let service: SportInfoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SportInfoService]
    });
    service = TestBed.inject(SportInfoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to create sport info', () => {
    const userId = 1;
    const sportData = { /* mock sport data */ };
    const mockResponse = { /* mock response data */ };

    service.createSportInfo(userId, sportData).subscribe(response => {
      expect(response).toEqual(mockResponse); // Check if response matches mock response
    });

    const req = httpMock.expectOne(`http://localhost:3006/users/${userId}/sports_habits`);
    expect(req.request.method).toBe('POST'); // Check if POST request is sent
    expect(req.request.body).toEqual(sportData); // Check if request body matches the expected payload
    req.flush(mockResponse); // Simulate server response
  });
});
