import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateServiceComponent } from './create-service.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CreateServiceService } from '../../../../services/create-service.service';
import { MaterialModule } from '../../../../material.module';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('CreateServiceComponent', () => {
  let component: CreateServiceComponent;
  let fixture: ComponentFixture<CreateServiceComponent>;
  let mockCreateServiceService: jasmine.SpyObj<CreateServiceService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockCreateServiceService = jasmine.createSpyObj('CreateServiceService', ['createService']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [MaterialModule, ReactiveFormsModule, CreateServiceComponent, NoopAnimationsModule],
      providers: [
        FormBuilder,
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: CreateServiceService, useValue: mockCreateServiceService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.createServiceForm.valid).toBeFalsy();
  });

  it('should not submit invalid form', () => {
    component.onSubmit();
    expect(mockCreateServiceService.createService).not.toHaveBeenCalled();
  });

  it('should submit and navigate on valid form submission', fakeAsync(() => {
    component.createServiceForm.setValue({
      name: 'Fitness Training',
      description: 'Personalized fitness plans',
      rate: '100'
    });
    mockCreateServiceService.createService.and.returnValue(of({}));
    component.onSubmit();
    tick(5000);
    expect(mockCreateServiceService.createService).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/service-list']);
  }));

  it('should handle errors during service creation', () => {
    component.createServiceForm.setValue({
      name: 'Diet Plans',
      description: 'Custom meal plans',
      rate: '150'
    });
    mockCreateServiceService.createService.and.returnValue(throwError(() => new Error('Creation failed')));
    component.onSubmit();
    expect(mockCreateServiceService.createService).toHaveBeenCalled();
  });
});
