import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NutritionPlanService {
  
  private baseUrl: string;

  constructor(private http: HttpClient) { 
    this.baseUrl = `${environment.baseUrlNutrition}/nutrition-plans`;
  }

  createMealPlan(mealPlanData: any): Observable<any> {
    return this.http.post(this.baseUrl, mealPlanData);
  }

}
