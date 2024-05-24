import { ComponentFixture, TestBed, fakeAsync, flush, tick } from '@angular/core/testing';
import { EventListComponent } from './event-list.component';
import { CreateEventService } from '../../../../services/create-event.service';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from '../../../../services/auth.service';

describe('EventListComponent', () => {
  let component: EventListComponent;
  let fixture: ComponentFixture<EventListComponent>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    mockCreateEventService = jasmine.createSpyObj('CreateEventService', ['getEvents', 'publishEvent']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['getUserById']);

    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MaterialModule,
        MatTableModule,
        TranslateModule.forRoot(),
        HttpClientTestingModule
      ],
      providers: [
        { provide: CreateEventService, useValue: mockCreateEventService },
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load events on init', fakeAsync(() => {
    const mockEvents = [{ id: 1, name: 'Music Festival', status: 'unpublished' }];
    const expectedEvents = mockEvents.map(event => ({
      ...event,
      isExpanded: false,
      userData: []
    }));
    mockCreateEventService.getEvents.and.returnValue(of(mockEvents));

    component.ngOnInit();
    fixture.detectChanges(); // This triggers ngOnInit

    flush(); // Complete all pending asynchronous activities

    expect(component.dataSource.data).toEqual(expectedEvents);
    expect(mockCreateEventService.getEvents).toHaveBeenCalled();
  }));

  it('should handle errors when loading events fails', fakeAsync(() => {
    const consoleSpy = spyOn(console, 'error'); // Spy on console.error
    mockCreateEventService.getEvents.and.returnValue(throwError(() => new Error('Failed to load')));

    component.ngOnInit();
    fixture.detectChanges(); // This triggers ngOnInit

    flush(); // Complete all pending asynchronous activities

    expect(consoleSpy).toHaveBeenCalledWith('Failed to load events', jasmine.any(Error));
  }));

  it('should update event status to published on successful publish', fakeAsync(() => {
    const event = { id: 2, name: 'Tech Expo', status: 'unpublished', isExpanded: false, userData: [] };
    mockCreateEventService.publishEvent.and.returnValue(of({}));

    component.onPublishEvent(event);
    tick(); // Simulate the passage of time until all async operations are complete

    expect(event.status).toEqual('published');
    expect(mockCreateEventService.publishEvent).toHaveBeenCalledWith(event.id);
  }));

  it('should handle errors during event publishing', fakeAsync(() => {
    const event = { id: 3, name: 'Health Conference', status: 'unpublished', isExpanded: false, userData: [] };
    const consoleSpy = spyOn(console, 'error');
    mockCreateEventService.publishEvent.and.returnValue(throwError(() => new Error('Publish failed')));

    component.onPublishEvent(event);
    tick(); // Simulate the passage of time until all async operations are complete

    expect(consoleSpy).toHaveBeenCalledWith('Failed to publish event', jasmine.any(Error));
  }));
});
