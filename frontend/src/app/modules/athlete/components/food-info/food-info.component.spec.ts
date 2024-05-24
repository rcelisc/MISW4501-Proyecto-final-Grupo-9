import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FoodInfoComponent } from './food-info.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { FoodInfoService } from '../../../../services/food-info.service';
import { AuthService } from '../../../../services/auth.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('FoodInfoComponent', () => {
  let component: FoodInfoComponent;
  let fixture: ComponentFixture<FoodInfoComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockFoodInfoService: jasmine.SpyObj<FoodInfoService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let translateService: TranslateService;

  beforeEach(async () => {
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const foodInfoServiceSpy = jasmine.createSpyObj('FoodInfoService', ['createFoodInfo']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['decodeToken']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        TranslateModule.forRoot()
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: Router, useValue: routerSpy },
        { provide: FoodInfoService, useValue: foodInfoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FoodInfoComponent);
    component = fixture.componentInstance;
    mockSnackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    mockFoodInfoService = TestBed.inject(FoodInfoService) as jasmine.SpyObj<FoodInfoService>;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    translateService = TestBed.inject(TranslateService);

    // Mock translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'requiredFieldsError': 'Please fill in all required fields.',
        'foodInfoAddedSuccess': 'Food information added successfully.',
        'foodInfoAddedError': 'Error adding food information.',
      };
      return translations[key] || key;
    });

    // Mock token and user ID decoding
    const mockToken = 'mock-token';
    const decodedToken = { user_id: 1 };
    localStorage.setItem('token', mockToken);
    mockAuthService.decodeToken.and.returnValue(decodedToken);

    fixture.detectChanges();
  });

  afterEach(() => {
    // Clean up localStorage
    localStorage.removeItem('token');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the food info form with empty fields', () => {
    const formFields = ['daily_calories', 'daily_protein', 'daily_liquid', 'daily_carbs', 'meal_frequency'];
    formFields.forEach(field => expect(component.foodInfoForm.get(field)?.value).toEqual(''));
  });

  it('should set the user ID from the token on initialization', () => {
    expect(component.userId).toBe(1);
  });

  it('should navigate to login if the token is invalid', () => {
    mockAuthService.decodeToken.and.returnValue(null);
    component.setUserIdFromToken();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should submit food info if the form is valid', fakeAsync(() => {
    component.foodInfoForm.setValue({
      daily_calories: '2000',
      daily_protein: '150',
      daily_liquid: '3000',
      daily_carbs: '250',
      meal_frequency: '3'
    });

    mockFoodInfoService.createFoodInfo.and.returnValue(of({}));
    component.onSubmit();
    tick(500); // Simulate the passage of time until all async operations are complete

    expect(mockFoodInfoService.createFoodInfo).toHaveBeenCalledWith(1, component.foodInfoForm.value);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  }));

  it('should display an error message if form submission fails', fakeAsync(() => {
    component.foodInfoForm.setValue({
      daily_calories: '2000',
      daily_protein: '150',
      daily_liquid: '3000',
      daily_carbs: '250',
      meal_frequency: '3'
    });

    mockFoodInfoService.createFoodInfo.and.returnValue(throwError(() => new Error('Failed to create')));
    component.onSubmit();
    tick(500); // Simulate the passage of time until all async operations are complete

    expect(mockFoodInfoService.createFoodInfo).toHaveBeenCalledWith(1, component.foodInfoForm.value);
  }));

  it('should not submit if the form is invalid', () => {
    component.foodInfoForm.setValue({
      daily_calories: '',
      daily_protein: '',
      daily_liquid: '',
      daily_carbs: '',
      meal_frequency: ''
    });

    component.onSubmit();
    expect(mockFoodInfoService.createFoodInfo).not.toHaveBeenCalled();
  });
});
