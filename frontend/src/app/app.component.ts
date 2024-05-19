import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { MaterialModule } from './material.module';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SharedModule } from './shared/shared.module';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterModule, MaterialModule, SharedModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'SportApp';
  showLogoutButton: boolean = false;

  constructor(private router: Router, private authService: AuthService, private translate: TranslateService) {
    // Listen to routing changes
    this.router.events.subscribe(() => {
      // Determine if the logout button should be displayed
      this.showLogoutButton = !['/', '/login', '/register'].includes(this.router.url);
    });

    // Set default language
    this.translate.setDefaultLang('en');
  }

  switchLanguage(language: string) {
    this.translate.use(language);
  }

  handleLogoClick() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'role' in decodedToken) {
        switch (decodedToken.role) {
          case 'athlete':
            this.router.navigate(['/athlete-dashboard']);
            break;
          case 'event_organizer':
            this.router.navigate(['/organizer-dashboard']);
            break;
          case 'complementary_services_professional':
            this.router.navigate(['/professional-dashboard']);
            break;
          default:
            this.router.navigate(['/']);
            break;
        }
      } else {
        this.router.navigate(['/']);
      }
    } else {
      this.router.navigate(['/']);
    }
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
