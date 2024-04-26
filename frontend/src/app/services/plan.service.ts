import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlanService {
  private baseUrl = 'http://localhost:3006/users';

  constructor(private http: HttpClient) {}

  updatePlan(userId: number, plan_type: string): Observable<any> {
    const url = `${this.baseUrl}/${userId}/plan`;
    return this.http.post(url, { plan_type });
  }
}
