import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { TrainingPlanRequest, TrainingPlanResponse } from '../models/plan-training.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class TrainingPlanService {

  private baseUrl: string;

  constructor(private http: HttpClient) {
    this.baseUrl = `${environment.baseUrlTraining}/training-plan`;
  }

  createPlan(plan: TrainingPlanRequest): Observable<TrainingPlanResponse> {
    return this.http.post<TrainingPlanResponse>(this.baseUrl, plan)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: any) {
    // Aquí puedes agregar código para manejar el error, como enviarlo a un servicio de registro de errores, mostrar un mensaje de error al usuario, etc.
    console.error('Error en la solicitud:', error);
    return throwError('Error en la solicitud. Por favor, inténtalo de nuevo más tarde.'); // O puedes lanzar un error personalizado
  }
}