import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SportInfoService {

  private readonly baseUrl = 'http://localhost:3006/users';

  constructor(private http: HttpClient) { }

  createSportInfo(userId: number, sportData: any): Observable<any> {
    const url = `${this.baseUrl}/${userId}/sports_habits`;
    return this.http.post(url, sportData );
  }
}
