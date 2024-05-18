import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { CreateServiceService } from '../../../../services/create-service.service';
import { CreateEventService } from '../../../../services/create-event.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-service-published-list',
  standalone: true,
  imports: [MaterialModule, CommonModule],
  templateUrl: './service-published-list.component.html',
  styleUrl: './service-published-list.component.scss'
})
export class ServicePublishedListComponent implements OnInit {
  servicesDataSource = new MatTableDataSource<any>([]);
  eventsDataSource = new MatTableDataSource<any>([]);
  servicesDisplayedColumns: string[] = ['nombre', 'descripcion', 'costo', 'disponibilidad'];
  eventsDisplayedColumns: string[] = ['nombre', 'descripcion', 'costo', 'fechaEvento', 'disponibilidad'];
  userId: number = 0;


  constructor(
    private createServiceService: CreateServiceService,
    private createEventService: CreateEventService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.setUserIdFromToken();
  }

  setUserIdFromToken(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.userId = decodedToken.user_id;
        this.loadPublishedItems();
      } else {
        console.error('Token is invalid or expired');
        this.router.navigate(['/login']); // Redirect to login if the token is invalid or expired
      }
    } else {
      this.router.navigate(['/login']); // Redirect to login if there's no token
    }
  }

  loadPublishedItems(): void {
    this.createServiceService.getPublishedItemsWithUserStatus(this.userId).subscribe({
      next: ({ services, events }) => {
        this.servicesDataSource.data = services;
        this.eventsDataSource.data = events;
      },
      error: (error) => {
        console.error('Failed to load services and events', error);
        this.router.navigate(['/login']); // Optionally redirect or handle errors more gracefully
      }
    });
  }

  enrollEvent(eventId: number): void {
    this.createEventService.enrollEvent(eventId).subscribe({
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

  purchaseService(serviceId: number): void {
    if (!this.userId) {
      console.error('User ID is not available');
      return;
    }
    this.createServiceService.purchaseService(serviceId, this.userId).subscribe({
      next: (response) => {
        console.log('Purchase successful');
        const service = this.servicesDataSource.data.find((s: any) => s.id === serviceId);
        if (service) {
          service.purchased = true;
          this.servicesDataSource.data = this.servicesDataSource.data.slice();
        }
      },
      error: (error) => console.error('Error purchasing service', error)
    });
  }
  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}