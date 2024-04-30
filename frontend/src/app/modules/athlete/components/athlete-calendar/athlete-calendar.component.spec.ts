import { ComponentFixture, TestBed, fakeAsync, tick, waitForAsync } from '@angular/core/testing';
import { AthleteCalendarComponent } from './athlete-calendar.component';
import { CreateServiceService } from '../../../../services/create-service.service';
import { NotificationService } from '../../../../services/notification.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AthleteCalendarComponent', () => {
  let component: AthleteCalendarComponent;
  let fixture: ComponentFixture<AthleteCalendarComponent>;
  let mockCreateService: any;
  let mockNotificationService: any;
  let mockSnackBar: any;

  beforeEach(waitForAsync(() => {
    mockCreateService = jasmine.createSpyObj('CreateServiceService', ['getServicesPublished']);
    mockNotificationService = jasmine.createSpyObj('NotificationService', ['showNotification']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockNotificationService.notifications = of({ /* mock data that resembles what would be emitted */ });


    TestBed.configureTestingModule({
      imports: [ AthleteCalendarComponent ], // Change here from declarations to imports
      providers: [
        { provide: CreateServiceService, useValue: mockCreateService },
        { provide: NotificationService, useValue: mockNotificationService },
        { provide: MatSnackBar, useValue: mockSnackBar }
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
