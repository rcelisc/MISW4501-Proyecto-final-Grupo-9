import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { EventListComponent } from './event-list.component';
import { CreateEventService } from '../../../../services/create-event.service';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { of, throwError } from 'rxjs';

describe('EventListComponent', () => {
  let component: EventListComponent;
  let fixture: ComponentFixture<EventListComponent>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockCreateEventService = jasmine.createSpyObj('CreateEventService', ['getEvents', 'publishEvent']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, MaterialModule, MatTableModule, EventListComponent],
      providers: [
        { provide: CreateEventService, useValue: mockCreateEventService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load events on init', () => {
    const mockEvents = [{id: 1, name: 'Music Festival', status: 'unpublished'}];
    mockCreateEventService.getEvents.and.returnValue(of(mockEvents));
    fixture.detectChanges(); // ngOnInit is called here
    expect(component.dataSource.data).toEqual(mockEvents);
    expect(mockCreateEventService.getEvents).toHaveBeenCalled();
  });

  it('should handle errors when loading events fails', () => {
    const consoleSpy = spyOn(console, 'error'); // Spy on console.error
    mockCreateEventService.getEvents.and.returnValue(throwError(() => new Error('Failed to load')));
    fixture.detectChanges();
    expect(consoleSpy).toHaveBeenCalledWith('Failed to load events', jasmine.any(Error));
  });

  it('should update event status to published on successful publish', fakeAsync(() => {
    let event = {id: 1, name: 'Tech Expo', status: 'unpublished'};
    mockCreateEventService.publishEvent.and.returnValue(of({}));
    component.onPublishEvent(event);
    tick();
    expect(event.status).toEqual('published');
    expect(mockCreateEventService.publishEvent).toHaveBeenCalledWith(event.id);
  }));

  it('should handle errors during event publishing', () => {
    let event = {id: 2, name: 'Health Conference', status: 'unpublished'};
    const consoleSpy = spyOn(console, 'error');
    mockCreateEventService.publishEvent.and.returnValue(throwError(() => new Error('Publish failed')));
    component.onPublishEvent(event);
    expect(consoleSpy).toHaveBeenCalledWith('Failed to publish event', jasmine.any(Error));
  });
});
