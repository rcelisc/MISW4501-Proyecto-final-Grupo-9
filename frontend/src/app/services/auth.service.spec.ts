import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to register a user', () => {
    const userData = { /* mock user data */ };
    const mockResponse = { /* mock response data */ };

    service.registerUser(userData).subscribe(response => {
      expect(response).toEqual(mockResponse); // Check if response matches mock response
    });

    const req = httpMock.expectOne('https://mysportapp.duckdns.org/users');
    expect(req.request.method).toBe('POST'); // Check if POST request is sent
    req.flush(mockResponse); // Simulate server response
  });
});
