import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FoodInfoService {

  //private readonly baseUrl = 'http://localhost:3006/users';
  private readonly baseUrl = 'https://35.232.6.198/users';

  constructor(private http: HttpClient) { }

  createFoodInfo(userId: number, foodData: any): Observable<any> {
    const url = `${this.baseUrl}/${userId}/food_data`;
    return this.http.post(url, foodData);
  }
}
