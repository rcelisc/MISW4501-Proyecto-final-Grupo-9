import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfessionalDashboardComponent } from './professional-dashboard.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../../../services/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';

describe('ProfessionalDashboardComponent', () => {
  let component: ProfessionalDashboardComponent;
  let fixture: ComponentFixture<ProfessionalDashboardComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  const mockToken = 'mock-token';

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken', 'getUserById']);

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        ProfessionalDashboardComponent // Import the standalone component here
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        TranslateService
      ]
    })
    .compileComponents();

    // Set a mock token in localStorage
    localStorage.setItem('token', mockToken);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessionalDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    // Clean up localStorage
    localStorage.removeItem('token');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have dashboard items initialized correctly', () => {
    expect(component.dashboardItems.length).toBe(4);
    expect(component.dashboardItems[0].titleKey).toEqual('createServicesTitle');
    expect(component.dashboardItems[1].titleKey).toEqual('viewAndPublishServicesTitle');
    expect(component.dashboardItems[2].titleKey).toEqual('createTrainingPlansTitle');
    expect(component.dashboardItems[3].titleKey).toEqual('createNutritionPlansTitle');
  });

  it('should fetch user data correctly', () => {
    const mockUserData = { user_id: 1, type: 'athlete' };
    const decodedToken = { user_id: 1 };

    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.getUserById.and.returnValue(of(mockUserData));

    component.fetchUserData();
    fixture.detectChanges();

    expect(mockAuthService.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(mockAuthService.getUserById).toHaveBeenCalledWith(1);
    expect(component.userData).toEqual(mockUserData);
    expect(component.userData.translatedRole).toBe('userTypeAthlete');
  });

  it('should handle invalid token', () => {
    mockAuthService.decodeToken.and.returnValue(null);

    spyOn(console, 'error');
    component.fetchUserData();
    fixture.detectChanges();
    expect(console.error).toHaveBeenCalledWith('Token is invalid or expired');
  });

  it('should handle fetch user data error', () => {
    const mockError = new Error('Failed to fetch');
    const decodedToken = { user_id: 1 };

    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.getUserById.and.returnValue(throwError(mockError));

    spyOn(console, 'error');
    component.fetchUserData();
    fixture.detectChanges();

    expect(mockAuthService.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(mockAuthService.getUserById).toHaveBeenCalledWith(1);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch user data:', mockError);
  });
});
