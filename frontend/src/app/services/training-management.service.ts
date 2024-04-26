import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrainingManagementService {

  private readonly baseUrl = 'http://localhost:3004/training-plan';

  constructor(private http: HttpClient) { }

  createTrainingPlan(trainingData: any): Observable<any> {
    const url = `${this.baseUrl}`;
    return this.http.post(url, trainingData );
  }
}