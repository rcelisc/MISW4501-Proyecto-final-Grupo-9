import { Component } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-event-organizer-dashboard',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule],
  templateUrl: './event-organizer-dashboard.component.html',
  styleUrl: './event-organizer-dashboard.component.scss'
})
export class EventOrganizerDashboardComponent {
  dashboardItems = [
    { title: 'Crear Eventos', content: 'Crea eventos para que los usuarios puedan asistir', link: '/create-event' },
    { title: 'Ver y Publicar Eventos', content: 'Visualiza todos los eventos que has creado y cuales se han publicado y cuales no', link: '/event-list' },
    { title: 'Ver Calendario de Eventos', content: 'Visualiza todos los eventos en un calendario para programar mejor proximos eventos', link: '/event-calendar' },
  ];
}
