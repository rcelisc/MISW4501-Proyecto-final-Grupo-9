import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarEventAction, CalendarView } from 'angular-calendar';
import { CreateServiceService } from '../../../../services/create-service.service'
import { NotificationService } from '../../../../services/notification.service';
import { MaterialModule } from '../../../../shared/material.module';
import { CalendarModule } from 'angular-calendar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MonthViewDay } from 'calendar-utils';
import { NotificationManagerComponent } from '../../../../shared/notification/notification.component';
import { Router } from '@angular/router';
@Component({
  selector: 'app-athlete-calendar',
  standalone: true,
  imports: [
    MaterialModule, CalendarModule, CommonModule, NotificationManagerComponent
  ],
  templateUrl: './athlete-calendar.component.html',
  styleUrl: './athlete-calendar.component.scss'
})
export class AthleteCalendarComponent implements OnInit {
  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  view: CalendarView = CalendarView.Month;
  selectedEvent: CalendarEvent | null = null;
  selectedEvents: CalendarEvent[] = [];
  isDetailsVisible: boolean = false;

  constructor(
    private createServiceService: CreateServiceService,
    private notificationService: NotificationService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.fetchEvents();
  }

  fetchEvents(): void {
    this.createServiceService.getServicesPublished().subscribe((response: any) => {
      const eventsMapped = (response.events as any[]).map(event => ({
        start: new Date(event.event_date),
        end: new Date(new Date(event.event_date).getTime() + event.duration * 3600000),
        title: event.name,
        color: {
          primary: '#1e90ff', // blue for events
          secondary: '#D1E8FF'
        },
        meta: {
          type: 'event',
          description: event.description,
          location: event.location,
          category: event.category,
          fee: event.fee,
          status: event.status,
          registrationDeadline: event.additional_info?.registration_deadline,
          maxParticipants: event.additional_info?.max_participants,
          minAge: event.additional_info?.min_age
        }
      }));
  
      const servicesMapped = response.services.map((service:any) => ({
        start: new Date(), // Date is not relevant for services, so we set it to today
        end: new Date(),
        allDay: true, // Consider services as all-day as no time is specified
        title: service.name,
        color: {
          primary: '#ff9800', // orange for services
          secondary: '#FFE0B2'
        },
        meta: {
          type: 'service',
          description: service.description,
          fee: service.rate,
          status: service.status,
          available: service.available
        }
      }));
  
      this.events = [...eventsMapped, ...servicesMapped]; // Combine events and services
      this.notifyUpcomingEvents();
    });
  }

  notifyUpcomingEvents(): void {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(0, 0, 0, 0);

    let todayEvents:any = [];
    let tomorrowEvents:any = [];

    this.events.forEach(event => {
      const eventDate = new Date(event.start);
      eventDate.setHours(0, 0, 0, 0); // Clear the time portion for accurate comparison
      if (eventDate.getTime() === today.getTime()) {
        todayEvents.push(event.title);
      } else if (eventDate.getTime() === tomorrow.getTime()) {
        tomorrowEvents.push(event.title);
      }
    });

    if (todayEvents.length > 0) {
      this.notificationService.showNotification('Today\'s events: ' + todayEvents.join(', '));
    }
    if (tomorrowEvents.length > 0) {
      this.notificationService.showNotification('Tomorrow\'s events: ' + tomorrowEvents.join(', '));
    }
  }

  showNotification(message: string, position: 'top' | 'bottom'): void {
    this.snackBar.open(message, 'Close', {
      duration: 10000, // Notification duration
      horizontalPosition: 'right', // Horizontal position
      verticalPosition: position // Vertical position either top or bottom
    });
  }

  nextMonth(): void {
    const nextMonth = new Date(this.viewDate);
    nextMonth.setMonth(this.viewDate.getMonth() + 1);
    this.viewDate = nextMonth;
  }

  previousMonth(): void {
    const previousMonth = new Date(this.viewDate);
    previousMonth.setMonth(this.viewDate.getMonth() - 1);
    this.viewDate = previousMonth;
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

  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}
