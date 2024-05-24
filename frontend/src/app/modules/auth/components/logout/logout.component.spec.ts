import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LogoutComponent } from './logout.component';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';

describe('LogoutComponent', () => {
  let component: LogoutComponent;
  let fixture: ComponentFixture<LogoutComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken', 'logoutUser']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, LogoutComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LogoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should log an error if no token is found', () => {
    const consoleSpy = spyOn(console, 'error');
    localStorage.removeItem('token');
    component.logout();
    expect(consoleSpy).toHaveBeenCalledWith('No token found');
  });

  it('should logout successfully and navigate to login', () => {
    const decodedToken = { user_id: 1 };
    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.logoutUser.and.returnValue(of({}));

    localStorage.setItem('token', 'mock-token');

    component.logout();

    expect(mockAuthService.logoutUser).toHaveBeenCalledWith(1);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should handle logout error', () => {
    const consoleSpy = spyOn(console, 'error');
    const decodedToken = { user_id: 1 };
    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.logoutUser.and.returnValue(throwError(() => new Error('Logout failed')));

    localStorage.setItem('token', 'mock-token');

    component.logout();

    expect(mockAuthService.logoutUser).toHaveBeenCalledWith(1);
    expect(consoleSpy).toHaveBeenCalledWith('Logout failed', jasmine.any(Error));
  });
});
