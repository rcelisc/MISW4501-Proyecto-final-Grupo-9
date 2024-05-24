import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingPlanComponent } from './training-plan.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { TrainingPlanService } from '../../../../services/training-plan.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('TrainingPlanComponent', () => {
  let component: TrainingPlanComponent;
  let fixture: ComponentFixture<TrainingPlanComponent>;
  let trainingPlanService: jasmine.SpyObj<TrainingPlanService>;
  let snackBar: MatSnackBar;
  let router: Router;

  beforeEach(async () => {
    const trainingPlanServiceSpy = jasmine.createSpyObj('TrainingPlanService', ['createPlan']);
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatSnackBarModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot(),
        TrainingPlanComponent
      ],
      providers: [
        { provide: TrainingPlanService, useValue: trainingPlanServiceSpy },
        FormBuilder,
        TranslateService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingPlanComponent);
    component = fixture.componentInstance;
    trainingPlanService = TestBed.inject(TrainingPlanService) as jasmine.SpyObj<TrainingPlanService>;
    snackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields and required validators', () => {
    const form = component.formulario;
    expect(form.valid).toBeFalsy();
    expect(form.controls['description'].valid).toBeFalsy();
    expect(form.controls['description'].errors?.['required']).toBeTruthy();
    expect(form.controls['exercises'].valid).toBeFalsy();
  });

  it('should submit valid form data and navigate', () => {
    const navigateSpy = spyOn(router, 'navigate');
    const formData = {
      description: 'Test Plan',
      exercises: '10 push-ups',
      duration: '30 minutes',
      frequency: '5 days a week',
      objectives: 'Increase strength',
      profile_type: 'Athlete'
    };
  
    component.formulario.setValue(formData);
    trainingPlanService.createPlan.and.returnValue(of({ id: 1, message: 'Success' }));
  
    component.enviarFormulario();
  
    expect(trainingPlanService.createPlan).toHaveBeenCalledWith(formData);
    expect(navigateSpy).toHaveBeenCalledWith(['/professional-dashboard']);
    expect(component.formulario.pristine).toBeTruthy();
  });
  
  it('should display error message when form submission fails', () => {
    const formData = {
      description: 'Test Plan',
      exercises: '10 push-ups',
      duration: '30 minutes',
      frequency: '5 days a week',
      objectives: 'Increase strength',
      profile_type: 'Athlete'
    };
    component.formulario.setValue(formData);
    trainingPlanService.createPlan.and.returnValue(throwError(() => new Error('Failed')));
  
    component.enviarFormulario();
  
    expect(trainingPlanService.createPlan).toHaveBeenCalledWith(formData);
    expect(component.errorMessage).toBe('trainingPlanSubmitErrorDetail');
  });
});
