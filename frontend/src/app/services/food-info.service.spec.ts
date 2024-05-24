import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FoodInfoService } from './food-info.service';
import { environment } from '../../environments/environment';

describe('FoodInfoService', () => {
  let service: FoodInfoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FoodInfoService]
    });
    service = TestBed.inject(FoodInfoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create food info', () => {
    const mockUserId = 1;
    const mockFoodData = {
      daily_calories: '2000',
      daily_protein: '150',
      daily_liquid: '3000',
      daily_carbs: '250',
      meal_frequency: '3'
    };
    const mockResponse = { success: true };
    
    localStorage.setItem('token', 'mock-token'); // Set mock token in local storage

    service.createFoodInfo(mockUserId, mockFoodData).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.baseUrl}/users/${mockUserId}/food_data`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(mockResponse);
  });
});
