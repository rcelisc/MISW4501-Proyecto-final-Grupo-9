import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { CreateEventService } from '../../../../services/create-event.service'
import { AuthService } from '../../../../services/auth.service';
import { MaterialModule } from '../../../../shared/material.module';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [MaterialModule, CommonModule],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss'],
})


export class EventListComponent implements OnInit {
  events: any[] = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = ['nombre', 'descripcion', 'fechaEvento', 'duracion', 'ubicacion', 'categoria', 'costo', 'estado', 'expandToggle'];

  constructor(
    private createEventService: CreateEventService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.createEventService.getEvents().subscribe({
      next: (response:any) => {
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
    console.log('toggleRow', element);
    element.isExpanded = !element.isExpanded;
  
    if (element.isExpanded && (!element.userData || element.userData.length === 0)) {
      this.fetchUserDetails(element.attendees.user_ids).subscribe(userDetails => {
        element.userData = userDetails;
        this.refreshTable();
        console.log('User data fetched and set:', element.userData);
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
}
