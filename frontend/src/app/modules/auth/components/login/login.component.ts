import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../shared/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.loginForm = this.fb.group({
      email: '',
      password: ''
    });
  }

  onLogin() {
    // Here goes the authentication logic
    // After login, route based on user type
    this.router.navigate(['/professional-dashboard']); // Just an example path

    //this.router.navigate(['/plan-entrenamiento']); // Just an example path
    //this.router.navigate(['/meal-plan']); // Just an example path
  }
}
