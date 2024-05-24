import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DemographicInfoComponent } from './demographic-info.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { DemographicInfoService } from '../../../../services/demographic-info.service';
import { MaterialModule } from '../../../../material.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AuthService } from '../../../../services/auth.service';

describe('DemographicInfoComponent', () => {
  let component: DemographicInfoComponent;
  let fixture: ComponentFixture<DemographicInfoComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockDemographicInfoService: jasmine.SpyObj<DemographicInfoService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let translateService: TranslateService;

  beforeEach(async () => {
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const demographicInfoServiceSpy = jasmine.createSpyObj('DemographicInfoService', ['createDemographicInfo']);
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
        DemographicInfoComponent
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: Router, useValue: routerSpy },
        { provide: DemographicInfoService, useValue: demographicInfoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DemographicInfoComponent);
    component = fixture.componentInstance;
    mockSnackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    mockDemographicInfoService = TestBed.inject(DemographicInfoService) as jasmine.SpyObj<DemographicInfoService>;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'requiredFieldsError': 'Please fill in all required fields.',
        'demographicInfoAddedSuccess': 'Demographic information added successfully.',
        'demographicInfoAddedError': 'Error adding demographic information.',
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

  it('should initialize the form with empty fields', () => {
    const form = component.demographicInfoForm;
    const formFields = ['ethnicity', 'heart_rate', 'vo2_max', 'blood_pressure', 'respiratory_rate'];
    formFields.forEach(field => expect(form.get(field)?.value).toEqual(''));
  });

  it('should set the user ID from the token on initialization', () => {
    expect(component.userId).toBe(1);
  });

  it('should navigate to login if the token is invalid', () => {
    mockAuthService.decodeToken.and.returnValue(null);
    component.setUserIdFromToken();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should submit demographic info if the form is valid', () => {
    component.demographicInfoForm.setValue({
      ethnicity: 'Latino',
      heart_rate: 75,
      vo2_max: 45,
      blood_pressure: '120/80',
      respiratory_rate: 16
    });

    mockDemographicInfoService.createDemographicInfo.and.returnValue(of({}));

    component.onSubmit();
    expect(mockDemographicInfoService.createDemographicInfo).toHaveBeenCalledWith(1, component.demographicInfoForm.value);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  });

  it('should display an error message if form submission fails', () => {
    component.demographicInfoForm.setValue({
      ethnicity: 'Latino',
      heart_rate: 75,
      vo2_max: 45,
      blood_pressure: '120/80',
      respiratory_rate: 16
    });

    mockDemographicInfoService.createDemographicInfo.and.returnValue(throwError(() => new Error('Failed to create')));

    component.onSubmit();
    expect(mockDemographicInfoService.createDemographicInfo).toHaveBeenCalled();
  });

  it('should not submit if the form is invalid', () => {
    component.demographicInfoForm.setValue({
      ethnicity: '',
      heart_rate: '',
      vo2_max: '',
      blood_pressure: '',
      respiratory_rate: ''
    });

    component.onSubmit();
    expect(mockDemographicInfoService.createDemographicInfo).not.toHaveBeenCalled();
  });
});
