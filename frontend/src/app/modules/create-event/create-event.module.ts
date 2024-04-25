import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { EventListComponent } from './components/event-list/event-list.component';
import { MaterialModule } from '../../shared/material.module';

@NgModule({
  declarations: [
    CreateEventComponent,
    EventListComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  providers: []
})
export class CreateEventModule { }