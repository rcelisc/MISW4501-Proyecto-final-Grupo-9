import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NutritionPlanService } from './nutrition-plan.service';

describe('NutritionPlanService', () => {
  let service: NutritionPlanService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NutritionPlanService]
    });
    service = TestBed.inject(NutritionPlanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verifies that no requests are outstanding after each test
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to create a meal plan', () => {
    const mockMealPlanData = {
      title: 'Sample Meal Plan',
      meals: [
        { name: 'Breakfast', calories: 400 },
        { name: 'Lunch', calories: 600 },
        { name: 'Dinner', calories: 800 }
      ]
    };

    const mockResponse = {
      id: '123456789',
      message: 'Meal plan created successfully'
    };

    service.createMealPlan(mockMealPlanData).subscribe(response => {
      expect(response).toEqual(mockResponse); // Check if response matches mock response
    });

    const req = httpMock.expectOne(service['baseUrl']);
    expect(req.request.method).toBe('POST'); // Check if POST request is sent
    req.flush(mockResponse); // Simulate server response
  });
});
