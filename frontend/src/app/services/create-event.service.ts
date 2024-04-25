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

  createEvent(eventData: any): Observable<any> {
    return this.http.post(this.apiUrlCommands, eventData);
  }

  getEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrlQueries}`);
  }

  publishEvent(eventId: number): Observable<any> {
    return this.http.post(`${this.apiUrlCommands}/${eventId}/publish`, {});
  }
}
