import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PlanService {
  private baseUrl = environment.baseUrlCommandsUser;

  constructor(private http: HttpClient) {}

  updatePlan(userId: number, plan_type: string): Observable<any> {
    const url = `${this.baseUrl}/users/${userId}/plan`;
    const headers = this.createAuthorizationHeader();
    return this.http.post(url, { plan_type }, { headers });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
