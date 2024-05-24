import { ComponentFixture, TestBed, fakeAsync, flush, tick } from '@angular/core/testing';
import { SelectPlanComponent } from './select-plan.component';
import { PlanService } from '../../../../services/plan.service';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';

describe('SelectPlanComponent', () => {
  let component: SelectPlanComponent;
  let fixture: ComponentFixture<SelectPlanComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockPlanService: jasmine.SpyObj<PlanService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockPlanService = jasmine.createSpyObj('PlanService', ['updatePlan']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MaterialModule,
        RouterModule,
        NoopAnimationsModule,
        MatSnackBarModule,
        TranslateModule.forRoot(),
        SelectPlanComponent // Import the standalone component here
      ],
      providers: [
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: PlanService, useValue: mockPlanService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SelectPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with predefined dashboard items', () => {
    expect(component.dashboardItems.length).toBe(3);
    expect(component.dashboardItems[0].planType).toEqual('basic');
  });

  it('should set user ID from token on init', () => {
    const mockToken = 'mock-token';
    const mockUserId = 1;

    spyOn(localStorage, 'getItem').and.returnValue(mockToken);
    mockAuthService.decodeToken.and.returnValue({ user_id: mockUserId });

    component.ngOnInit();

    expect(mockAuthService.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(component.userId).toBe(mockUserId);
  });

  it('should handle invalid token on init', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);

    component.ngOnInit();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle plan update success', fakeAsync(() => {
    const mockUserId = 1;
    const mockToken = 'mock-token';

    spyOn(localStorage, 'getItem').and.returnValue(mockToken);
    mockAuthService.decodeToken.and.returnValue({ user_id: mockUserId });
    mockPlanService.updatePlan.and.returnValue(of({}));

    component.ngOnInit();
    component.updatePlan('premium');
    flush();

    expect(mockPlanService.updatePlan).toHaveBeenCalledWith(mockUserId, 'premium');
    // Since MatSnackBar is mocked, we won't verify it here as per the user's request
  }));

  it('should handle plan update failure', fakeAsync(() => {
    const mockUserId = 1;
    const mockToken = 'mock-token';

    spyOn(localStorage, 'getItem').and.returnValue(mockToken);
    mockAuthService.decodeToken.and.returnValue({ user_id: mockUserId });
    mockPlanService.updatePlan.and.returnValue(throwError(() => new Error('Failed')));

    component.ngOnInit();
    component.updatePlan('basic');
    flush();

    expect(mockPlanService.updatePlan).toHaveBeenCalledWith(mockUserId, 'basic');
    // Since MatSnackBar is mocked, we won't verify it here as per the user's request
  }));

  it('should navigate to athlete-dashboard on goBack', () => {
    component.goBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  });
});
