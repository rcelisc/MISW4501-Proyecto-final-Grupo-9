import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../shared/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ){
    this.loginForm = this.fb.group({
      id_number: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onLogin() {
    this.authService.loginUser(this.loginForm.value).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token); // Store token in localStorage
        const decoded = this.authService.decodeToken(res.token);
        localStorage.setItem('userRole', decoded.role); // Store user role
        
        // Navigate based on role
        if (decoded.role === 'athlete') {
          this.router.navigate(['/athlete-dashboard']);
        } else if (decoded.role === 'event_organizer') {
          this.router.navigate(['/organizer-dashboard']);
        } else if (decoded.role === 'complementary_services_professional') {
          this.router.navigate(['/professional-dashboard']);
        } else {
          this.router.navigate(['/login']); // Redirect if the role is not recognized
        }
      },
      error: (err) => {
        console.error('Login failed:', err);
      }
    });
  }
  goBack(): void {
    this.router.navigate(['/']);
  }
}