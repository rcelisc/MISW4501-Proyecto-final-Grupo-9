import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TrainingHistoryComponent } from './training-history.component';
import { TrainingPlanService } from '../../../../services/training-plan.service';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';

describe('TrainingHistoryComponent', () => {
  let component: TrainingHistoryComponent;
  let fixture: ComponentFixture<TrainingHistoryComponent>;
  let mockTrainingPlanService: jasmine.SpyObj<TrainingPlanService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockTrainingPlanService = jasmine.createSpyObj('TrainingPlanService', ['getTrainingSessionByUser']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, MaterialModule, TranslateModule.forRoot(), TrainingHistoryComponent],
      providers: [
        { provide: TrainingPlanService, useValue: mockTrainingPlanService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingHistoryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set user ID from token and load trainings on init', () => {
    const mockUserId = 1;
    const mockToken = 'mock-token';
    const mockTrainings = [{ training_type: 'Cardio', end_time: '2021-01-01T00:00:00Z', duration: '30 mins', notes: 'Intense' }];
    
    spyOn(localStorage, 'getItem').and.returnValue(mockToken);
    mockAuthService.decodeToken.and.returnValue({ user_id: mockUserId });
    mockTrainingPlanService.getTrainingSessionByUser.and.returnValue(of(mockTrainings));

    fixture.detectChanges(); // ngOnInit is called here

    expect(mockAuthService.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(component.userId).toBe(mockUserId);
    expect(component.dataSource.data).toEqual(mockTrainings);
    expect(mockTrainingPlanService.getTrainingSessionByUser).toHaveBeenCalledWith(mockUserId);
  });

  it('should handle invalid token', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);

    fixture.detectChanges(); // ngOnInit is called here

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle error when loading trainings fails', () => {
    const consoleSpy = spyOn(console, 'error'); // Spy on console.error
    const mockUserId = 1;
    const mockToken = 'mock-token';

    spyOn(localStorage, 'getItem').and.returnValue(mockToken);
    mockAuthService.decodeToken.and.returnValue({ user_id: mockUserId });
    mockTrainingPlanService.getTrainingSessionByUser.and.returnValue(throwError(() => new Error('Failed to load')));

    fixture.detectChanges(); // ngOnInit is called here

    expect(consoleSpy).toHaveBeenCalledWith('Error loading trainings', jasmine.any(Error));
  });

  it('should navigate to athlete-dashboard on goBack', () => {
    component.goBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  });
});
