import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../shared/material.module';
import { EventOrganizerDashboardComponent } from './components/event-organizer-dashboard/event-organizer-dashboard.component';

@NgModule({
  declarations: [EventOrganizerDashboardComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ]
})
export class EventOrganizerModule {}
