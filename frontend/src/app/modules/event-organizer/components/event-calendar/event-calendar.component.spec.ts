import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventCalendarComponent } from './event-calendar.component';
import { CreateEventService } from '../../../../services/create-event.service';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { MonthViewDay } from 'calendar-utils';

describe('EventCalendarComponent', () => {
  let component: EventCalendarComponent;
  let fixture: ComponentFixture<EventCalendarComponent>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;

  beforeEach(async () => {
    mockCreateEventService = jasmine.createSpyObj('CreateEventService', ['getEvents']);

    await TestBed.configureTestingModule({
      imports: [
        CalendarModule, EventCalendarComponent
      ],
      providers: [
        { provide: CreateEventService, useValue: mockCreateEventService }
      ],
      schemas: [NO_ERRORS_SCHEMA]  // Use NO_ERRORS_SCHEMA to ignore unrecognized elements and attributes
    }).compileComponents();

    fixture = TestBed.createComponent(EventCalendarComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch events on initialization', () => {
    const mockEvents = [{ event_date: new Date(), name: 'Test Event', duration: 120 }];
    mockCreateEventService.getEvents.and.returnValue(of(mockEvents));
    fixture.detectChanges(); // ngOnInit() will be called here

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
      events: [{ title: 'Event 1', start:new Date() }, { title: 'Event 2', start: new Date()}],
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
