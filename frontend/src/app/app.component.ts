import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'SportApp';showLogoutButton: boolean = false;

  constructor(private router: Router, private authService: AuthService) {
    // Listen to routing changes
    this.router.events.subscribe(() => {
      // Determine if the logout button should be displayed
      this.showLogoutButton = !['/', '/login', '/register'].includes(this.router.url);
    });
  }

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