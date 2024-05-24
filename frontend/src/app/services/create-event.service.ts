import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateEventService {

  private readonly apiUrlCommands = 'http://localhost:3001/events';
  private readonly apiUrlQueries = 'http://localhost:3002/events';
  //private readonly apiUrlCommands = 'https://35.232.6.198/events';
  //private readonly apiUrlQueries = 'https://35.232.6.198/events/get';

  constructor(private http: HttpClient) { }

  getApiUrl(): string {
    return this.apiUrlCommands;
  }

  getApiUrlQueries(): string {
    return this.apiUrlQueries;
  }

  createEvent(eventData: any): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(this.apiUrlCommands, eventData, { headers: headers });
  }

  getEvents(): Observable<any[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<any[]>(`${this.apiUrlQueries}/get`, { headers });
  }

  publishEvent(eventId: number): Observable<any> {
    const headers = this.createAuthorizationHeader();
    return this.http.post(`${this.apiUrlCommands}/${eventId}/publish`, {}, { headers: headers });
  }

  enrollEvent(eventId: number): Observable<any> {
    const headers = this.createAuthorizationHeader();
    return this.http.post(`${this.apiUrlCommands}/${eventId}/add`, {}, { headers: headers });
  }

  getUserEnrolledEvents(userId: number): Observable<any[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<any[]>(`${this.apiUrlQueries}/user/${userId}`, { headers });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
