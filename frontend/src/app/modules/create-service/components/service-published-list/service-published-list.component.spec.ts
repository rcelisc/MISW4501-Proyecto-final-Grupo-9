import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServicePublishedListComponent } from './service-published-list.component';
import { CreateServiceService } from '../../../../services/create-service.service';
import { CreateEventService } from '../../../../services/create-event.service';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { AuthService } from '../../../../services/auth.service';

describe('ServicePublishedListComponent', () => {
  let component: ServicePublishedListComponent;
  let fixture: ComponentFixture<ServicePublishedListComponent>;
  let mockCreateServiceService: jasmine.SpyObj<CreateServiceService>;
  let mockCreateEventService: jasmine.SpyObj<CreateEventService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let router: Router;
  let translateService: TranslateService;

  beforeEach(async () => {
    const createServiceServiceSpy = jasmine.createSpyObj('CreateServiceService', ['getPublishedItemsWithUserStatus', 'purchaseService']);
    const createEventServiceSpy = jasmine.createSpyObj('CreateEventService', ['enrollEvent']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['decodeToken']);

    await TestBed.configureTestingModule({
      imports: [
        MatTableModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        RouterTestingModule.withRoutes([]),
        MaterialModule,
        ServicePublishedListComponent
      ],
      providers: [
        { provide: CreateServiceService, useValue: createServiceServiceSpy },
        { provide: CreateEventService, useValue: createEventServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        TranslateService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePublishedListComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    mockCreateServiceService = TestBed.inject(CreateServiceService) as jasmine.SpyObj<CreateServiceService>;
    mockCreateEventService = TestBed.inject(CreateEventService) as jasmine.SpyObj<CreateEventService>;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    translateService = TestBed.inject(TranslateService);

    // Mock the translations
    spyOn(translateService, 'instant').and.callFake((key: string) => {
      const translations: { [key: string]: string } = {
        'listServicesPublishedTitle': 'Published Services',
        'listServicesPublishedName': 'Name',
        'listServicesPublishedDescription': 'Description',
        'listServicesPublishedRate': 'Rate',
        'purchaseButton': 'Purchase',
        'purchasedLabel': 'Purchased',
        'listEventsPublishedTitle': 'Published Events',
        'listEventsPublishedName': 'Name',
        'listEventsPublishedDescription': 'Description',
        'listEventPublishedRate': 'Rate',
        'listEventsPublishedEventDate': 'Event Date',
        'enrollButton': 'Enroll',
        'enrolledLabel': 'Enrolled'
      };
      return translations[key] || key;
    });

    // Mock token and user ID decoding
    const mockToken = 'mock-token';
    const decodedToken = { user_id: 1 };
    localStorage.setItem('token', mockToken);
    mockAuthService.decodeToken.and.returnValue(decodedToken);

    mockCreateServiceService.getPublishedItemsWithUserStatus.and.returnValue(of({ services: [], events: [] }));

    fixture.detectChanges(); // ngOnInit() will be called here
  });

  afterEach(() => {
    // Clean up localStorage
    localStorage.removeItem('token');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadPublishedItems on initialization', () => {
    spyOn(component, 'loadPublishedItems').and.callThrough();
    component.ngOnInit();
    expect(component.loadPublishedItems).toHaveBeenCalled();
  });

  it('should load published services and events correctly', () => {
    const mockPublishedItems = { services: [{ id: 1, name: 'Service One' }], events: [{ id: 1, name: 'Event One' }] };
    mockCreateServiceService.getPublishedItemsWithUserStatus.and.returnValue(of(mockPublishedItems));
    component.loadPublishedItems();
    fixture.detectChanges();

    expect(component.servicesDataSource.data).toEqual(mockPublishedItems.services);
    expect(component.eventsDataSource.data).toEqual(mockPublishedItems.events);
  });

  it('should handle event enrollment correctly', () => {
    const mockEventId = 1;
    mockCreateEventService.enrollEvent.and.returnValue(of({ message: 'Enrollment successful' }));
    component.eventsDataSource.data = [{ id: 1, name: 'Event One', enrolled: false }];

    component.enrollEvent(mockEventId);
    expect(mockCreateEventService.enrollEvent).toHaveBeenCalledWith(mockEventId);
    expect(component.eventsDataSource.data.find(e => e.id === mockEventId).enrolled).toBeTrue();
  });

  it('should mark a service as purchased', () => {
    component.userId = 1;
    const service = { id: 1, name: 'Service One', purchased: false };
    component.servicesDataSource.data = [service];
    mockCreateServiceService.purchaseService.and.returnValue(of({ message: 'Purchase successful' }));

    component.purchaseService(1);
    expect(mockCreateServiceService.purchaseService).toHaveBeenCalledWith(1, component.userId);
    expect(service.purchased).toBeTrue();
    expect(component.servicesDataSource.data).toContain(jasmine.objectContaining({ id: 1, purchased: true }));
  });

  it('should log an error if loading services and events fails', () => {
    const consoleSpy = spyOn(console, 'error');
    mockCreateServiceService.getPublishedItemsWithUserStatus.and.returnValue(throwError(() => new Error('Failed to load')));

    component.loadPublishedItems();
    fixture.detectChanges();
    expect(consoleSpy).toHaveBeenCalledWith('Failed to load services and events', jasmine.any(Error));
  });

});
