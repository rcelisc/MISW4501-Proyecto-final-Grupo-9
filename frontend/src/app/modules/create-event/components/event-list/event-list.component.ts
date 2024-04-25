import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { CreateEventService } from '../../../../services/create-event.service'
import { MaterialModule } from '../../../../material.module';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [MaterialModule, CommonModule],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.scss'
})


export class EventListComponent implements OnInit {
  events: any[] = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = ['nombre', 'descripcion', 'fechaEvento', 'duracion', 'ubicacion', 'categoria', 'costo', 'estado'];

  constructor(
    private createEventService: CreateEventService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.createEventService.getEvents().subscribe({
      next: (response:any) => {
        // Assuming the response format is the one shown above
        this.dataSource.data = response;
      },
      error: (error) => {
        console.error('Failed to load events', error);
        // Optionally, handle the error, e.g., show a snackbar message
      }
    });
  }

  onPublishEvent(event: any): void {
    if (event.status === 'published') {
      // Service is already published, do nothing
      return;
    }
    this.createEventService.publishEvent(event.id).subscribe({
      next: () => {
        // Update the local event state to reflect the new status
        event.status = 'published';
      },
      error: (error) => {
        console.error('Failed to publish event', error);
      }
    });
  }
}
