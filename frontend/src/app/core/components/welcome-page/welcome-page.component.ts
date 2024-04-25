import { Component } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-welcome-page',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './welcome-page.component.html',
  styleUrl: './welcome-page.component.scss'
})
export class WelcomePageComponent {

  constructor(private router: Router) {}
  
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  // Method to navigate to register
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
