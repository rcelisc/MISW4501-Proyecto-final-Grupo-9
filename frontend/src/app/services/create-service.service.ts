import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateServiceService {

  private readonly apiUrl = 'http://localhost:3005/services';
  //private readonly apiUrl = 'https://35.232.6.198/services';

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

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

}
