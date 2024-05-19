import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from "jwt-decode";
import { environment } from '../../environments/environment';




@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.baseUrl;

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

  logoutUser(userId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/logout`, {user_id: userId}, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
  }

  getUserById(userId: number) {
    return this.http.get<any>(`${this.baseUrl}/users/${userId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
  }

  getUsers(userType: string): Observable<any> {
    return this.http.get<any[]>(`${this.baseUrl}/users/get?type=${userType}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
  }
}
