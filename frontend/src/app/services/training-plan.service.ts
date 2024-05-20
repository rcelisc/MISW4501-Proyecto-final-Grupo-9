import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { TrainingPlanRequest, TrainingPlanResponse } from '../models/plan-training.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TrainingPlanService {

  private baseCommandsUrl: string;
  private baseQueriesUrl: string;

  constructor(private http: HttpClient) {
    this.baseCommandsUrl = `${environment.baseUrl}/training-plan`;
    this.baseQueriesUrl = `${environment.baseUrl}`;
  }

  createPlan(plan: TrainingPlanRequest): Observable<TrainingPlanResponse> {
    const headers = this.createAuthorizationHeader();
    return this.http.post<TrainingPlanResponse>(this.baseCommandsUrl, plan, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  getTrainingSessions(): Observable<any[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<any[]>(`${this.baseQueriesUrl}/training-sessions`, { headers });
  }

  getTrainingSessionByUser(userId: number): Observable<any[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<any[]>(`${this.baseQueriesUrl}/training-sessions/user/${userId}`, { headers });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  private handleError(error: any) {
    console.error('Error in request:', error);
    return throwError(() => new Error('Error in request. Please try again later.'));
  }
}
