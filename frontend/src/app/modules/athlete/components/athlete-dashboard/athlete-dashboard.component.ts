import { Component } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-athlete-dashboard',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule],
  templateUrl: './athlete-dashboard.component.html',
  styleUrl: './athlete-dashboard.component.scss'
})
export class AthleteDashboardComponent {
  dashboardItems = [
    { title: 'Ver Calendario', content: 'Visualiza en tu calendario todos las actividades que puedes realizar', link: '/athlete-calendar' },
    { title: 'Ver Eventos y Servicios', content: 'Navega a traves de todos los eventos y servicios a lo que puedes inscribirte o ya estas inscrito', link: '/service-published-list' },
    { title: 'Ver Historial de Entrenamiento', content: 'Consulta tu historial de las sesiones de entrenamiento realizadas', link: '/training-history' },
    { title: 'Seleccionar Plan', content: 'Cambia de plan: Basico, Intermedio o Premium', link: '/select-plan'},
    { title: 'Agregar informacion demografica', content: 'Agregar mayor informacion sobre tu salud e informacion demografica', link: '/demographic-info'},
    { title: 'Agregar informacion deportiva', content: 'Agrega mayoir informacion acerca de tus habitos deportivos', link: '/sport-info'},
  ];
}
