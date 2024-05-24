import { ComponentFixture, TestBed, fakeAsync, tick, waitForAsync } from '@angular/core/testing';
import { AthleteCalendarComponent } from './athlete-calendar.component';
import { CreateServiceService } from '../../../../services/create-service.service';
import { NotificationService } from '../../../../services/notification.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { MaterialModule } from '../../../../material.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';

describe('AthleteCalendarComponent', () => {
  let component: AthleteCalendarComponent;
  let fixture: ComponentFixture<AthleteCalendarComponent>;
  let mockCreateService: jasmine.SpyObj<CreateServiceService>;
  let mockNotificationService: jasmine.SpyObj<NotificationService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let notificationsSubject: Subject<any>;

  beforeEach(waitForAsync(() => {
    mockCreateService = jasmine.createSpyObj('CreateServiceService', ['getServicesPublished']);
    mockNotificationService = jasmine.createSpyObj('NotificationService', ['showNotification']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    notificationsSubject = new Subject<any>();

    TestBed.configureTestingModule({
      imports: [
        AthleteCalendarComponent, // Standalone component import
        CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
        MaterialModule,
        TranslateModule.forRoot(),
        HttpClientTestingModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatSnackBarModule
      ],
      providers: [
        { provide: CreateServiceService, useValue: mockCreateService },
        { provide: NotificationService, useValue: mockNotificationService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        TranslateService
      ],
      schemas: [NO_ERRORS_SCHEMA] // Use this schema to ignore unknown elements and attributes
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AthleteCalendarComponent);
    component = fixture.componentInstance;
    mockCreateService.getServicesPublished.and.returnValue(of({
      events: [{ event_date: new Date(), name: 'Event 1', duration: 60 }],
      services: []
    }));
    
    // Mock the notifications observable
    Object.defineProperty(mockNotificationService, 'notifications', { get: () => notificationsSubject.asObservable() });
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch events and services on init', () => {
    expect(mockCreateService.getServicesPublished).toHaveBeenCalled();
    expect(component.events.length).toBeGreaterThan(0);
  });

  it('should display notification for today\'s events', fakeAsync(() => {
    component.notifyUpcomingEvents();
    tick(); // Simulate passage of time if needed
    expect(mockNotificationService.showNotification).toHaveBeenCalledWith(jasmine.any(String));
  }));
});
