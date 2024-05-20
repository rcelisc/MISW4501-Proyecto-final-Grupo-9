import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DemographicInfoService {

  private baseUrl = environment.baseUrl;
  private readonly userUrl = `${this.baseUrl}/users`;

  constructor(private http: HttpClient) { }

  createDemographicInfo(userId: number, demographicData: any): Observable<any> {
    const url = `${this.userUrl}/${userId}/demographic_data`;
    const headers = this.createAuthorizationHeader();
    return this.http.post(url, demographicData, { headers });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
