import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SportInfoService } from '../../../../services/sport-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-sport-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './sport-info.component.html',
  styleUrl: './sport-info.component.scss'
})
export class SportInfoComponent implements OnInit{
  sportInfoForm: FormGroup;
  userId: number = 0;
  constructor(
    private fb: FormBuilder,
    private sportInfoService: SportInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authService: AuthService
  ){
    this.sportInfoForm = this.fb.group({
      training_frequency: [''],
      sports_practiced: [''],
      average_session_duration: [''],
      recovery_time : [''],
      training_pace: [''],
    });
  }
  
  ngOnInit(): void {
    this.setUserIdFromToken();
  }

  setUserIdFromToken() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.userId = decodedToken.user_id;
      } else {
        console.error('Token is invalid or expired');
        this.router.navigate(['/login']); // Redirect to login if the token is invalid or expired
      }
    } else {
      this.router.navigate(['/login']); // Redirect to login if there's no token
    }
  }
  
  onSubmit(): void {
    // Trigger validation for all form fields
    this.sportInfoForm.markAllAsTouched();

    if (!this.sportInfoForm.valid) {
      this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error'] 
      });
      return; 
    }

    this.sportInfoService.createSportInfo(this.userId, this.sportInfoForm.value).subscribe({
      next: (response) => {
        this.snackBar.open('Informacion deportiva agregada exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/athlete-dashboard']);
      },
      error: (error) => {
        this.snackBar.open('Error al agregar informacion deportiva del usuario', 'Cerrar', { duration: 3000 });
      }
    });
  }
}
