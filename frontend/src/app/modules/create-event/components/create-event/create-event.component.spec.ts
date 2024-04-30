import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateEventComponent } from './create-event.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CreateEventService } from '../../../../services/create-event.service';
import { MaterialModule } from '../../../../shared/material.module';
import { DatePipe } from '@angular/common';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('CreateEventComponent', () => {
  let component: CreateEventComponent;
  let fixture: ComponentFixture<CreateEventComponent>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockRouter: jasmine.SpyObj<Router>;
  let datePipe: DatePipe;

  beforeEach(async () => {
    mockCreateEventService = jasmine.createSpyObj('CreateEventService', ['createEvent']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    datePipe = new DatePipe('en-US');  // Using real DatePipe for simplicity

    await TestBed.configureTestingModule({
      imports: [MaterialModule, ReactiveFormsModule, NoopAnimationsModule, CreateEventComponent],
      providers: [
        FormBuilder,
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: CreateEventService, useValue: mockCreateEventService },
        { provide: DatePipe, useValue: datePipe }  // Can use actual DatePipe since it's straightforward
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateEventComponent);
    component = fixture.componentInstance;
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
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/event-list']);
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
