import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map, forkJoin } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateServiceService {

  private readonly apiUrl = 'http://localhost:3005/services';
  //private readonly apiUrl = 'https://35.232.6.198/services';
  private readonly eventsApiUrl = 'http://localhost:3002/events';

  constructor(private http: HttpClient) { }
  
  public getApiUrl(): string {
    return this.apiUrl;
  }

  createService(serviceData: any): Observable<any> {
    return this.http.post(this.apiUrl, serviceData, { headers: this.createAuthorizationHeader() });
  }

  getServices(): Observable<any[]> {
    return this.http.get<{ services: any[] }>(this.apiUrl, { headers: this.createAuthorizationHeader() }).pipe(
      map(response => response.services)
    );
  }

  getServicesPublished(): Observable<{ services: any[], events: any[] }> {
    return this.http.get<{ services: any[], events: any[] }>(`${this.apiUrl}/published`, { headers: this.createAuthorizationHeader() });
  }

  publishService(serviceId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${serviceId}/publish`, {}, { headers: this.createAuthorizationHeader() });
  }

  purchaseService(serviceId: number, userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${serviceId}/purchase`, { user_id: userId }, { headers: this.createAuthorizationHeader() });
  }

  getPurchasedServices(userId: number): Observable<any[]> {
    return this.http.get<{ services: any[] }>(`${this.apiUrl}/user/${userId}`, { headers: this.createAuthorizationHeader() }).pipe(
      map(response => response.services)
    );
  }

  getPublishedItemsWithUserStatus(userId: number): Observable<any> {
    const servicesUrl = `${this.apiUrl}/published`;
    const userServicesUrl = `${this.apiUrl}/user/${userId}`;
    const userEventsUrl = `${this.eventsApiUrl}/user/${userId}`;

    return forkJoin({
      publishedItems: this.http.get<{ services: any[], events: any[] }>(servicesUrl, { headers: this.createAuthorizationHeader() }),
      userServices: this.http.get<any[]>(userServicesUrl, { headers: this.createAuthorizationHeader() }),
      userEvents: this.http.get<any[]>(userEventsUrl, { headers: this.createAuthorizationHeader() })
    }).pipe(
      map(results => {
        // Assuming services and events are separate but structured similarly
        const { services, events } = results.publishedItems;

        services.forEach(service => {
          service.purchased = results.userServices.some(us => us.id === service.id);
        });

        events.forEach(event => {
          event.enrolled = results.userEvents.some(ue => ue.id === event.id);
        });

        return { services, events };
      })
    );
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

}
