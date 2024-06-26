import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CreateEventService } from '../../../../services/create-event.service';
import { AuthService } from '../../../../services/auth.service';
import { MaterialModule } from '../../../../material.module';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { Observable, forkJoin } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [MaterialModule, CommonModule, TranslateModule],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit {
  events: any[] = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = [
    'nombre', 'descripcion', 'fechaEvento', 'duracion', 'ubicacion', 
    'categoria', 'costo', 'estado', 'expandToggle'
  ];

  constructor(
    private createEventService: CreateEventService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.createEventService.getEvents().subscribe({
      next: (response: any) => {
        this.dataSource.data = response.map((event: any) => ({
          ...event,
          isExpanded: false,
          userData: []
        }));
      },
      error: (error) => {
        console.error('Failed to load events', error);
      }
    });
  }

  isExpandedRow = (i: number, element: any) => element.isExpanded;

  toggleRow(element: any): void {
    element.isExpanded = !element.isExpanded;

    if (element.isExpanded && (!element.userData || element.userData.length === 0)) {
      this.fetchUserDetails(element.attendees.user_ids).subscribe(userDetails => {
        element.userData = userDetails;
        this.refreshTable();
      });
    } else {
      this.refreshTable();
    }
  }

  refreshTable() {
    this.dataSource.data = [...this.dataSource.data];
    this.cdr.detectChanges();
  }

  fetchUserDetails(userIds: number[]): Observable<any[]> {
    let requests = userIds.map(id => this.authService.getUserById(id));
    return forkJoin(requests);
  }

  onPublishEvent(event: any): void {
    if (event.status === 'published') {
      return;
    }
    this.createEventService.publishEvent(event.id).subscribe({
      next: () => {
        event.status = 'published';
      },
      error: (error) => {
        console.error('Failed to publish event', error);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/organizer-dashboard']);
  }
}
