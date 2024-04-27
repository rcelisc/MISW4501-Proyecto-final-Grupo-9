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
    this.baseCommandsUrl = `${environment.baseUrlCommandsTraining}/training-plan`;
    this.baseQueriesUrl = `${environment.baseUrlQueriesTraining}`;
  }

  createPlan(plan: TrainingPlanRequest): Observable<TrainingPlanResponse> {
    return this.http.post<TrainingPlanResponse>(this.baseCommandsUrl, plan)
      .pipe(
        catchError(this.handleError)
      );
  }

  getTrainingSessions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseQueriesUrl}/training-sessions`);
  }

  private handleError(error: any) {
    console.error('Error en la solicitud:', error);
    return throwError('Error en la solicitud. Por favor, inténtalo de nuevo más tarde.'); // O puedes lanzar un error personalizado
  }
}