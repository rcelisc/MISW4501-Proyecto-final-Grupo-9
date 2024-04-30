import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SportInfoComponent } from './sport-info.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { SportInfoService } from '../../../../services/sport-info.service';
import { MaterialModule } from '../../../../shared/material.module';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SportInfoComponent', () => {
  let component: SportInfoComponent;
  let fixture: ComponentFixture<SportInfoComponent>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockSportInfoService: jasmine.SpyObj<SportInfoService>;

  beforeEach(async () => {
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockSportInfoService = jasmine.createSpyObj('SportInfoService', ['createSportInfo']);

    mockSportInfoService.createSportInfo.and.returnValue(of({})); 
    await TestBed.configureTestingModule({
      imports: [MaterialModule, ReactiveFormsModule, SportInfoComponent, NoopAnimationsModule],
      providers: [
        FormBuilder,
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: SportInfoService, useValue: mockSportInfoService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SportInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the sport info form with empty fields', () => {
    const formFields = ['training_frequency', 'sports_practiced', 'average_session_duration', 'recovery_time', 'training_pace'];
    formFields.forEach(field => expect(component.sportInfoForm.get(field)?.value).toEqual(''));
  });

  it('should submit if form is empty', () => {
    mockSportInfoService.createSportInfo.and.returnValue(of({})); // Ensure mock returns an observable even if not called
  
    component.onSubmit();
  
    expect(mockSportInfoService.createSportInfo).toHaveBeenCalled();
  });
  

  it('should submit and navigate on valid form submission', fakeAsync(() => {
    component.sportInfoForm.setValue({
      training_frequency: 'Daily',
      sports_practiced: 'Running',
      average_session_duration: '2 hours',
      recovery_time: '48 hours',
      training_pace: 'Moderate'
    });
    mockSportInfoService.createSportInfo.and.returnValue(of({}));
    component.onSubmit();
    tick(5000);
    expect(mockSportInfoService.createSportInfo).toHaveBeenCalledWith(component.userId, {
      training_frequency: 'Daily',
      sports_practiced: 'Running',
      average_session_duration: '2 hours',
      recovery_time: '48 hours',
      training_pace: 'Moderate'
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  }));
});
