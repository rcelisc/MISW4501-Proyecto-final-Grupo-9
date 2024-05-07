import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-professional-dashboard',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule],
  templateUrl: './professional-dashboard.component.html',
  styleUrl: './professional-dashboard.component.scss'
})
export class ProfessionalDashboardComponent {
  dashboardItems = [
    { title: 'Crear Servicios', content: 'Crea servicios para que tus usuarios puedan adquirirlos', link: '/create-service' },
    { title: 'Ver Servicios y Publicar Servicios', content: 'Navega a traves de todos los servicios creados y visualiza cuales se han publicado o quieres publicar', link: '/service-list' },
    { title: 'Crear Planes de Entrenamiento', content: 'Crea planes de entrenamiento especializados para tus usuarios', link: '/training-plan' },
    { title: 'Crear Planes de Nutricion', content: 'Crea planes de nutricion especializados para tus usuarios', link: '/meal-plan'},
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