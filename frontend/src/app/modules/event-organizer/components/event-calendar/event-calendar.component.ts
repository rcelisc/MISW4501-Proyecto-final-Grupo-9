import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarEventAction, CalendarView } from 'angular-calendar';
import { CreateEventService } from '../../../../services/create-event.service'
import { MaterialModule } from '../../../../shared/material.module';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { CommonModule } from '@angular/common';
import { MonthViewDay } from 'calendar-utils';


@Component({
  selector: 'app-event-calendar',
  standalone: true,
  imports: [
    MaterialModule, CalendarModule, CommonModule
  ],
  templateUrl: './event-calendar.component.html',
  styleUrl: './event-calendar.component.scss'
})
export class EventCalendarComponent implements OnInit {
  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  view: CalendarView = CalendarView.Month;
  selectedEvent: CalendarEvent | null = null;
  selectedEvents: CalendarEvent[] = [];
  isDetailsVisible: boolean = false;

  constructor(private createEventService: CreateEventService) {}

  ngOnInit(): void {
    this.fetchEvents();
  }

  fetchEvents(): void {
    this.createEventService.getEvents().subscribe(eventsFromServer => {
      this.events = eventsFromServer.map(event => ({
        start: new Date(event.event_date),
        end: new Date(new Date(event.event_date).getTime() + event.duration * 3600000), // Duration is in hours
        title: event.name,
        color: {
          primary: '#1e90ff',
          secondary: '#D1E8FF'
        },
        meta: {
          description: event.description,
          location: event.location,
          category: event.category,
          fee: event.fee,
          status: event.status
        }
      }));
    });
  }

  nextMonth(): void {
    const nextMonth = new Date(this.viewDate);
    nextMonth.setMonth(this.viewDate.getMonth() + 1);
    this.viewDate = nextMonth;
  }

  previousMonth(): void {
    const prevMonth = new Date(this.viewDate);
    prevMonth.setMonth(this.viewDate.getMonth() - 1);
    this.viewDate = prevMonth;
  }

  today(): void {
    this.viewDate = new Date();
  }

  dayClicked(day: MonthViewDay<any>): void {
    this.selectedEvents = day.events;
    this.selectedEvents = day.events; // Store all events of the clicked day
    this.isDetailsVisible = false; // Hide the details view when a new day is clicked
    this.selectedEvent = null;
  }

  selectEvent(event: CalendarEvent): void {
    this.selectedEvent = event;
    this.isDetailsVisible = !this.isDetailsVisible; // Toggle visibility of the details
  }

  closeDetails(): void {
    this.isDetailsVisible = false;
  }
  
}