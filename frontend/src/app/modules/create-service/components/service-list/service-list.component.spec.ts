import { ComponentFixture, TestBed, fakeAsync, flush, tick } from '@angular/core/testing';
import { ServiceListComponent } from './service-list.component';
import { CreateServiceService } from '../../../../services/create-service.service';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { of, throwError } from 'rxjs';

describe('ServiceListComponent', () => {
  let component: ServiceListComponent;
  let fixture: ComponentFixture<ServiceListComponent>;
  let mockCreateServiceService: jasmine.SpyObj<CreateServiceService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockCreateServiceService = jasmine.createSpyObj('CreateServiceService', ['getServices', 'publishService']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, MaterialModule, MatTableModule, ServiceListComponent],
      providers: [
        { provide: CreateServiceService, useValue: mockCreateServiceService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ServiceListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load services on init', () => {
    const mockServices = [
      { id: 1, name: 'RUMBA', description: 'RUMBON', rate: 123000.0, status: 'published' },
      { id: 2, name: 'BAILOTERAPIA', description: 'BAILOTEO ASESINO', rate: 12000.0, status: 'published' },
    ];
    mockCreateServiceService.getServices.and.returnValue(of(mockServices));
  
    component.ngOnInit();
    fixture.detectChanges();  // This triggers ngOnInit
  
    expect(component.dataSource.data).toEqual(mockServices);
  });

  it('should handle errors when loading services fails', () => {
    const consoleSpy = spyOn(console, 'error'); // Spy on console.error
    mockCreateServiceService.getServices.and.returnValue(throwError(() => new Error('Failed to load')));
    fixture.detectChanges();
    expect(consoleSpy).toHaveBeenCalledWith('Failed to load services', jasmine.any(Error));
  });

  it('should not publish service if already published', () => {
    let service = { id: 1, name: 'Web Development', description: 'test', rate: '10', status: 'published' };
    component.onPublishService(service);
    expect(mockCreateServiceService.publishService).not.toHaveBeenCalled();  // Ensure this method is mocked even if not called
  });

  it('should update service status to published on successful publish', fakeAsync(() => {
    let service = { id: 2, name: 'Graphic Design', description: 'test', rate: '10', status: 'created' };
    mockCreateServiceService.publishService.and.returnValue(of({}));
    component.onPublishService(service);
    tick(5000);  // Simulate the passage of time until all async operations are complete
    expect(service.status).toEqual('published');
    expect(mockCreateServiceService.publishService).toHaveBeenCalledWith(service.id);
  }));
  

  it('should handle errors during service publishing', () => {
    let service = { id: 3, name: 'SEO Optimization',description:'test', rate:'10', estado: 'created' };
    const consoleSpy = spyOn(console, 'error');
    // Mock return of an observable with error
    mockCreateServiceService.publishService.and.returnValue(throwError(() => new Error('Publish failed')));
    component.onPublishService(service);
    expect(consoleSpy).toHaveBeenCalledWith('Failed to publish service', jasmine.any(Error));
  });
});
