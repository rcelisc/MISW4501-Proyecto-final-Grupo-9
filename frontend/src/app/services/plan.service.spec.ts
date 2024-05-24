import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PlanService } from './plan.service';

describe('PlanService', () => {
  let service: PlanService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PlanService]
    });
    service = TestBed.inject(PlanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to update plan', () => {
    const userId = 1;
    const planType = 'new_plan_type';
    const mockResponse = { /* mock response data */ };

    service.updatePlan(userId, planType).subscribe(response => {
      expect(response).toEqual(mockResponse); // Check if response matches mock response
    });

    const req = httpMock.expectOne(`https://mysportapp.duckdns.org/users/${userId}/plan`);
    expect(req.request.method).toBe('POST'); // Check if POST request is sent
    expect(req.request.body).toEqual({ plan_type: planType }); // Check if request body matches the expected payload
    req.flush(mockResponse); // Simulate server response
  });
});
