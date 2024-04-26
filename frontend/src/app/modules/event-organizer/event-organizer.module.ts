import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarModule, DateAdapter} from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../shared/material.module';
import { EventOrganizerDashboardComponent } from './components/event-organizer-dashboard/event-organizer-dashboard.component';
import { EventCalendarComponent } from './components/event-calendar/event-calendar.component';

@NgModule({
  declarations: [
    EventOrganizerDashboardComponent, 
    EventCalendarComponent 
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
  ]
})
export class EventOrganizerModule {}
