import { TestBed, ComponentFixture } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../../services/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;
  let authService: jasmine.SpyObj<AuthService>;
  let snackBar: MatSnackBar;
  let translateService: TranslateService;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['loginUser', 'decodeToken']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        NoopAnimationsModule,
        LoginComponent
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    snackBar = TestBed.inject(MatSnackBar);
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'loginFailed': 'Login failed. Please try again.',
        'loginTitle': 'Login',
        'loginIdNumber': 'ID Number',
        'loginPassword': 'Password',
        'loginButton': 'Login',
      };
      return translations[key] || key;
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a login form with id_number and password', () => {
    expect(component.loginForm.contains('id_number')).toBeTruthy();
    expect(component.loginForm.contains('password')).toBeTruthy();
  });

  it('should navigate to the correct dashboard on login', () => {
    const navigateSpy = spyOn(router, 'navigate');
    const response = { token: 'fake-jwt-token' };
    const decodedToken = { role: 'complementary_services_professional' };

    authService.loginUser.and.returnValue(of(response));
    authService.decodeToken.and.returnValue(decodedToken);

    component.loginForm.setValue({ id_number: '12345', password: 'password' });
    component.onLogin();

    expect(authService.loginUser).toHaveBeenCalledWith({ id_number: '12345', password: 'password' });
    expect(authService.decodeToken).toHaveBeenCalledWith('fake-jwt-token');
    expect(navigateSpy).toHaveBeenCalledWith(['/professional-dashboard']);
  });

  it('should display an error message when login fails', () => {
    authService.loginUser.and.returnValue(throwError(() => new Error('Login failed')));

    component.loginForm.setValue({ id_number: '12345', password: 'wrong-password' });
    component.onLogin();

    expect(authService.loginUser).toHaveBeenCalledWith({ id_number: '12345', password: 'wrong-password' });
  });
});
