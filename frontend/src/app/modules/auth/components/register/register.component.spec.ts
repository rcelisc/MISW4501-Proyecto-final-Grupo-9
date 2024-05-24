import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['registerUser']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, MaterialModule, ReactiveFormsModule, RegisterComponent, NoopAnimationsModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
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
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  }));

  it('should log error if registration fails', () => {
    component.registerForm.setValue({
        name: 'Jane',
        surname: 'Smith',
        id_type: 'Passport',
        id_number: '987654321',
        city_of_living: '',
        country_of_living: '',
        type: 'athlete',
        age: '25', // Required for athletes
        gender: 'Female', // Also likely required for athletes
        weight: '70', // Consider this if needed
        height: '165', // Consider this if needed
        city_of_birth: '', // Fill if required
        country_of_birth: '', // Fill if required
        sports: '', // Fill if required
        profile_type: '' // Fill if required
    });

    const consoleSpy = spyOn(console, 'error');
    mockAuthService.registerUser.and.returnValue(throwError(() => new Error('Registration error')));
    component.onRegister();
    expect(consoleSpy).toHaveBeenCalledWith('Registration error:', jasmine.any(Error));
});
});
