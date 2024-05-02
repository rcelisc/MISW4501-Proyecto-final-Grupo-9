import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DemographicInfoService {

  //private readonly baseUrl = 'http://localhost:3006/users';
  private readonly baseUrl = 'http://35.232.6.198/users';

  constructor(private http: HttpClient) { }

  createDemographicInfo(userId: number, demographicData: any): Observable<any> {
    const url = `${this.baseUrl}/${userId}/demographic_data`;
    return this.http.post(url, demographicData);
  }
}
