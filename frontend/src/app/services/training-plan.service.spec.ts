import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TrainingPlanService } from './training-plan.service';
import { TrainingPlanRequest, TrainingPlanResponse } from '../models/plan-training.model';

describe('TrainingPlanService', () => {
  let service: TrainingPlanService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TrainingPlanService]
    });
    service = TestBed.inject(TrainingPlanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to create a training plan', () => {
    const mockPlan = new TrainingPlanRequest(
      'Increase Stamina',
      'This plan focuses on building stamina through interval training.',
      'breast, legs',
      '3 times a week',
      '6 weeks',
      'Beginner'
    );
    
    
    const mockResponse = new TrainingPlanResponse(123456789);

    service.createPlan(mockPlan).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(service['baseCommandsUrl']);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should send a GET request to fetch training sessions', () => {
    const mockResponse = [{ /* mock session data */ }];

    service.getTrainingSessions().subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${service['baseQueriesUrl']}/training-sessions`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
