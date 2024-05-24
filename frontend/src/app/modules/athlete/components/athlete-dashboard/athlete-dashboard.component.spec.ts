import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AthleteDashboardComponent } from './athlete-dashboard.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../../../services/auth.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';

describe('AthleteDashboardComponent', () => {
  let component: AthleteDashboardComponent;
  let fixture: ComponentFixture<AthleteDashboardComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let translateService: TranslateService;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken', 'getUserById']);

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MaterialModule,
        CommonModule,
        AthleteDashboardComponent // Import the standalone component here
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        TranslateService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AthleteDashboardComponent);
    component = fixture.componentInstance;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'viewCalendar': 'View Calendar',
        'viewEventsServices': 'View Events and Services',
        'viewTrainingHistory': 'View Training History',
        'selectPlan': 'Select Plan',
        'addDemographicInfo': 'Add Demographic Information',
        'addFoodInfo': 'Add Food Information',
        'addSportInfo': 'Add Sport Information',
        'welcomeMessage': 'Welcome, {name}',
        'roleLabel': 'Role: {role}',
        'athleteDashboardTitle': 'Athlete Dashboard',
        'goButton': 'Go'
      };
      return translations[key] || key;
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have dashboard items defined', () => {
    expect(component.dashboardItems.length).toBeGreaterThan(0);
  });

  it('should display titles of dashboard items in the template', () => {
    fixture.detectChanges(); // ensure the template is updated with translated texts
    const compiled = fixture.nativeElement as HTMLElement;
    const dashboardItemTitles = component.dashboardItems.map(item => translateService.instant(item.titleKey));

    dashboardItemTitles.forEach(title => {
      const element = Array.from(compiled.querySelectorAll('mat-card-title'))
                           .find(el => el.textContent?.trim() === title);
      expect(element).not.toBeNull(`Expected element with title '${title}' to be found in the template`);
    });
  });

  it('should fetch user data on initialization', () => {
    const mockToken = 'mock-token';
    const decodedToken = { user_id: 1 };
    const mockUserData = { name: 'John Doe', type: 'athlete' };

    localStorage.setItem('token', mockToken);
    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.getUserById.and.returnValue(of(mockUserData));

    component.fetchUserData();
    fixture.detectChanges();

    expect(mockAuthService.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(mockAuthService.getUserById).toHaveBeenCalledWith(1);
    expect(component.userData).toEqual(mockUserData);
    expect(component.userData.translatedRole).toBe('userTypeAthlete');

    localStorage.removeItem('token'); // Cleanup
  });

  it('should handle error when fetching user data fails', () => {
    const mockToken = 'mock-token';
    const decodedToken = { user_id: 1 };
    const mockError = new Error('Failed to fetch user data');

    localStorage.setItem('token', mockToken);
    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.getUserById.and.returnValue(throwError(() => mockError));

    spyOn(console, 'error');

    component.fetchUserData();
    fixture.detectChanges();

    expect(mockAuthService.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(mockAuthService.getUserById).toHaveBeenCalledWith(1);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch user data:', mockError);

    localStorage.removeItem('token'); // Cleanup
  });
});
