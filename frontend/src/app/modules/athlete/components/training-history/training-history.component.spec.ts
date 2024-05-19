import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TrainingHistoryComponent } from './training-history.component';
import { TrainingPlanService } from '../../../../services/training-plan.service';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { of, throwError } from 'rxjs';

describe('TrainingHistoryComponent', () => {
  let component: TrainingHistoryComponent;
  let fixture: ComponentFixture<TrainingHistoryComponent>;
  let mockTrainingPlanService: jasmine.SpyObj<TrainingPlanService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockTrainingPlanService = jasmine.createSpyObj('TrainingPlanService', ['getTrainingSessions']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, MaterialModule, MatTableModule, TrainingHistoryComponent],
      providers: [
        { provide: TrainingPlanService, useValue: mockTrainingPlanService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingHistoryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load trainings on init', () => {
    const mockTrainings = [{ tipo_entrenamiento: 'Cardio', fecha: '2021-01-01', duracion: '30 mins', notas: 'Intense' }];
    mockTrainingPlanService.getTrainingSessions.and.returnValue(of(mockTrainings));
    fixture.detectChanges(); // ngOnInit is called here
    expect(component.dataSource.data).toEqual(mockTrainings);
    expect(mockTrainingPlanService.getTrainingSessions).toHaveBeenCalled();
  });

  it('should handle errors when loading trainings fails', () => {
    const consoleSpy = spyOn(console, 'error'); // Spy on console.error
    mockTrainingPlanService.getTrainingSessions.and.returnValue(throwError(() => new Error('Failed to load')));
    fixture.detectChanges();
    expect(consoleSpy).toHaveBeenCalledWith('Failed to load trainings', jasmine.any(Error));
  });
});
