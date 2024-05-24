import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../material.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let translateService: TranslateService;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['registerUser']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        NoopAnimationsModule,
        MaterialModule,
        RegisterComponent
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'registerError': 'Error during registration.',
        'registerRequiredFieldsError': 'Please fill in all required fields.'
      };
      return translations[key] || key;
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.registerForm.valid).toBeFalsy();
  });

  it('form should be valid when required fields are filled', () => {
    component.registerForm.setValue({
      name: 'John',
      surname: 'Doe',
      id_type: 'Passport',
      id_number: '123456789',
      password: 'password',
      city_of_living: '',
      country_of_living: '',
      type: 'athlete',
      age: '30',
      gender: 'Male',
      weight: '80',
      height: '180',
      city_of_birth: 'City',
      country_of_birth: 'Country',
      sports: 'Running',
      profile_type: 'Professional'
    });
    expect(component.registerForm.valid).toBeTruthy();
  });

  it('should navigate to correct dashboard after successful registration', fakeAsync(() => {
    component.registerForm.setValue({
      name: 'John',
      surname: 'Doe',
      id_type: 'Passport',
      id_number: '123456789',
      password: 'password',
      city_of_living: '',
      country_of_living: '',
      type: 'athlete',
      age: '30',
      gender: 'Male',
      weight: '80',
      height: '180',
      city_of_birth: 'City',
      country_of_birth: 'Country',
      sports: 'Running',
      profile_type: 'Professional'
    });
    mockAuthService.registerUser.and.returnValue(of({ userId: 1 }));
    component.onRegister();
    tick();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('should log error if registration fails', () => {
    component.registerForm.setValue({
      name: 'Jane',
      surname: 'Smith',
      id_type: 'Passport',
      id_number: '987654321',
      password: 'password',
      city_of_living: '',
      country_of_living: '',
      type: 'athlete',
      age: '25',
      gender: 'Female',
      weight: '70',
      height: '165',
      city_of_birth: 'City',
      country_of_birth: 'Country',
      sports: 'Running',
      profile_type: 'Beginner'
    });

    const consoleSpy = spyOn(console, 'error');
    mockAuthService.registerUser.and.returnValue(throwError(() => new Error('Registration error')));
    component.onRegister();
    expect(consoleSpy).toHaveBeenCalledWith('Registration error:', jasmine.any(Error));
  });
});
