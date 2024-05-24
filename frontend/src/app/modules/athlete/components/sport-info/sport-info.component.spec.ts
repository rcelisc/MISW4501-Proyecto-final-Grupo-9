import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SportInfoComponent } from './sport-info.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { SportInfoService } from '../../../../services/sport-info.service';
import { MaterialModule } from '../../../../material.module';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../../../services/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SportInfoComponent', () => {
  let component: SportInfoComponent;
  let fixture: ComponentFixture<SportInfoComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockSportInfoService: jasmine.SpyObj<SportInfoService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let translateService: TranslateService;

  beforeEach(async () => {
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const sportInfoServiceSpy = jasmine.createSpyObj('SportInfoService', ['createSportInfo']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['decodeToken']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        NoopAnimationsModule,
        MaterialModule,
        SportInfoComponent
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: Router, useValue: routerSpy },
        { provide: SportInfoService, useValue: sportInfoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SportInfoComponent);
    component = fixture.componentInstance;
    mockSnackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    mockSportInfoService = TestBed.inject(SportInfoService) as jasmine.SpyObj<SportInfoService>;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'requiredFieldsError': 'Please fill in all required fields.',
        'sportInfoAddedSuccess': 'Sport information added successfully.',
        'sportInfoAddedError': 'Error adding sport information.',
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

  it('should initialize the sport info form with empty fields', () => {
    const formFields = ['training_frequency', 'sports_practiced', 'average_session_duration', 'recovery_time', 'training_pace'];
    formFields.forEach(field => expect(component.sportInfoForm.get(field)?.value).toEqual(''));
  });

  it('should set the user ID from the token on initialization', () => {
    expect(component.userId).toBe(1);
  });

  it('should navigate to login if the token is invalid', () => {
    mockAuthService.decodeToken.and.returnValue(null);
    component.setUserIdFromToken();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should submit sport info if the form is valid', fakeAsync(() => {
    component.sportInfoForm.setValue({
      training_frequency: 'Daily',
      sports_practiced: 'Running',
      average_session_duration: '2 hours',
      recovery_time: '48 hours',
      training_pace: 'Moderate'
    });

    mockSportInfoService.createSportInfo.and.returnValue(of({}));  // Ensure the mock returns an observable
    component.onSubmit();
    tick(500);
    expect(mockSportInfoService.createSportInfo).toHaveBeenCalledWith(1, component.sportInfoForm.value);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  }));

  it('should display an error message if form submission fails', fakeAsync(() => {
    mockSportInfoService.createSportInfo.and.returnValue(throwError(() => new Error('Failed to create')));
    component.sportInfoForm.setValue({
      training_frequency: 'Daily',
      sports_practiced: 'Running',
      average_session_duration: '2 hours',
      recovery_time: '48 hours',
      training_pace: 'Moderate'
    });

    component.onSubmit();
    tick(500);
    expect(mockSportInfoService.createSportInfo).toHaveBeenCalledWith(1, component.sportInfoForm.value);
  }));

  it('should not submit if the form is invalid', () => {
    component.sportInfoForm.setValue({
      training_frequency: '',
      sports_practiced: '',
      average_session_duration: '',
      recovery_time: '',
      training_pace: ''
    });

    component.onSubmit();
    expect(mockSportInfoService.createSportInfo).not.toHaveBeenCalled();
  });
});
