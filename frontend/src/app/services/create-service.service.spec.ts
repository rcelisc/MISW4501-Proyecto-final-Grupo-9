import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CreateServiceService } from './create-service.service';

describe('CreateServiceService', () => {
  let service: CreateServiceService;
  let httpMock: HttpTestingController;


  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CreateServiceService]
    });

    service = TestBed.inject(CreateServiceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verify that no unmatched requests are outstanding.
  });

  it('should post data to create a service', () => {
    const serviceData = { name: 'Test Service' };
    const responseMock = { id: 1, name: 'Test Service' };
  
    service.createService(serviceData).subscribe(res => {
      expect(res).toEqual(responseMock);
    });
    const req = httpMock.expectOne(service.getApiUrl());
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(serviceData);
    req.flush(responseMock);
  });

  it('should retrieve all services', () => {
    const mockResponse = { services: [{ id: 1, name: 'Test Service' }] };
  
    service.getServices().subscribe(services => {
      expect(services.length).toBe(1);
      expect(services).toEqual(mockResponse.services);
    });
  
    const req = httpMock.expectOne(service.getApiUrl());
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should retrieve published services and events', () => {
    const mockResponse = { services: [{ id: 1 }], events: [{ id: 100 }] };
  
    service.getServicesPublished().subscribe(result => {
      expect(result.services.length).toBe(1);
      expect(result.events.length).toBe(1);
    });
  
    const req = httpMock.expectOne(`${service.getApiUrl()}/published`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should post to publish a service', () => {
    const serviceId = 1;
    const responseMock = { success: true };
  
    service.publishService(serviceId).subscribe(res => {
      expect(res).toEqual(responseMock);
    });
  
    const req = httpMock.expectOne(`${service.getApiUrl()}/${serviceId}/publish`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush(responseMock);
  });

});
