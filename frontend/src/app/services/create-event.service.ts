import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateEventService {

  private readonly apiUrlCommands = 'http://localhost:3001/events';
  private readonly apiUrlQueries = 'http://localhost:3002/events/get';

  constructor(private http: HttpClient) { }

  getApiUrl(): string {
    return this.apiUrlCommands;
  }

  getApiUrlQueries(): string {
    return this.apiUrlQueries;
  }

  createEvent(eventData: any): Observable<any> {
    return this.http.post(this.apiUrlCommands, eventData);
  }

  getEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrlQueries}`);
  }

  publishEvent(eventId: number): Observable<any> {
    return this.http.post(`${this.apiUrlCommands}/${eventId}/publish`, {});
  }

  enrollEvent(eventId: number, userId: number = 1): Observable<any> { // Default userId to 1
    const payload = { user_id: userId };
    return this.http.post(`${this.apiUrlCommands}/${eventId}/add`, payload);
  }
}
