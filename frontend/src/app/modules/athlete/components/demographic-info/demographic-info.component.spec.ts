import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DemographicInfoComponent } from './demographic-info.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { DemographicInfoService } from '../../../../services/demographic-info.service';
import { MaterialModule } from '../../../../material.module';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DemographicInfoComponent', () => {
  let component: DemographicInfoComponent;
  let fixture: ComponentFixture<DemographicInfoComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockDemographicInfoService: jasmine.SpyObj<DemographicInfoService>;

  beforeEach(async () => {
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockDemographicInfoService = jasmine.createSpyObj('DemographicInfoService', ['createDemographicInfo']);

    // Return values for the spies
    mockDemographicInfoService.createDemographicInfo.and.returnValue(of({})); 

    await TestBed.configureTestingModule({
      imports: [MaterialModule, ReactiveFormsModule, DemographicInfoComponent, NoopAnimationsModule],
      providers: [
        FormBuilder,
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: DemographicInfoService, useValue: mockDemographicInfoService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DemographicInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {
    const form = component.demographicInfoForm;
    const formFields = ['ethnicity', 'heart_rate', 'vo2_max', 'blood_pressure', 'respiratory_rate'];
    formFields.forEach(field => expect(form.get(field)?.value).toEqual(''));
  });

  it('should submit demographic info if the form is valid', () => {
    // Set valid form values
    component.demographicInfoForm.setValue({
      ethnicity: 'Latino',
      heart_rate: 75,
      vo2_max: 45,
      blood_pressure: '120/80',
      respiratory_rate: 16
    });
    component.onSubmit();
    expect(mockDemographicInfoService.createDemographicInfo).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  });
});
