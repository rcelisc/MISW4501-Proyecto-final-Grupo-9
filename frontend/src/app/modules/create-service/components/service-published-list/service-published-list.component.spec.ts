import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServicePublishedListComponent } from './service-published-list.component';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CreateServiceService } from '../../../../services/create-service.service';
import { CreateEventService } from '../../../../services/create-event.service';
import { of, throwError } from 'rxjs';

describe('ServicePublishedListComponent', () => {
  let component: ServicePublishedListComponent;
  let fixture: ComponentFixture<ServicePublishedListComponent>;
  let mockCreateServiceService: jasmine.SpyObj<CreateServiceService>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;

  beforeEach(async () => {
    mockCreateServiceService = jasmine.createSpyObj('CreateServiceService', ['getServicesPublished']);
    mockCreateEventService = jasmine.createSpyObj('CreateEventService', ['enrollEvent']);

    await TestBed.configureTestingModule({
      imports: [MatTableModule, BrowserAnimationsModule, ServicePublishedListComponent],
      providers: [
        { provide: CreateServiceService, useValue: mockCreateServiceService },
        { provide: CreateEventService, useValue: mockCreateEventService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePublishedListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadPublishedItems on initialization', () => {
    spyOn(component, 'loadPublishedItems');
    fixture.detectChanges(); // triggers ngOnInit
    expect(component.loadPublishedItems).toHaveBeenCalled();
  });
  
  it('should load published services and events correctly', () => {
    const mockServices = { services: [{ id: 1, name: 'Service One' }], events: [{ id: 1, name: 'Event One' }] };
    mockCreateServiceService.getServicesPublished.and.returnValue(of(mockServices));
    fixture.detectChanges(); // triggers loadPublishedItems through ngOnInit
  
    expect(component.servicesDataSource.data).toEqual(mockServices.services);
    expect(component.eventsDataSource.data).toEqual(mockServices.events);
  });

  it('should handle event enrollment correctly', () => {
    const mockEventId = 1;
    mockCreateEventService.enrollEvent.and.returnValue(of({ message: 'Enrollment successful' }));
    component.eventsDataSource.data = [{ id: 1, name: 'Event One', enrolled: false }];
  
    component.enrollEvent(mockEventId);
    expect(mockCreateEventService.enrollEvent).toHaveBeenCalledWith(mockEventId, 3);
    expect(component.eventsDataSource.data.find(e => e.id === mockEventId).enrolled).toBeTrue();
  });

  it('should mark a service as purchased', () => {
    const service = { id: 1, name: 'Service One', purchased: false };
    component.servicesDataSource.data = [service];
  
    component.purchaseService(service);
    expect(service.purchased).toBeTrue();
    expect(component.servicesDataSource.data).toContain(jasmine.objectContaining({ id: 1, purchased: true }));
  });

  it('should log an error if loading services and events fails', () => {
    const consoleSpy = spyOn(console, 'error');
    mockCreateServiceService.getServicesPublished.and.returnValue(throwError(() => new Error('Failed to load')));
  
    fixture.detectChanges(); // triggers ngOnInit and loadPublishedItems
    expect(consoleSpy).toHaveBeenCalledWith('Failed to load services and events', jasmine.any(Error));
  });

});
