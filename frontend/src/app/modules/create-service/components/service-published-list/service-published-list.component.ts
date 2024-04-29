import { Component } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { CreateServiceService } from '../../../../services/create-service.service';
import { CreateEventService } from '../../../../services/create-event.service';

@Component({
  selector: 'app-service-published-list',
  standalone: true,
  imports: [MaterialModule, CommonModule],
  templateUrl: './service-published-list.component.html',
  styleUrl: './service-published-list.component.scss'
})
export class ServicePublishedListComponent {
  servicesDataSource = new MatTableDataSource<any>([]);
  eventsDataSource = new MatTableDataSource<any>([]);
  servicesDisplayedColumns: string[] = ['nombre', 'descripcion', 'costo', 'disponibilidad'];
  eventsDisplayedColumns: string[] = ['nombre', 'descripcion', 'costo', 'fechaEvento', 'disponibilidad'];


  constructor(
    private createServiceService: CreateServiceService,
    private createEventService: CreateEventService,
  ) {}

  ngOnInit(): void {
    this.loadPublishedItems();
  }

  loadPublishedItems(): void {
    this.createServiceService.getServicesPublished().subscribe({
      next: (response: any) => {
        // Prepare data by marking all services as initially available
        const services = response.services.map((service: any) => ({
          ...service,
          purchased: false
        }));
        const events = response.events.map((event: any) => ({
          ...event,
          enrolled: false
        }));
        this.servicesDataSource.data = response.services;
        this.eventsDataSource.data = response.events;
      },
      error: (error) => console.error('Failed to load services and events', error)
    });
  }

  enrollEvent(eventId: number): void {
    const userId = 3; // Hardcoded user ID for now
    this.createEventService.enrollEvent(eventId, userId).subscribe({
      next: (response) => {
        console.log('Enrollment successful');
        const event = this.eventsDataSource.data.find((e: any) => e.id === eventId);
        if (event) {
          event.enrolled = true;
          this.eventsDataSource.data = this.eventsDataSource.data.slice(); 
        }
      },
      error: (error) => console.error('Error enrolling in event', error)
    });
}

  purchaseService(service: any): void {
    service.purchased = true;
    this.servicesDataSource.data = this.servicesDataSource.data.slice();
  }
}
