import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateEventComponent } from './create-event.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { CreateEventService } from '../../../../services/create-event.service';
import { MaterialModule } from '../../../../material.module';
import { DatePipe } from '@angular/common';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('CreateEventComponent', () => {
  let component: CreateEventComponent;
  let fixture: ComponentFixture<CreateEventComponent>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let translateService: TranslateService;
  let datePipe: DatePipe;

  beforeEach(async () => {
    const createEventServiceSpy = jasmine.createSpyObj('CreateEventService', ['createEvent']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    datePipe = new DatePipe('en-US');

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        NoopAnimationsModule,
        MaterialModule,
        CreateEventComponent
      ],
      providers: [
        FormBuilder,
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: Router, useValue: routerSpy },
        { provide: CreateEventService, useValue: createEventServiceSpy },
        { provide: DatePipe, useValue: datePipe }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateEventComponent);
    component = fixture.componentInstance;
    mockCreateEventService = TestBed.inject(CreateEventService) as jasmine.SpyObj<CreateEventService>;
    mockSnackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'createEventValidationError': 'Please fill in all required fields.',
        'createEventDateError': 'Please provide a valid event date.',
        'createEventDateFormatError': 'Event date format is incorrect.',
        'createEventSuccess': 'Event created successfully.',
        'createEventError': 'Error creating event.',
      };
      return translations[key] || key;
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.createEventForm.valid).toBeFalsy();
  });

  it('should not submit invalid form', () => {
    component.onSubmit();
    expect(mockCreateEventService.createEvent).not.toHaveBeenCalled();
  });

  it('should submit and navigate on valid form submission', fakeAsync(() => {
    component.createEventForm.setValue({
      name: 'Music Festival',
      description: 'Annual Music Event',
      event_date: new Date(),
      duration: '120',
      location: 'Beach Park',
      category: 'Music',
      fee: '50'
    });
    mockCreateEventService.createEvent.and.returnValue(of({}));
    component.onSubmit();
    tick(5000);
    expect(mockCreateEventService.createEvent).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/organizer-dashboard']);
  }));

  it('should handle errors during event creation', fakeAsync(() => {
    component.createEventForm.setValue({
      name: 'Tech Conference',
      description: 'Annual Tech Symposium',
      event_date: new Date(),
      duration: '300',
      location: 'Convention Center',
      category: 'Technology',
      fee: '200'
    });
    mockCreateEventService.createEvent.and.returnValue(throwError(() => new Error('Backend Error')));
    component.onSubmit();
    tick(5000);
    expect(mockCreateEventService.createEvent).toHaveBeenCalled();
    }));
});
