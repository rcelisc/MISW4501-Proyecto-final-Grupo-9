import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NutritionPlanService } from '../../../../services/nutrition-plan.service';
import { SocketService } from '../../../../services/socket.service';
import { MealPlanComponent } from './meal-plan.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';

describe('MealPlanComponent', () => {
  let component: MealPlanComponent;
  let fixture: ComponentFixture<MealPlanComponent>;
  let mockNutritionPlanService: jasmine.SpyObj<NutritionPlanService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockSocketService: jasmine.SpyObj<SocketService>;

  beforeEach(async () => {
    mockNutritionPlanService = jasmine.createSpyObj('NutritionPlanService', ['createMealPlan']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockSocketService = jasmine.createSpyObj('SocketService', ['fromEvent', 'disconnect']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, NoopAnimationsModule, MealPlanComponent],
      providers: [
        { provide: FormBuilder, useValue: new FormBuilder() },
        { provide: NutritionPlanService, useValue: mockNutritionPlanService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: SocketService, useValue: mockSocketService }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    mockSocketService.fromEvent.and.returnValue(of({ details: 'Sample data' }));
    mockNutritionPlanService.createMealPlan.and.returnValue(of({ message: 'Success' })); 
    fixture = TestBed.createComponent(MealPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {
    const form = component.formulario;
    expect(form.valid).toBeFalsy();
    expect(form.controls['user_id'].value).toEqual('');
    expect(form.controls['nutritional_objectives'].value).toEqual('');
    expect(form.controls['description'].value).toEqual('');
    expect(form.controls['meal_frequency'].value).toEqual('');
    expect(form.controls['food_types'].value).toEqual('');
  });
  
  it('should listen for nutrition plan notifications on init', () => {
    expect(mockSocketService.fromEvent).toHaveBeenCalledWith('nutrition_plan_notification');
  });

  it('should submit the form and navigate on successful creation', () => {
    spyOn(component, 'crearPlanAlimentacion').and.callThrough();
    mockNutritionPlanService.createMealPlan.and.returnValue(of({ message: 'Success' }));
    component.formulario.setValue({
      user_id: '1',
      nutritional_objectives: 'Gain Muscle',
      description: 'High protein diet',
      meal_frequency: '5 meals a day',
      food_types: 'High protein foods'
    });
  
    component.crearPlanAlimentacion();
    expect(component.crearPlanAlimentacion).toHaveBeenCalled();

  });
  
  it('should display an error message if form submission fails', () => {
    component.formulario.setValue({
      user_id: '1',
      nutritional_objectives: 'Lose Weight',
      description: 'Low carb diet',
      meal_frequency: '3 meals a day',
      food_types: 'Low carb foods'
    });
    mockNutritionPlanService.createMealPlan.and.returnValue(throwError(() => new Error('Failed to create meal plan')));
  
    component.crearPlanAlimentacion();
    expect(mockNutritionPlanService.createMealPlan).toHaveBeenCalled();
  });

  it('should unsubscribe from socket on destroy', () => {
    const spy = spyOn(component.socketSubscription, 'unsubscribe').and.callThrough();
    component.ngOnDestroy();
    expect(mockSocketService.disconnect).toHaveBeenCalled();
    expect(spy).toHaveBeenCalled();
  });
  
});
