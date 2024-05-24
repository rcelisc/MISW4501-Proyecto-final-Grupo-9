import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateServiceComponent } from './create-service.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { CreateServiceService } from '../../../../services/create-service.service';
import { MaterialModule } from '../../../../material.module';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

describe('CreateServiceComponent', () => {
  let component: CreateServiceComponent;
  let fixture: ComponentFixture<CreateServiceComponent>;
  let mockCreateServiceService: jasmine.SpyObj<CreateServiceService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let router: Router;
  let translateService: TranslateService;

  beforeEach(async () => {
    const createServiceServiceSpy = jasmine.createSpyObj('CreateServiceService', ['createService']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        NoopAnimationsModule,
        MaterialModule,
        CreateServiceComponent
      ],
      providers: [
        { provide: CreateServiceService, useValue: createServiceServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateServiceComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    mockCreateServiceService = TestBed.inject(CreateServiceService) as jasmine.SpyObj<CreateServiceService>;
    mockSnackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'fillRequiredFields': 'Please fill in all required fields.',
        'serviceCreated': 'Service created successfully.',
        'serviceCreationError': 'Error creating service.',
      };
      return translations[key] || key;
    });

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
    const navigateSpy = spyOn(router, 'navigate');
    component.createServiceForm.setValue({
      name: 'Fitness Training',
      description: 'Personalized fitness plans',
      rate: '100'
    });
    mockCreateServiceService.createService.and.returnValue(of({}));
    component.onSubmit();
    tick(500);
    expect(mockCreateServiceService.createService).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/professional-dashboard']);
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
