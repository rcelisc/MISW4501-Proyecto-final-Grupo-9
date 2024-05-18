import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { CreateServiceService } from '../../../../services/create-service.service'
import { MaterialModule } from '../../../../shared/material.module';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [MaterialModule, CommonModule],
  templateUrl: './service-list.component.html',
  styleUrl: './service-list.component.scss'
})


export class ServiceListComponent implements OnInit {
  services: any[] = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = ['nombre', 'descripcion', 'costo', 'estado'];

  constructor(
    private createServiceService: CreateServiceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.createServiceService.getServices().subscribe({
      next: (services: any[]) => {
        this.dataSource.data = services;  // Now 'services' is directly the array
      },
      error: (error) => {
        console.error('Failed to load services', error);
      }
    });
  }

  onPublishService(service: any): void {
    if (service.status === 'published') {
      return;
    }
    this.createServiceService.publishService(service.id).subscribe({
      next: () => {
        // Update the local service state to reflect the new status
        service.status = 'published';
      },
      error: (error) => {
        console.error('Failed to publish service', error);
      }
    });
  }
  goBack(): void {
    this.router.navigate(['/professional-dashboard']);
  }
}
