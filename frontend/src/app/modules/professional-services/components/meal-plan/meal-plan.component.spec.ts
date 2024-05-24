import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NutritionPlanService } from '../../../../services/nutrition-plan.service';
import { SocketService } from '../../../../services/socket.service';
import { AuthService } from '../../../../services/auth.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { MealPlanComponent } from './meal-plan.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';

describe('MealPlanComponent', () => {
  let component: MealPlanComponent;
  let fixture: ComponentFixture<MealPlanComponent>;
  let mockNutritionPlanService: jasmine.SpyObj<NutritionPlanService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockSocketService: jasmine.SpyObj<SocketService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    mockNutritionPlanService = jasmine.createSpyObj('NutritionPlanService', ['createMealPlan']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockSocketService = jasmine.createSpyObj('SocketService', ['fromEvent', 'disconnect']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['getUsers']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        TranslateModule.forRoot(),
        MealPlanComponent // Import the standalone component here
      ],
      providers: [
        { provide: FormBuilder, useValue: new FormBuilder() },
        { provide: NutritionPlanService, useValue: mockNutritionPlanService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: SocketService, useValue: mockSocketService },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    mockSocketService.fromEvent.and.returnValue(of({ details: 'Sample data' }));
    mockNutritionPlanService.createMealPlan.and.returnValue(of({ message: 'Success' }));
    mockAuthService.getUsers.and.returnValue(of([{ id: 1, name: 'John', surname: 'Doe' }]));

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

  it('should load users on init', () => {
    expect(mockAuthService.getUsers).toHaveBeenCalledWith('athlete');
    expect(component.users.length).toBe(1);
    expect(component.users[0].name).toBe('John');
    expect(component.users[0].surname).toBe('Doe');
  });

  it('should submit the form and navigate on successful creation', () => {
    spyOn(component, 'crearPlanAlimentacion').and.callThrough();
    component.formulario.setValue({
      user_id: '1',
      nutritional_objectives: 'Gain Muscle',
      description: 'High protein diet',
      meal_frequency: '5 meals a day',
      food_types: 'High protein foods'
    });

    component.crearPlanAlimentacion();
    expect(component.crearPlanAlimentacion).toHaveBeenCalled();
    expect(mockNutritionPlanService.createMealPlan).toHaveBeenCalledWith(component.formulario.value);
  });

  it('should display an error message if form submission fails', () => {
    mockNutritionPlanService.createMealPlan.and.returnValue(throwError(() => new Error('Failed to create meal plan')));
    component.formulario.setValue({
      user_id: '1',
      nutritional_objectives: 'Lose Weight',
      description: 'Low carb diet',
      meal_frequency: '3 meals a day',
      food_types: 'Low carb foods'
    });

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
