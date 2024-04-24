import { Component, OnInit} from '@angular/core';
import { CreateServiceService } from '../../../../services/create-service.service'
import { CommonModule } from '@angular/common';

export interface Service {
  id: number;
  nombre: string;
  descripcion: string;
  costo: number;
}

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './service-list.component.html',
  styleUrl: './service-list.component.css'
})


export class ServiceListComponent implements OnInit {
  services: Service[] = [];

  constructor(private serviceService: CreateServiceService) {}

  ngOnInit(): void {
    this.serviceService.getServices().subscribe({
      next: (services) => {
        this.services = services;
      },
      error: (error) => {
        // Handle error
      }
    });
  }

  onPublishService(serviceId: number): void {
    // Call the publish service method
  }

}
