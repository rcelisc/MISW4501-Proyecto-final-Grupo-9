import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from "jwt-decode";




@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:3006/';
  private baseUrlQueries = 'http://localhost:3007/';
  //private baseUrl = 'https://35.232.6.198/users';

  constructor(private http: HttpClient) {}

  decodeToken(token: string): any | null {
    try {
      const decodedToken = jwtDecode<any>(token);
      console.log('Decoded token:', decodedToken);
      return jwtDecode<any>(token);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }
  registerUser(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users`, userData);
  }

  loginUser(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, userData);
  }

  getUserById(userId: number) {
    return this.http.get<any>(`${this.baseUrlQueries}users/${userId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
  }
}
