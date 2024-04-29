import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateServiceService {

  private readonly apiUrl = 'http://localhost:3005/services';

  constructor(private http: HttpClient) { }

  createService(serviceData: any): Observable<any> {
    return this.http.post(this.apiUrl, serviceData);
  }

  getServices(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  getServicesPublished(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/published`);
  }

  publishService(serviceId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${serviceId}/publish`, {});
  }

}
