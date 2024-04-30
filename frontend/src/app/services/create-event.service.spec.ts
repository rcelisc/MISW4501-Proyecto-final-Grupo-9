import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CreateEventService } from './create-event.service';

describe('CreateEventService', () => {
  let service: CreateEventService;
  let httpMock: HttpTestingController;


  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CreateEventService]
    });

    service = TestBed.inject(CreateEventService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verify that no unmatched requests are outstanding.
  });

  it('should post data to create a service', () => {
    const eventData = { name: 'Test Service' };
    const responseMock = { id: 1, name: 'Test Service' };
  
    service.createEvent(eventData).subscribe(res => {
      expect(res).toEqual(responseMock);
    });
    const req = httpMock.expectOne(service.getApiUrl());
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(eventData);
    req.flush(responseMock);
  });


  it('should post to publish a event', () => {
    const eventId = 1;
    const responseMock = { success: true };
  
    service.publishEvent(eventId).subscribe(res => {
      expect(res).toEqual(responseMock);
    });
  
    const req = httpMock.expectOne(`${service.getApiUrl()}/${eventId}/publish`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush(responseMock);
  });

});
