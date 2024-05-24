import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventCalendarComponent } from './event-calendar.component';
import { CreateEventService } from '../../../../services/create-event.service';
import { CalendarModule } from 'angular-calendar';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { MonthViewDay } from 'calendar-utils';

describe('EventCalendarComponent', () => {
  let component: EventCalendarComponent;
  let fixture: ComponentFixture<EventCalendarComponent>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;
  let router: Router;
  let translateService: TranslateService;

  beforeEach(async () => {
    mockCreateEventService = jasmine.createSpyObj('CreateEventService', ['getEvents']);

    await TestBed.configureTestingModule({
      imports: [
        CalendarModule,
        TranslateModule.forRoot(),
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),
        MaterialModule,
        EventCalendarComponent
      ],
      providers: [
        { provide: CreateEventService, useValue: mockCreateEventService },
        TranslateService
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(EventCalendarComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'eventCalendarTitle': 'Event Calendar',
        'eventCalendarPrevMonth': 'Previous Month',
        'eventCalendarNextMonth': 'Next Month',
        'eventCalendarEventsOn': 'Events on',
        'eventCalendarClose': 'Close',
        'eventCalendarEventDetails': 'Event Details',
        'eventCalendarEventTitle': 'Title',
        'eventCalendarEventStart': 'Start',
        'eventCalendarEventEnd': 'End',
        'eventCalendarEventLocation': 'Location',
        'eventCalendarEventDescription': 'Description',
      };
      return translations[key] || key;
    });

    mockCreateEventService.getEvents.and.returnValue(of([
      { event_date: new Date(), name: 'Test Event', duration: 120, description: 'Test Description', location: 'Test Location', category: 'Test Category', fee: 'Test Fee', status: 'Test Status' }
    ]));

    fixture.detectChanges(); // ngOnInit() will be called here
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch events on initialization', () => {
    expect(component.events.length).toBe(1);
    expect(component.events[0].title).toEqual('Test Event');
  });

  it('should correctly navigate to next and previous month', () => {
    const originalMonth = component.viewDate.getMonth();

    component.nextMonth();
    expect(component.viewDate.getMonth()).toBe(originalMonth + 1);

    component.previousMonth();
    expect(component.viewDate.getMonth()).toBe(originalMonth);
  });

  it('should reset to today when today function is called', () => {
    const today = new Date();
    component.today();
    expect(component.viewDate.toDateString()).toEqual(today.toDateString());
  });

  it('should select event and toggle details', () => {
    const event = { title: 'Event 1', start: new Date() };
    component.selectEvent(event);
    expect(component.selectedEvent).toBe(event);
    expect(component.isDetailsVisible).toBeTruthy();

    component.selectEvent(event);
    expect(component.isDetailsVisible).toBeFalsy();
  });

  it('should close details', () => {
    component.isDetailsVisible = true;
    component.closeDetails();
    expect(component.isDetailsVisible).toBeFalsy();
  });

  it('should update selected events on day click', () => {
    const currentDate = new Date();
    const day: MonthViewDay<any> = {
      events: [{ title: 'Event 1', start: new Date() }, { title: 'Event 2', start: new Date() }],
      date: new Date(),
      inMonth: true,
      isFuture: true,
      isPast: false,
      isToday: false,
      badgeTotal: 2,
      day: currentDate.getDate(),
      isWeekend: currentDate.getDay() === 0 || currentDate.getDay() === 6
    };

    component.dayClicked(day);
    expect(component.selectedEvents.length).toBe(2);
    expect(component.isDetailsVisible).toBeFalsy();
    expect(component.selectedEvent).toBeNull();
  });
});
