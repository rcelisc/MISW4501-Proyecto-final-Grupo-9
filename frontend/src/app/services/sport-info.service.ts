import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SportInfoService {
  private baseUrl = environment.baseUrl;
  private readonly userUrl = `${this.baseUrl}/users`;

  constructor(private http: HttpClient) { }

  createSportInfo(userId: number, sportData: any): Observable<any> {
    const url = `${this.userUrl}/${userId}/sports_habits`;
    const headers = this.createAuthorizationHeader();
    return this.http.post(url, sportData, { headers });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}