import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout',
  standalone: true,
  imports: [],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.scss'
})
export class LogoutComponent {
  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return;
    }
    const decodedToken = this.authService.decodeToken(token);
    const userId = decodedToken.user_id; // Implement this method to retrieve the stored user ID
    this.authService.logoutUser(userId).subscribe({
      next: (response) => {
        console.log('Logged out successfully');
        this.clearLocalStorage(); // Clear the token and other data from local storage
        this.router.navigate(['/login']); // Redirect to login page
      },
      error: (error) => {
        console.error('Logout failed', error);
      }
    });
  }

  clearLocalStorage() {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.clear();
  }

}
