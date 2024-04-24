import { Component } from '@angular/core';

@Component({
  selector: 'app-professional-profile',
  standalone: true,
  imports: [],
  templateUrl: './professional-profile.component.html',
  styleUrl: './professional-profile.component.css'
})
export class ProfessionalProfileComponent {
  professional = {
    name: 'Jhon Smith',
    title: 'Profesional',
    city: 'Bogotá',
    country: 'Colombia'
  };
}
