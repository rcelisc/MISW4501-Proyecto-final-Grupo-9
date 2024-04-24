import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { CreateServiceService } from '../../../../services/create-service.service'
import { MaterialModule } from '../../../../material.module';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [MaterialModule],
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
      next: (response:any) => {
        // Assuming the response format is the one shown above
        this.dataSource.data = response.services; // Accessing the 'services' array directly
      },
      error: (error) => {
        console.error('Failed to load services', error);
        // Optionally, handle the error, e.g., show a snackbar message
      }
    });
  }

  onPublishService(service: any): void {
    if (service.status === 'published') {
      // Service is already published, do nothing
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
}
