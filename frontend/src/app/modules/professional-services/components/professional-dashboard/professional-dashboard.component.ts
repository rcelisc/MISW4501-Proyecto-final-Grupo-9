import { Component } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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
    //{ title: 'Crear Planes de Entrenamiento', content: 'Crea planes de entrenamiento especializados para tus usuarios', link: '/view-training-history' },
    { title: 'Crear Planes de Entrenamiento', content: 'Crea planes de entrenamiento especializados para tus usuarios', link: '/plan-entrenamiento' },
    //{ title: 'Crear Planes de Nutricion', content: 'Crea planes de nutricion especializados para tus usuarios', link: '/select-plan'},
    { title: 'Crear Planes de Nutricion', content: 'Crea planes de nutricion especializados para tus usuarios', link: '/meal-plan'},
  ];
}
