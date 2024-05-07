import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-event-organizer-dashboard',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule],
  templateUrl: './event-organizer-dashboard.component.html',
  styleUrl: './event-organizer-dashboard.component.scss'
})
export class EventOrganizerDashboardComponent implements OnInit{
  dashboardItems = [
    { title: 'Crear Eventos', content: 'Crea eventos para que los usuarios puedan asistir', link: '/create-event' },
    { title: 'Ver y Publicar Eventos', content: 'Visualiza todos los eventos que has creado y cuales se han publicado y cuales no', link: '/event-list' },
    { title: 'Ver Calendario de Eventos', content: 'Visualiza todos los eventos en un calendario para programar mejor proximos eventos', link: '/event-calendar' },
  ];
  
  userData: any;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.fetchUserData();
  }

  fetchUserData() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.authService.getUserById(decodedToken.user_id).subscribe({
          next: (data) => {
            this.userData = data;
          },
          error: (err) => {
            console.error('Failed to fetch user data:', err);
          }
        });
      } else {
        console.error('Token is invalid or expired');
      }
    }
  }
}