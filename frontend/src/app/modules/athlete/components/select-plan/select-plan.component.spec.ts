import { ComponentFixture, TestBed, fakeAsync, flush, tick } from '@angular/core/testing';
import { SelectPlanComponent } from './select-plan.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PlanService } from '../../../../services/plan.service';
import { delay, finalize, of, tap, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../../../../shared/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SelectPlanComponent', () => {
  let component: SelectPlanComponent;
  let fixture: ComponentFixture<SelectPlanComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockPlanService: jasmine.SpyObj<PlanService>;

  beforeEach(async () => {
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockPlanService = jasmine.createSpyObj('PlanService', ['updatePlan']);

    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MaterialModule,
        RouterModule, 
        MaterialModule, 
        SelectPlanComponent,
        NoopAnimationsModule,
        MatSnackBarModule
      ],
      providers: [
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: PlanService, useValue: mockPlanService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SelectPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should directly open MatSnackBar', fakeAsync(() => {
    mockSnackBar.open('Test message', 'Close', { duration: 2000 });
    tick();
    expect(mockSnackBar.open).toHaveBeenCalledWith('Test message', 'Close', { duration: 2000 });
  }));
  

  it('should initialize with predefined dashboard items', () => {
    expect(component.dashboardItems.length).toBe(3);
    expect(component.dashboardItems[0].planType).toEqual('basic');
  });

  it('should handle plan update success', fakeAsync(() => {
    // Ensure the observable completes after emitting
    mockPlanService.updatePlan.and.returnValue(of({}).pipe(
      tap(response => console.log("Emitting value")),
      finalize(() => console.log("Completing observable"))
    ));
    component.updatePlan('premium');
    flush();
    expect(mockPlanService.updatePlan).toHaveBeenCalledWith(component.userId, 'premium');
  }));
  

  it('should handle plan update failure', fakeAsync(() => {
    mockPlanService.updatePlan.and.returnValue(throwError(() => new Error('Failed')));
    component.updatePlan('basic');
    flush();
    expect(mockPlanService.updatePlan).toHaveBeenCalledWith(1, 'basic');
  }));
});
